package kr.co.iteyes.fhirmeta.service;

import com.fasterxml.jackson.databind.JsonNode;
import kr.co.iteyes.fhirmeta.exception.CustomException;
import kr.co.iteyes.fhirmeta.exception.ExceptionEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class AuthService {

    @Value("${auth.server.url}")
    private String authServerUrl;

    @Value("${meta.client.id}")
    private String clientId;

    @Value("${meta.client.secretKey}")
    private String clientSecretKey;

    @Autowired
    RestTemplate restTemplate;

    public String getAuthToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> jsonBody = new HashMap<>();
        jsonBody.put("clientId", clientId);
        jsonBody.put("clientSecretKey", clientSecretKey);

        HttpEntity requestBody = new HttpEntity<>(jsonBody, headers);

        try {
            ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(authServerUrl + "/token", HttpMethod.POST, requestBody, JsonNode.class);
            JsonNode node = responseEntity.getBody();
            String validityYN = node.path("validityYN").asText();
            if(!validityYN.equals("Y")) {
                throw new CustomException(ExceptionEnum.ACCESS_DENIED_EXCEPTION);
            }
            return node.path("token").asText();
        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new CustomException(ExceptionEnum.ACCESS_DENIED_EXCEPTION);
            } else {
                throw new CustomException(ExceptionEnum.INTERNAL_AUTH_SERVER_ERROR);
            }
        }
    }

    public void createAuthToken(String clientId, String clientSecretKey) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> jsonBody = new HashMap<>();
        jsonBody.put("clientId", clientId);
        jsonBody.put("clientSecretKey", clientSecretKey);

        HttpEntity requestBody = new HttpEntity<>(jsonBody, headers);

        try {
            ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(authServerUrl + "/token/create", HttpMethod.POST, requestBody, JsonNode.class);
            JsonNode node = responseEntity.getBody();
            String result = node.path("result").asText();
            if(!result.equals("SUCCESS")) {
                throw new CustomException(ExceptionEnum.ACCESS_TOKEN_CREATE_EXCEPTION);
            }
            log.debug("토큰 생성 완료 = {}", clientId);
        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new CustomException(ExceptionEnum.ACCESS_DENIED_EXCEPTION);
            } else {
                throw new CustomException(ExceptionEnum.INTERNAL_AUTH_SERVER_ERROR);
            }
        }
    }
}
