package kr.co.iteyes.oauth.tokenstore;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.oauth2.common.*;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.common.util.SerializationUtils;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

/**
 * Implementation of token services that stores tokens in a database.
 *
 * <p>
 *
 * @author Ken Dombeck
 * @author Luke Taylor
 * @author Dave Syer
 * @deprecated See the <a href="https://github.com/spring-projects/spring-security/wiki/OAuth-2.0-Migration-Guide">OAuth 2.0 Migration Guide</a> for Spring Security 5.
 */
@Deprecated
public class JdbcTokenStore implements TokenStore {
    private static final BytesKeyGenerator DEFAULT_TOKEN_GENERATOR = KeyGenerators.secureRandom(20);

    private static final Charset US_ASCII = Charset.forName("US-ASCII");

    private static final Log LOG = LogFactory.getLog(JdbcTokenStore.class);

    private static final String DEFAULT_ACCESS_TOKEN_INSERT_STATEMENT = "insert into oauth_access_token (token_id, token, authentication_id, user_name, client_id, authentication, refresh_token) values (?, ?, ?, ?, ?, ?, ?)";

    private static final String DEFAULT_ACCESS_TOKEN_SELECT_STATEMENT = "select token_id, token from oauth_access_token where token_id = ?";

    private static final String DEFAULT_ACCESS_TOKEN_AUTHENTICATION_SELECT_STATEMENT = "select token_id, authentication from oauth_access_token where token_id = ?";

    private static final String DEFAULT_ACCESS_TOKEN_FROM_AUTHENTICATION_SELECT_STATEMENT = "select token_id, token from oauth_access_token where authentication_id = ?";

    private static final String DEFAULT_ACCESS_TOKENS_FROM_USERNAME_AND_CLIENT_SELECT_STATEMENT = "select token_id, token from oauth_access_token where user_name = ? and client_id = ?";

    private static final String DEFAULT_ACCESS_TOKENS_FROM_USERNAME_SELECT_STATEMENT = "select token_id, token from oauth_access_token where user_name = ?";

    private static final String DEFAULT_ACCESS_TOKENS_FROM_CLIENTID_SELECT_STATEMENT = "select token_id, token from oauth_access_token where client_id = ?";

    private static final String DEFAULT_ACCESS_TOKEN_DELETE_STATEMENT = "delete from oauth_access_token where token_id = ?";

    private static final String DEFAULT_ACCESS_TOKEN_DELETE_FROM_REFRESH_TOKEN_STATEMENT = "delete from oauth_access_token where refresh_token = ?";

    private static final String DEFAULT_REFRESH_TOKEN_INSERT_STATEMENT = "insert into oauth_refresh_token (token_id, token, authentication) values (?, ?, ?)";

    private static final String DEFAULT_REFRESH_TOKEN_SELECT_STATEMENT = "select token_id, token from oauth_refresh_token where token_id = ?";

    private static final String DEFAULT_REFRESH_TOKEN_AUTHENTICATION_SELECT_STATEMENT = "select token_id, authentication from oauth_refresh_token where token_id = ?";

    private static final String DEFAULT_REFRESH_TOKEN_DELETE_STATEMENT = "delete from oauth_refresh_token where token_id = ?";

    private String insertAccessTokenSql = DEFAULT_ACCESS_TOKEN_INSERT_STATEMENT;

    private String selectAccessTokenSql = DEFAULT_ACCESS_TOKEN_SELECT_STATEMENT;

    private String selectAccessTokenAuthenticationSql = DEFAULT_ACCESS_TOKEN_AUTHENTICATION_SELECT_STATEMENT;

    private String selectAccessTokenFromAuthenticationSql = DEFAULT_ACCESS_TOKEN_FROM_AUTHENTICATION_SELECT_STATEMENT;

    private String selectAccessTokensFromUserNameAndClientIdSql = DEFAULT_ACCESS_TOKENS_FROM_USERNAME_AND_CLIENT_SELECT_STATEMENT;

    private String selectAccessTokensFromUserNameSql = DEFAULT_ACCESS_TOKENS_FROM_USERNAME_SELECT_STATEMENT;

    private String selectAccessTokensFromClientIdSql = DEFAULT_ACCESS_TOKENS_FROM_CLIENTID_SELECT_STATEMENT;

