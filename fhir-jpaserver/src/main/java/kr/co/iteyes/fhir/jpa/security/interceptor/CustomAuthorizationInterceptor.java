package kr.co.iteyes.fhir.jpa.security.interceptor;

import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.server.interceptor.auth.AuthorizationInterceptor;
import ca.uhn.fhir.rest.server.interceptor.auth.IAuthRule;
import ca.uhn.fhir.rest.server.interceptor.auth.RuleBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import kr.co.iteyes.fhir.jpa.security.exception.AuthenticationFailException;
import kr.co.iteyes.fhir.jpa.security.service.OAuthAuthorizeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class CustomAuthorizationInterceptor extends AuthorizationInterceptor {


	private static final Logger ourLog = LoggerFactory.getLogger(CustomAuthorizationInterceptor.class);

	private Logger myLogger = ourLog;

	private OAuthAuthorizeService oAuthAuthorizeService = OAuthAuthorizeService.getInstance();


	@Override
	public List<IAuthRule> buildRuleList(RequestDetails theRequestDetails) {
		String authHeader = theRequestDetails.getHeader("Authorization");

		String result = null;
		try{
			if (authHeader == null || authHeader.length() == 0 ){
				myLogger.error("Authorization Header is empty ::: {} ", authHeader);
				throw new AuthenticationFailException("인증을 위한 헤더 정보 부족 AuthHeader ::: " + authHeader);
			}
			myLogger.debug("Authorization token ::: {} ", authHeader);
			String splitToken = authHeader.split(" ")[1];
			result = oAuthAuthorizeService.checkAccessToken(splitToken);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (AuthenticationFailException e) {
			throw new RuntimeException(e);
		}

		RuleBuilder ruleBuilder = new RuleBuilder();
		if (result.equals("Y")) {
			return new RuleBuilder()
				.allowAll()
				.build();
		} else {
			// Throw an HTTP 401
			return new RuleBuilder()
				.denyAll()
				.build();
		}
	}
}