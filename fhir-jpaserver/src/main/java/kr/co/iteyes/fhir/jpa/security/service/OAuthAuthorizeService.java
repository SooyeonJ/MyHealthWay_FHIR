package kr.co.iteyes.fhir.jpa.security.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import kr.co.iteyes.fhir.jpa.security.config.OAuthConfig;
import kr.co.iteyes.fhir.jpa.security.vo.AccessToken;
import kr.co.iteyes.fhir.jpa.security.vo.CheckToken;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class OAuthAuthorizeService implements IOAuthAuthorizeService {
	private static final org.slf4j.Logger ourLog = org.slf4j.LoggerFactory.getLogger(OAuthAuthorizeService.class);
	private static OAuthAuthorizeService oAuthAuthorizeService;

	public OAuthAuthorizeService(){}

	public static OAuthAuthorizeService getInstance(){
		if(oAuthAuthorizeService == null){
			oAuthAuthorizeService = new OAuthAuthorizeService();
		}
		return oAuthAuthorizeService;
	}

	// OAuthConfig start
	private static String oauthServerUrl;
	public void setOauth_server_url(String oauth_server_url) {
		this.oauthServerUrl = oauth_server_url;
	}

	private static String oauthServerClientid;
	public void setOauth_server_clientid(String oauth_server_clientid) {
		this.oauthServerClientid = oauth_server_clientid;
	}

	private static String oauthServerSecretkey;
	public void setOauth_server_secretkey(String oauth_server_secretkey) {
		this.oauthServerSecretkey = oauth_server_secretkey;
	}
	// OAuthConfig end


	@Override
	public AccessToken getAccessToken(String username, String password) throws JsonProcessingException, IOException {
		ResponseEntity<AccessToken> response = null;
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		String credentials = oauthServerClientid+":"+ oauthServerSecretkey;

		String encodedCredentials = credentials;
		encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.add("Authorization", "Basic " + encodedCredentials);

		MultiValueMap<String, String> mapRequest= new LinkedMultiValueMap<>();
		mapRequest.add("grant_type","password");
		mapRequest.add("username",username);
		mapRequest.add("password",password);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(mapRequest, headers);
		String access_token_url = null;
//		if (OAuthConfig.getOauthServerPort() != null || OAuthConfig.getOauthServerPort().length() >0){
//			access_token_url = oauthServerUrl + ":" +OAuthConfig.getOauthServerPort() + OAuthConfig.getOauthCheckToken();
//		}else{
//			access_token_url = oauthServerUrl + OAuthConfig.getOauthCheckToken();
//		}
		access_token_url = oauthServerUrl + OAuthConfig.getOauthCheckToken();
		response = restTemplate.exchange(access_token_url, HttpMethod.POST, request, AccessToken.class);


		AccessToken accessToken = response.getBody();

		return accessToken;
	}

	@Override
	public String getClientAccessToken(String clientId, String clientSecretKey) throws JsonProcessingException, IOException {
		ResponseEntity<AccessToken> response = null;
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		String credentials = oauthServerClientid+":"+ oauthServerSecretkey;

		String encodedCredentials = credentials;
		encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.add("Authorization", "Basic " + encodedCredentials);

		MultiValueMap<String, String> mapRequest= new LinkedMultiValueMap<>();
		mapRequest.add("grant_type","password");


		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(mapRequest, headers);
		String access_token_url = null;
//		if (OAuthConfig.getOauthServerPort() != null || OAuthConfig.getOauthServerPort().length() >0){
//			access_token_url = oauthServerUrl + ":" +OAuthConfig.getOauthServerPort() + OAuthConfig.getOauthCheckToken();
//		}else{
//			access_token_url = oauthServerUrl + OAuthConfig.getOauthCheckToken();
//		}
		access_token_url = oauthServerUrl + OAuthConfig.getOauthCheckToken();
		response = restTemplate.exchange(access_token_url, HttpMethod.POST, request, AccessToken.class);


		AccessToken accessToken = response.getBody();

		return accessToken.getAccess_token();
	}

	@Override
	public AccessToken refreshAccessToken(String refreshToken) throws JsonProcessingException, IOException {
		ResponseEntity<AccessToken> response = null;
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		String credentials = oauthServerClientid+":"+ oauthServerSecretkey;
		String encodedCredentials = credentials;
		encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.add("Authorization", "Basic " + encodedCredentials);

//		HttpEntity<String> request = new HttpEntity<String>(headers);
		MultiValueMap<String, String> mapRequest= new LinkedMultiValueMap<>();
		mapRequest.add("grant_type","refresh_token");
		mapRequest.add("refresh_token",refreshToken);
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(mapRequest, headers);

		String access_token_url = null;
//		if (OAuthConfig.getOauthServerPort() != null || OAuthConfig.getOauthServerPort().length() >0){
//			access_token_url = oauthServerUrl + ":" +OAuthConfig.getOauthServerPort() + OAuthConfig.getOauthCheckToken();
//		}else{
//			access_token_url = oauthServerUrl + OAuthConfig.getOauthCheckToken();
//		}
		access_token_url = oauthServerUrl + OAuthConfig.getOauthCheckToken();
		response = restTemplate.exchange(access_token_url, HttpMethod.POST, request, AccessToken.class);

		AccessToken accessToken = response.getBody();
		return accessToken;
	}

	@Override
	public String checkAccessToken(String accessToken) throws JsonProcessingException, IOException {
		ResponseEntity<CheckToken> response = null;
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		Map<String, Object> jsonBody = new HashMap<>();
		jsonBody.put("clientId", oauthServerClientid);
		jsonBody.put("clientSecretKey", oauthServerSecretkey);
		jsonBody.put("token", accessToken);

		HttpEntity requestBody = new HttpEntity<>(jsonBody, headers);

		String access_token_url = null;

		access_token_url = oauthServerUrl + OAuthConfig.getOauthCheckToken();
		ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(access_token_url, HttpMethod.POST, requestBody, JsonNode.class);
//		ResponseEntity<JsonNode> responseEntity = restTemplate.exchange("http://localhost:80/auth/check", HttpMethod.POST, requestBody, JsonNode.class);

		JsonNode node = responseEntity.getBody();
		String result = node.path("validityYN").asText();
		if(!result.equals("Y")) {
			throw new RuntimeException("Access Denied");
		}
		return result;
	}


}