    private String deleteAccessTokenSql = DEFAULT_ACCESS_TOKEN_DELETE_STATEMENT;

    private String insertRefreshTokenSql = DEFAULT_REFRESH_TOKEN_INSERT_STATEMENT;

    private String selectRefreshTokenSql = DEFAULT_REFRESH_TOKEN_SELECT_STATEMENT;

    private String selectRefreshTokenAuthenticationSql = DEFAULT_REFRESH_TOKEN_AUTHENTICATION_SELECT_STATEMENT;

    private String deleteRefreshTokenSql = DEFAULT_REFRESH_TOKEN_DELETE_STATEMENT;

    private String deleteAccessTokenFromRefreshTokenSql = DEFAULT_ACCESS_TOKEN_DELETE_FROM_REFRESH_TOKEN_STATEMENT;

    private AuthenticationKeyGenerator authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();

    private final JdbcTemplate jdbcTemplate;

    public JdbcTokenStore(DataSource dataSource) {
        Assert.notNull(dataSource, "DataSource required");
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void setAuthenticationKeyGenerator(AuthenticationKeyGenerator authenticationKeyGenerator) {
        this.authenticationKeyGenerator = authenticationKeyGenerator;
    }

    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        OAuth2AccessToken accessToken = null;
        String key = authenticationKeyGenerator.extractKey(authentication);
        try {
            accessToken = jdbcTemplate.queryForObject(selectAccessTokenFromAuthenticationSql,
                    new RowMapper<OAuth2AccessToken>() {
                        public OAuth2AccessToken mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return deserializeAccessToken(rs.getBytes(2));
                        }
                    }, key);
        } catch (EmptyResultDataAccessException e) {
            if (LOG.isDebugEnabled()) {
                LOG.error("Failed to find access token for authentication getAccessToken " + authentication);
            }
        } catch (IllegalArgumentException e) {
            LOG.error("Could not extract access token for authentication " + authentication, e);
        }

