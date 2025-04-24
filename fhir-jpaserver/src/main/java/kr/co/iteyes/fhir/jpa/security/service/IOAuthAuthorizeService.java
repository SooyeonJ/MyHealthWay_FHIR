package kr.co.iteyes.fhir.jpa.security.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import kr.co.iteyes.fhir.jpa.security.vo.AccessToken;

import java.io.IOException;

public interface IOAuthAuthorizeService {

//	public AccessToken getAccessToken() throws JsonProcessingException, IOException;

	AccessToken getAccessToken(String username, String password) throws JsonProcessingException, IOException;

	String getClientAccessToken(String clientId, String clientSecretKey) throws JsonProcessingException, IOException;

	public AccessToken refreshAccessToken(String refreshToken) throws JsonProcessingException, IOException;

//	public AccessToken getAccessToken();

	public String checkAccessToken(String accessToken) throws JsonProcessingException, IOException;


}
