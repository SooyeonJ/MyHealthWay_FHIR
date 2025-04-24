package kr.co.iteyes.fhirmeta.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import kr.co.iteyes.fhirmeta.service.EncryptService;
import kr.co.iteyes.fhirmeta.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service("convertJsonFilter")
public class ConvertJsonFilter implements Filter {

    @Autowired
    private EncryptService encryptService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;

        try {
            if(!req.getMethod().equals(HttpMethod.GET.name())) {
                RequestBodyJsonWrapper requestBodyJsonWrapper = new RequestBodyJsonWrapper(req);
                filterChain.doFilter(requestBodyJsonWrapper, res);
            } else {
                filterChain.doFilter(req, res);
            }
        } catch (Exception e) {
            try {
                String key = null;
                String cisn = req.getHeader("cisn");
                if(StringUtils.isNotBlank(cisn)) {
                    key = encryptService.getSeedKey(cisn).getSeedCtr().getKey();
                } else {
                    key = encryptService.getValidKey("SEED").getSeedCtr().getKey();
                }
                String message = "Converted to json Fail";
                log.error(message, e);

                Map<String, String> resultMap = new HashMap<>();
                resultMap.put("result", "FAIL");
                resultMap.put("message", message);

                String responseMessage = new ObjectMapper().writeValueAsString(resultMap);
                HttpUtils.commonEncryptRes(res, key, responseMessage, HttpStatus.BAD_REQUEST.value()).flushBuffer();
            } catch (InvalidKeyException ex) {
                String message = "유효한 암호화 키(SEED)가 아닙니다.";
                log.error(message, e);

                Map<String, String> resultMap = new HashMap<>();
                resultMap.put("result", "FAIL");
                resultMap.put("message", message);

                String responseMessage = new ObjectMapper().writeValueAsString(resultMap);
                HttpUtils.commonRes(res, responseMessage, HttpStatus.BAD_REQUEST.value()).flushBuffer();
            }
        }
    }
}
