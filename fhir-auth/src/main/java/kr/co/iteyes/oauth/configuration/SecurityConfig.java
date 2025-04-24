package kr.co.iteyes.oauth.configuration;

import kr.co.iteyes.oauth.handler.CustomFailHander;
import kr.co.iteyes.oauth.handler.CustomSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().httpBasic() // csrf 공격을 막기 위해 state 값을 전달 받지 않는다
//                http.httpBasic()// http 통신으로 basic auth를 사용 할 수 있다. (ex: Authorization: Basic bzFbdGfmZrptWY30YQ==)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS); //Session 에 상관없이 토큰 인증가능하도록 변경.
    }

    /**
     * authenticationManager bean 생성 하여 셋팅 안할시 grant_type : password 지원 안함
     *
     * @return
     * @throws Exception
     */
    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Bean
    public AuthenticationFailureHandler failureHandler() {
        return new CustomFailHander();
    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return new CustomSuccessHandler();
    }
}
