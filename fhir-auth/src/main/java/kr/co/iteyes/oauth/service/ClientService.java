package kr.co.iteyes.oauth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Service
public class ClientService {

    @Autowired
    DataSource dataSource;

    private JdbcClientDetailsService clientDetailsService;

    private JdbcClientDetailsService getClientDetailsService() {
        if (clientDetailsService == null) {
            this.clientDetailsService = new JdbcClientDetailsService(dataSource);
        }
        return this.clientDetailsService;
    }

    public ClientDetails loadClientByClientId(String clientId) throws NoSuchClientException, SQLException, UncategorizedSQLException {
        return getClientDetailsService().loadEncryptClientByClientId(clientId);


    }

    public void createClient(String clientId, String secretKey) throws Exception {
        BaseClientDetails newClientDetail = new BaseClientDetails();
        newClientDetail.setClientId(clientId);
        newClientDetail.setClientSecret(secretKey);
        String[] grants = new String[]{"authorization_code", "implicit,password", "client_credentials", "refresh_token"};
        newClientDetail.setAuthorizedGrantTypes(Arrays.asList(grants));
        newClientDetail.setAccessTokenValiditySeconds(360000000);
        newClientDetail.setRefreshTokenValiditySeconds(360000000);
        newClientDetail.setScope(Arrays.asList("read", "write"));

        getClientDetailsService().addEcryptClientDetails(newClientDetail);
    }

    public void updateScope(ClientDetails clientDetails) {
        BaseClientDetails updateClientDetail = new BaseClientDetails();
        updateClientDetail.setClientId(clientDetails.getClientId());
        updateClientDetail.setClientSecret(clientDetails.getClientSecret());
        updateClientDetail.setAuthorizedGrantTypes(clientDetails.getAuthorizedGrantTypes());
        updateClientDetail.setAccessTokenValiditySeconds(clientDetails.getAccessTokenValiditySeconds());
        updateClientDetail.setRefreshTokenValiditySeconds(clientDetails.getRefreshTokenValiditySeconds());
        updateClientDetail.setScope(Arrays.asList("read", "write"));

        getClientDetailsService().updateClientDetails(updateClientDetail);
    }
}
