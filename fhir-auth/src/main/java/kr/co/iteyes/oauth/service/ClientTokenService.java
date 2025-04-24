package kr.co.iteyes.oauth.service;

import kr.co.iteyes.oauth.tokenstore.JdbcTokenStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Service
@Slf4j
public class ClientTokenService {

    private final DataSource dataSource;


    private JdbcTokenStore jdbcTokenStore;

    public ClientTokenService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private JdbcTokenStore getJdbcTokenStore() {
        if (jdbcTokenStore == null) {
            this.jdbcTokenStore = new JdbcTokenStore(dataSource);
        }
        return this.jdbcTokenStore;
    }


    private JdbcClientDetailsService clientDetailsService;

    private JdbcClientDetailsService getClientDetailsService() {
        if (clientDetailsService == null) {
            this.clientDetailsService = new JdbcClientDetailsService(dataSource);
        }
        return this.clientDetailsService;
    }

    public String createAuthToken(String clientId) throws Exception {

        ClientDetails clientDetails = getClientDetailsService().loadClientByClientId(clientId);

        DefaultOAuth2AccessToken oAuth2AccessToken = getJdbcTokenStore().createNewAccessToken(clientDetails);

        String expired_date = null;
        Date date = new Date();
        SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.SECOND, (int) oAuth2AccessToken.getExpiresIn());
        expired_date = sdformat.format(cal.getTime());
        String jsonResult = "{\n" +
                "\t\"validityYN\":\"Y\",\n" +
                "\t\"validityDateTime\":\"" + expired_date + "\",\n" +
                "\t\"token\":\"" + oAuth2AccessToken.getValue() + "\"\n" +
                "}";
        return jsonResult;

    }


}
