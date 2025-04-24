package kr.co.iteyes.fhir.jpa.security.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class HttpRequestService implements IHttpRequestService {

	RestTemplate restTemplate = new RestTemplate();



	@Override
	public String restTemplateRequestAuth(String targetURL, String clientId, String secretKey) {
		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		Map<String, Object> jsonBody = new HashMap<>();
		jsonBody.put("clientId", clientId);
		jsonBody.put("clientSecretKey", secretKey);

		HttpEntity requestBody = new HttpEntity<>(jsonBody, headers);

		ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(targetURL, HttpMethod.POST, requestBody, JsonNode.class);

		return responseEntity.getBody().path("token").asText();
	}

	@Override
	public String restTemplateRequestWithAuth(String targetURL, String authorization) {
		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(authorization);

		Map<String, Object> jsonBody = new HashMap<>();

		HttpEntity requestBody = new HttpEntity<>(jsonBody, headers);

		ResponseEntity<String> responseEntity = restTemplate.exchange(targetURL, HttpMethod.GET, requestBody, String.class);

		return responseEntity.getBody();
	}

}
