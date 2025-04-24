package kr.co.iteyes.fhirmeta.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import kr.co.iteyes.fhirmeta.service.EncryptService;
import kr.co.iteyes.fhirmeta.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service("responseTempSeedFilter")
public class ResponseTempSeedFilter implements Filter {

    @Autowired
    private EncryptService encryptService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;
        String key = null;
        try {
            ResponseBodyWrapper responseWrapper = new ResponseBodyWrapper(res);
            responseWrapper.addHeader("serverDomainNo", req.getHeader("serverDomainNo"));
            filterChain.doFilter(req, responseWrapper);

            // Response Body Data
            String responseMessage = responseWrapper.getDataStreamToString();
            key = encryptService.getTempSeedKey().getKey();
            HttpUtils.commonEncryptRes(res, key, responseMessage, HttpStatus.BAD_REQUEST.value()).flushBuffer();
        } catch (Exception e) {
            Map<String, String> resultMap = new HashMap<>();
            resultMap.put("result", "FAIL");
            resultMap.put("message", e.getMessage());

            String responseMessage = new ObjectMapper().writeValueAsString(resultMap);
            HttpUtils.commonEncryptRes(res, key, responseMessage, HttpStatus.BAD_REQUEST.value()).flushBuffer();
        }
    }
}