        if (accessToken != null) {
            OAuth2Authentication oldAuthentication = readAuthentication(accessToken.getValue());
            if (oldAuthentication == null || !key.equals(authenticationKeyGenerator.extractKey(oldAuthentication))) {
//                removeAccessToken(accessToken.getValue());
                // Keep the store consistent (maybe the same user is represented by this authentication but the details have
                // changed)
//                storeAccessToken(accessToken, authentication);
            }
        }
        return accessToken;
    }

    protected boolean isSupportRefreshToken(ClientDetails clientDetails) {
        if (clientDetails != null) {
            return clientDetails.getAuthorizedGrantTypes().contains("refresh_token");
        }
        return false;
    }

    private OAuth2RefreshToken createRefreshToken(ClientDetails clientDetails) {
        if (!isSupportRefreshToken(clientDetails)) {
            return null;
        }
        int validitySeconds = clientDetails.getRefreshTokenValiditySeconds();
        String tokenValue = new String(Base64.encodeBase64URLSafe(DEFAULT_TOKEN_GENERATOR.generateKey()), US_ASCII);
        if (validitySeconds > 0) {
            return new DefaultExpiringOAuth2RefreshToken(tokenValue, new Date(System.currentTimeMillis()
                    + (validitySeconds * 1000L)));
        }
        return new DefaultOAuth2RefreshToken(tokenValue);
    }

    private OAuth2RequestFactory requestFactory;


    public DefaultOAuth2AccessToken createNewAccessToken(ClientDetails clientDetails) {
        String tokenValue = new String(Base64.encodeBase64URLSafe(DEFAULT_TOKEN_GENERATOR.generateKey()), US_ASCII);
        DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken(tokenValue);
        int validitySeconds = clientDetails.getAccessTokenValiditySeconds();
        if (validitySeconds > 0) {
            token.setExpiration(new Date(System.currentTimeMillis() + (validitySeconds * 1000L)));
        }
        OAuth2RefreshToken oAuth2RefreshToken = createRefreshToken(clientDetails);
        token.setRefreshToken(oAuth2RefreshToken);
        token.setScope(clientDetails.getScope());
        Map<String, String> requestParameters = new HashMap<>();
        HashMap<String, String> modifiable = new HashMap<String, String>(requestParameters);
        // Remove password if present to prevent leaks
        modifiable.remove("password");
        modifiable.remove("client_secret");
        // Add grant type so it can be retrieved from OAuth2Request
        modifiable.put(OAuth2Utils.GRANT_TYPE, "client_credentials");

        OAuth2Request oAuth2Request = new OAuth2Request(modifiable, clientDetails.getClientId(), clientDetails.getAuthorities(), true, clientDetails.getScope(),
                clientDetails.getResourceIds(), null, null, null);

        OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(oAuth2Request, null);

        jdbcTemplate.update(insertAccessTokenSql, new Object[]{extractTokenKey(token.getValue()),
                new SqlLobValue(serializeAccessToken(token)), authenticationKeyGenerator.extractKey(oAuth2Authentication),
                oAuth2Authentication.isClientOnly() ? null : oAuth2Authentication.getName(),
                oAuth2Authentication.getOAuth2Request().getClientId(),
                new SqlLobValue(serializeAuthentication(oAuth2Authentication)), extractTokenKey(oAuth2RefreshToken.getValue())}, new int[]{
                Types.VARCHAR, Types.BLOB, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.BLOB, Types.VARCHAR});

        return token;
    }

    public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        String refreshToken = null;
        if (token.getRefreshToken() != null) {
            refreshToken = token.getRefreshToken().getValue();
        }

        if (readAccessToken(token.getValue()) != null) {
            removeAccessToken(token.getValue());
        }

        jdbcTemplate.update(insertAccessTokenSql, new Object[]{extractTokenKey(token.getValue()),
                new SqlLobValue(serializeAccessToken(token)), authenticationKeyGenerator.extractKey(authentication),
                authentication.isClientOnly() ? null : authentication.getName(),
                authentication.getOAuth2Request().getClientId(),
                new SqlLobValue(serializeAuthentication(authentication)), extractTokenKey(refreshToken)}, new int[]{
                Types.VARCHAR, Types.BLOB, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.BLOB, Types.VARCHAR});
    }

    public OAuth2AccessToken readAccessToken(String tokenValue) {
        OAuth2AccessToken accessToken = null;

        try {
            accessToken = jdbcTemplate.queryForObject(selectAccessTokenSql, new RowMapper<OAuth2AccessToken>() {
                public OAuth2AccessToken mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return deserializeAccessToken(rs.getBytes(2));
                }
            }, extractTokenKey(tokenValue));
        } catch (EmptyResultDataAccessException e) {
            if (LOG.isInfoEnabled()) {
                LOG.error("Failed to find access token readAccessToken " + tokenValue);
            }
        } catch (IllegalArgumentException e) {
            LOG.error("Failed to deserialize access token", e);
            removeAccessToken(tokenValue);
        }

        return accessToken;
    }

    public void removeAccessToken(OAuth2AccessToken token) {
        removeAccessToken(token.getValue());
    }

    public void removeAccessToken(String tokenValue) {
        jdbcTemplate.update(deleteAccessTokenSql, extractTokenKey(tokenValue));
    }

    public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
        return readAuthentication(token.getValue());
    }

    public OAuth2Authentication readAuthentication(String token) {
        OAuth2Authentication authentication = null;

        try {
            authentication = jdbcTemplate.queryForObject(selectAccessTokenAuthenticationSql,
                    new RowMapper<OAuth2Authentication>() {
                        public OAuth2Authentication mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return deserializeAuthentication(rs.getBytes(2));
                        }
                    }, extractTokenKey(token));
        } catch (EmptyResultDataAccessException e) {
            if (LOG.isInfoEnabled()) {
                LOG.error("Failed to find access token readAuthentication" + token);
            }
        } catch (IllegalArgumentException e) {
            LOG.error("Failed to deserialize authentication", e);
            removeAccessToken(token);
        }

        return authentication;
    }

    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
        jdbcTemplate.update(insertRefreshTokenSql, new Object[]{extractTokenKey(refreshToken.getValue()),
                new SqlLobValue(serializeRefreshToken(refreshToken)),
                new SqlLobValue(serializeAuthentication(authentication))}, new int[]{Types.VARCHAR, Types.BLOB,
                Types.BLOB});
    }

    public OAuth2RefreshToken readRefreshToken(String token) {
        OAuth2RefreshToken refreshToken = null;

        try {
            refreshToken = jdbcTemplate.queryForObject(selectRefreshTokenSql, new RowMapper<OAuth2RefreshToken>() {
                public OAuth2RefreshToken mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return deserializeRefreshToken(rs.getBytes(2));
                }
            }, extractTokenKey(token));
        } catch (EmptyResultDataAccessException e) {
            if (LOG.isInfoEnabled()) {
                LOG.error("Failed to find refresh token");
            }
        } catch (IllegalArgumentException e) {
            LOG.error("Failed to deserialize refresh token", e);
            removeRefreshToken(token);
        }

        return refreshToken;
    }

    public void removeRefreshToken(OAuth2RefreshToken token) {
        removeRefreshToken(token.getValue());
    }

    public void removeRefreshToken(String token) {
        jdbcTemplate.update(deleteRefreshTokenSql, extractTokenKey(token));
    }

    public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
        return readAuthenticationForRefreshToken(token.getValue());
    }

    public OAuth2Authentication readAuthenticationForRefreshToken(String value) {
        OAuth2Authentication authentication = null;

        try {
            authentication = jdbcTemplate.queryForObject(selectRefreshTokenAuthenticationSql,
                    new RowMapper<OAuth2Authentication>() {
                        public OAuth2Authentication mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return deserializeAuthentication(rs.getBytes(2));
                        }
                    }, extractTokenKey(value));
        } catch (EmptyResultDataAccessException e) {
            if (LOG.isInfoEnabled()) {
                LOG.error("Failed to find access token readAuthenticationForRefreshToken" + value);
            }
        } catch (IllegalArgumentException e) {
            LOG.error("Failed to deserialize access token", e);
            removeRefreshToken(value);
        }

        return authentication;
    }

    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
        removeAccessTokenUsingRefreshToken(refreshToken.getValue());
    }

    public void removeAccessTokenUsingRefreshToken(String refreshToken) {
        jdbcTemplate.update(deleteAccessTokenFromRefreshTokenSql, new Object[]{extractTokenKey(refreshToken)},
                new int[]{Types.VARCHAR});
    }

    public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
        List<OAuth2AccessToken> accessTokens = new ArrayList<OAuth2AccessToken>();

        try {
            accessTokens = jdbcTemplate.query(selectAccessTokensFromClientIdSql, new SafeAccessTokenRowMapper(),
                    clientId);
        } catch (EmptyResultDataAccessException e) {
            if (LOG.isInfoEnabled()) {
                LOG.error("Failed to find access token for clientId " + clientId);
            }
        }
        accessTokens = removeNulls(accessTokens);

        return accessTokens;
    }

    public Collection<OAuth2AccessToken> findTokensByUserName(String userName) {
        List<OAuth2AccessToken> accessTokens = new ArrayList<OAuth2AccessToken>();

        try {
            accessTokens = jdbcTemplate.query(selectAccessTokensFromUserNameSql, new SafeAccessTokenRowMapper(),
                    userName);
        } catch (EmptyResultDataAccessException e) {
            if (LOG.isInfoEnabled())
                LOG.error("Failed to find access token for userName " + userName);
        }
        accessTokens = removeNulls(accessTokens);

        return accessTokens;
    }

    public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName) {
        List<OAuth2AccessToken> accessTokens = new ArrayList<OAuth2AccessToken>();

        try {
            accessTokens = jdbcTemplate.query(selectAccessTokensFromUserNameAndClientIdSql, new SafeAccessTokenRowMapper(),
                    userName, clientId);
        } catch (EmptyResultDataAccessException e) {
            if (LOG.isInfoEnabled()) {
                LOG.error("Failed to find access token for clientId " + clientId + " and userName " + userName);
            }
        }
        accessTokens = removeNulls(accessTokens);

        return accessTokens;
    }

    private List<OAuth2AccessToken> removeNulls(List<OAuth2AccessToken> accessTokens) {
        List<OAuth2AccessToken> tokens = new ArrayList<OAuth2AccessToken>();
        for (OAuth2AccessToken token : accessTokens) {
            if (token != null) {
                tokens.add(token);
            }
        }
        return tokens;
    }

    protected String extractTokenKey(String value) {
        if (value == null) {
            return null;
        }
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("MD5 algorithm not available.  Fatal (should be in the JDK).");
        }

        try {
            byte[] bytes = digest.digest(value.getBytes("UTF-8"));
            return String.format("%032x", new BigInteger(1, bytes));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 encoding not available.  Fatal (should be in the JDK).");
        }
    }

    private final class SafeAccessTokenRowMapper implements RowMapper<OAuth2AccessToken> {
        public OAuth2AccessToken mapRow(ResultSet rs, int rowNum) throws SQLException {
            try {
                return deserializeAccessToken(rs.getBytes(2));
            } catch (IllegalArgumentException e) {
                String token = rs.getString(1);
                jdbcTemplate.update(deleteAccessTokenSql, token);
                return null;
            }
        }
    }

    protected byte[] serializeAccessToken(OAuth2AccessToken token) {
        return SerializationUtils.serialize(token);
    }

    protected byte[] serializeRefreshToken(OAuth2RefreshToken token) {
        return SerializationUtils.serialize(token);
    }

    protected byte[] serializeAuthentication(OAuth2Authentication authentication) {
        return SerializationUtils.serialize(authentication);
    }

    protected OAuth2AccessToken deserializeAccessToken(byte[] token) {
        return SerializationUtils.deserialize(token);
    }

    protected OAuth2RefreshToken deserializeRefreshToken(byte[] token) {
        return SerializationUtils.deserialize(token);
    }

    protected OAuth2Authentication deserializeAuthentication(byte[] authentication) {
        return SerializationUtils.deserialize(authentication);
    }

    public void setInsertAccessTokenSql(String insertAccessTokenSql) {
        this.insertAccessTokenSql = insertAccessTokenSql;
    }

    public void setSelectAccessTokenSql(String selectAccessTokenSql) {
        this.selectAccessTokenSql = selectAccessTokenSql;
    }

    public void setDeleteAccessTokenSql(String deleteAccessTokenSql) {
        this.deleteAccessTokenSql = deleteAccessTokenSql;
    }

    public void setInsertRefreshTokenSql(String insertRefreshTokenSql) {
        this.insertRefreshTokenSql = insertRefreshTokenSql;
    }

    public void setSelectRefreshTokenSql(String selectRefreshTokenSql) {
        this.selectRefreshTokenSql = selectRefreshTokenSql;
    }

    public void setDeleteRefreshTokenSql(String deleteRefreshTokenSql) {
        this.deleteRefreshTokenSql = deleteRefreshTokenSql;
    }

    public void setSelectAccessTokenAuthenticationSql(String selectAccessTokenAuthenticationSql) {
        this.selectAccessTokenAuthenticationSql = selectAccessTokenAuthenticationSql;
    }

    public void setSelectRefreshTokenAuthenticationSql(String selectRefreshTokenAuthenticationSql) {
        this.selectRefreshTokenAuthenticationSql = selectRefreshTokenAuthenticationSql;
    }

    public void setSelectAccessTokenFromAuthenticationSql(String selectAccessTokenFromAuthenticationSql) {
        this.selectAccessTokenFromAuthenticationSql = selectAccessTokenFromAuthenticationSql;
    }

    public void setDeleteAccessTokenFromRefreshTokenSql(String deleteAccessTokenFromRefreshTokenSql) {
        this.deleteAccessTokenFromRefreshTokenSql = deleteAccessTokenFromRefreshTokenSql;
    }

    public void setSelectAccessTokensFromUserNameSql(String selectAccessTokensFromUserNameSql) {
        this.selectAccessTokensFromUserNameSql = selectAccessTokensFromUserNameSql;
    }

    public void setSelectAccessTokensFromUserNameAndClientIdSql(String selectAccessTokensFromUserNameAndClientIdSql) {
        this.selectAccessTokensFromUserNameAndClientIdSql = selectAccessTokensFromUserNameAndClientIdSql;
    }

    public void setSelectAccessTokensFromClientIdSql(String selectAccessTokensFromClientIdSql) {
        this.selectAccessTokensFromClientIdSql = selectAccessTokensFromClientIdSql;
    }

}
