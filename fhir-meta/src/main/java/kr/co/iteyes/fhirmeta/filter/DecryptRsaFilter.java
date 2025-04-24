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
import java.security.InvalidKeyException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service("decryptRsaFilter")
public class DecryptRsaFilter implements Filter {

    @Autowired
    private EncryptService encryptService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;

        try {
            String privateKey = encryptService.getValidKey("RSA").getRsaOaep().getPrivateKey();
            RequestBodyDecryptRsaWrapper requestWrapper = new RequestBodyDecryptRsaWrapper(req, privateKey);
            filterChain.doFilter(requestWrapper, res);
        } catch (NullPointerException e) {
            String message = "Request body is Null or Empty";
            log.error(message, e);

            Map<String, String> resultMap = new HashMap<>();
            resultMap.put("result", "FAIL");
            resultMap.put("message", message);

            String responseMessage = new ObjectMapper().writeValueAsString(resultMap);
            HttpUtils.commonRes(res, responseMessage, HttpStatus.BAD_REQUEST.value()).flushBuffer();
        } catch (InvalidKeyException e) {
            String message = "유효한 암호화 키(RSA)가 아닙니다.";
            log.error(message, e);

            Map<String, String> resultMap = new HashMap<>();
            resultMap.put("result", "FAIL");
            resultMap.put("message", message);

            String responseMessage = new ObjectMapper().writeValueAsString(resultMap);
            HttpUtils.commonRes(res, responseMessage, HttpStatus.BAD_REQUEST.value()).flushBuffer();
        } catch (Exception e) {
            String message = "Decrypt Fail";
            log.error(message, e);

            Map<String, String> resultMap = new HashMap<>();
            resultMap.put("result", "FAIL");
            resultMap.put("message", message);

            String responseMessage = new ObjectMapper().writeValueAsString(resultMap);
            HttpUtils.commonRes(res, responseMessage, HttpStatus.BAD_REQUEST.value()).flushBuffer();
        }
    }
}
