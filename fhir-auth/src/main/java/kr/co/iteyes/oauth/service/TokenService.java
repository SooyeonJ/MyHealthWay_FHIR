package kr.co.iteyes.oauth.service;

import kr.co.iteyes.oauth.tokenstore.JdbcTokenStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.Collection;

@Service
public class TokenService {

    @Autowired
    DataSource dataSource;

    private JdbcTokenStore jdbcTokenStore;

    private JdbcTokenStore getJdbcTokenStore() {
        if (jdbcTokenStore == null) {
            this.jdbcTokenStore = new JdbcTokenStore(dataSource);
        }
        return this.jdbcTokenStore;
    }


    public Collection<OAuth2AccessToken> readAccessTokens(String clientId) throws Exception {
        return getJdbcTokenStore().findTokensByClientId(clientId);
    }


    public OAuth2AccessToken getAccessTokenByToken(String token) throws Exception {
        return getJdbcTokenStore().readAccessToken(token);
    }

}
