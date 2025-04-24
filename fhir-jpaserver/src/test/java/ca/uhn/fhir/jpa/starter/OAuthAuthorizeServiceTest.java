package ca.uhn.fhir.jpa.starter;

import com.fasterxml.jackson.databind.JsonNode;
import kr.co.iteyes.fhir.jpa.security.service.OAuthAuthorizeService;
import kr.co.iteyes.fhir.jpa.security.vo.AccessToken;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OAuthAuthorizeServiceTest {

	private OAuthAuthorizeService oAuthAuthorizeService = OAuthAuthorizeService.getInstance();

	@Test
	public void testGetAccessToken() throws IOException {
	}

	@Test
	public void testGetTokenByRefreshToken() throws IOException {
	}

	@Test
	public void testCheckAccessToken() throws IOException {
	}

}
