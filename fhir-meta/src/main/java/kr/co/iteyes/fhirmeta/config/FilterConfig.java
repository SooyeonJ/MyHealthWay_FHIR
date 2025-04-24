package kr.co.iteyes.fhirmeta.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.DelegatingFilterProxy;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean DefaultFilter() {
        DelegatingFilterProxy proxy = new DelegatingFilterProxy();
        proxy.setTargetFilterLifecycle(true);
        proxy.setTargetBeanName("defaultFilter");

        FilterRegistrationBean registrationBean = new FilterRegistrationBean();

        registrationBean.setFilter(proxy);

        registrationBean.addUrlPatterns("/*");
        registrationBean.setName("defaultFilter");
        registrationBean.setOrder(1);

        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean DecryptFilter() {
        DelegatingFilterProxy proxy = new DelegatingFilterProxy();
        proxy.setTargetFilterLifecycle(true);
        proxy.setTargetBeanName("decryptFilter");

        FilterRegistrationBean registrationBean = new FilterRegistrationBean();

        registrationBean.setFilter(proxy);

        registrationBean.addUrlPatterns("/patient/*");
        registrationBean.addUrlPatterns("/organization/*");
        registrationBean.addUrlPatterns("/extract/*");
        registrationBean.addUrlPatterns("/visit/*");
        registrationBean.addUrlPatterns("/status/*");
        registrationBean.addUrlPatterns("/logs/*");
        registrationBean.addUrlPatterns("/statistics/*");
        registrationBean.addUrlPatterns("/fhir/*");
        registrationBean.addUrlPatterns("/security/organization/*");
        registrationBean.addUrlPatterns("/agent/*");
        registrationBean.addUrlPatterns("/metrics/*");
        registrationBean.addUrlPatterns("/consent/*");
        registrationBean.addUrlPatterns("/updtdmnd/*");

        registrationBean.setName("decryptFilter");
        registrationBean.setOrder(2);

        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean DecryptRsaFilter() {
        DelegatingFilterProxy proxy = new DelegatingFilterProxy();
        proxy.setTargetFilterLifecycle(true);
        proxy.setTargetBeanName("decryptRsaFilter");

        FilterRegistrationBean registrationBean = new FilterRegistrationBean();

        registrationBean.setFilter(proxy);

        registrationBean.addUrlPatterns("/security/seed");
        registrationBean.addUrlPatterns("/security/seed/updt");
        registrationBean.setName("decryptRsaFilter");
        registrationBean.setOrder(3);

        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean DecompressFilter() {
        DelegatingFilterProxy proxy = new DelegatingFilterProxy();
        proxy.setTargetFilterLifecycle(true);
        proxy.setTargetBeanName("decompressFilter");

        FilterRegistrationBean registrationBean = new FilterRegistrationBean();

        registrationBean.setFilter(proxy);

        registrationBean.addUrlPatterns("/patient/*");
        registrationBean.addUrlPatterns("/security/seed");
        registrationBean.addUrlPatterns("/security/seed/updt");
        registrationBean.addUrlPatterns("/organization/*");
        registrationBean.addUrlPatterns("/extract/*");
        registrationBean.addUrlPatterns("/visit/*");
        registrationBean.addUrlPatterns("/status/*");
        registrationBean.addUrlPatterns("/logs/*");
        registrationBean.addUrlPatterns("/statistics/*");
        registrationBean.addUrlPatterns("/fhir/*");
        registrationBean.addUrlPatterns("/security/organization/*");
        registrationBean.addUrlPatterns("/agent/*");
        registrationBean.addUrlPatterns("/metrics/*");
        registrationBean.addUrlPatterns("/consent/*");
        registrationBean.addUrlPatterns("/updtdmnd/*");
        registrationBean.setName("decompressFilter");
        registrationBean.setOrder(4);

        return registrationBean;

    }

    @Bean
    public FilterRegistrationBean ConvertJsonFilter() {
        DelegatingFilterProxy proxy = new DelegatingFilterProxy();
        proxy.setTargetFilterLifecycle(true);
        proxy.setTargetBeanName("convertJsonFilter");

        FilterRegistrationBean registrationBean = new FilterRegistrationBean();

        registrationBean.setFilter(proxy);

        registrationBean.addUrlPatterns("/patient/*");
        registrationBean.addUrlPatterns("/security/seed");
        registrationBean.addUrlPatterns("/security/seed/updt");
        registrationBean.addUrlPatterns("/organization/*");
        registrationBean.addUrlPatterns("/extract/*");
        registrationBean.addUrlPatterns("/visit/*");
        registrationBean.addUrlPatterns("/status/*");
        registrationBean.addUrlPatterns("/logs/*");
        registrationBean.addUrlPatterns("/statistics/*");
        registrationBean.addUrlPatterns("/fhir/*");
        registrationBean.addUrlPatterns("/security/organization/*");
        registrationBean.addUrlPatterns("/agent/*");
        registrationBean.addUrlPatterns("/metrics/*");
        registrationBean.addUrlPatterns("/consent/*");
        registrationBean.addUrlPatterns("/updtdmnd/*");

        registrationBean.setName("convertJsonFilter");
        registrationBean.setOrder(5);

        return registrationBean;

    }

    @Bean
    public FilterRegistrationBean ResponseFilter() {
        DelegatingFilterProxy proxy = new DelegatingFilterProxy();
        proxy.setTargetFilterLifecycle(true);
        proxy.setTargetBeanName("responseFilter");

        FilterRegistrationBean registrationBean = new FilterRegistrationBean();

        registrationBean.setFilter(proxy);

        registrationBean.addUrlPatterns("/patient/*");
        registrationBean.addUrlPatterns("/organization/*");
        registrationBean.addUrlPatterns("/extract/*");
        registrationBean.addUrlPatterns("/visit/*");
        registrationBean.addUrlPatterns("/status/*");
        registrationBean.addUrlPatterns("/logs/*");
        registrationBean.addUrlPatterns("/statistics/*");
        registrationBean.addUrlPatterns("/fhir/*");
        registrationBean.addUrlPatterns("/security/organization/*");
        registrationBean.addUrlPatterns("/agent/*");
        registrationBean.addUrlPatterns("/metrics/*");
        registrationBean.addUrlPatterns("/consent/*");
        registrationBean.addUrlPatterns("/updtdmnd/*");

        registrationBean.setName("responseFilter");
        registrationBean.setOrder(6);

        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean ResponseTempSeedFilter() {
        DelegatingFilterProxy proxy = new DelegatingFilterProxy();
        proxy.setTargetFilterLifecycle(true);
        proxy.setTargetBeanName("responseTempSeedFilter");

        FilterRegistrationBean registrationBean = new FilterRegistrationBean();

        registrationBean.setFilter(proxy);
        registrationBean.addUrlPatterns("/__security/seed"); //dummy
        registrationBean.setName("responseTempSeedFilter");
        registrationBean.setOrder(7);

        return registrationBean;
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        List<ClientHttpRequestInterceptor> interceptors
                = restTemplate.getInterceptors();
        if (CollectionUtils.isEmpty(interceptors)) {
            interceptors = new ArrayList<>();
        }
        restTemplate.setInterceptors(interceptors);
        return restTemplate;
    }
}
