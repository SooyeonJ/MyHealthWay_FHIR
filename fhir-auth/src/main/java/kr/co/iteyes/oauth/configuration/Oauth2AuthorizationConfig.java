package kr.co.iteyes.oauth.configuration;

import kr.co.iteyes.oauth.tokenstore.JdbcTokenStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.JdbcApprovalStore;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

import javax.sql.DataSource;

@EnableAuthorizationServer
@Configuration
public class Oauth2AuthorizationConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private DataSource dataSource;


    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.tokenKeyAccess("permitAll()") // 모두 허용하지 않으면 해당 서버에서 토큰 접근이 불가능 하여 토큰을 DB에서 찾을 수 없다.
                .checkTokenAccess("isAuthenticated()") // 인증된 사용자만 토큰 체크 가능
                .allowFormAuthenticationForClients();
    }

    // client 설정
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {

        clients.jdbc(dataSource);
//                .passwordEncoder(passwordEncoder());

    }

    // 토큰 DB 저장
    @Bean
    public TokenStore tokenStore() {
        return new JdbcTokenStore(dataSource);
    }

    @Bean
    public ApprovalStore approvalStore() {
        return new JdbcApprovalStore(dataSource);
    }


    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore((org.springframework.security.oauth2.provider.token.TokenStore) tokenStore());
        defaultTokenServices.setSupportRefreshToken(true);
        return defaultTokenServices;
    }

    // 인증, 토큰 설정
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager) // grant_type password를 사용하기 위함 (manager 지정 안할시 password type 으로 토큰 발행시 Unsupported grant type: password 오류 발생)
                .tokenStore(tokenStore())
                .approvalStore(approvalStore());

    }

}
