package kr.co.iteyes.fhirmeta.interceptor;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.StringUtils;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import kr.co.iteyes.fhirmeta.exception.CustomException;
import kr.co.iteyes.fhirmeta.exception.ExceptionEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Value("${meta.client.id}")
    private String clientId;

    @Value("${meta.client.secretKey}")
    private String clientSecretKey;

    @Value("${auth.server.url}")
    private String authServerUrl;

    @Autowired
    RestTemplate restTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(StringUtils.isBlank(token)) throw new CustomException(ExceptionEnum.ACCESS_TOKEN_NULL_EXCEPTION);
        log.debug("================= preHandle =================");
        log.debug("token = {}", token);
        log.debug("=============================================");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> jsonBody = new HashMap<>();
        jsonBody.put("clientId", clientId);
        jsonBody.put("clientSecretKey", clientSecretKey);
        jsonBody.put("token", token);

        HttpEntity requestBody = new HttpEntity<>(jsonBody, headers);

        try {
            ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(authServerUrl + "/check", HttpMethod.POST, requestBody, JsonNode.class);
            JsonNode node = responseEntity.getBody();
            String result = node.path("validityYN").asText();
            if(!result.equals("Y")) {
                throw new CustomException(ExceptionEnum.ACCESS_DENIED_EXCEPTION);
            }
        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new CustomException(ExceptionEnum.ACCESS_DENIED_EXCEPTION);
            } else {
                throw new CustomException(ExceptionEnum.INTERNAL_AUTH_SERVER_ERROR);
            }
        } catch (Exception e) {
            throw new CustomException(ExceptionEnum.INTERNAL_AUTH_SERVER_ERROR);
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }
}
