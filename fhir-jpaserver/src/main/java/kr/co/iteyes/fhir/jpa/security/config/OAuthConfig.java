package kr.co.iteyes.fhir.jpa.security.config;


public class OAuthConfig {
	// application.yaml에서 설정
	private static String OAUTH_SERVER_PORT = "";

	private static String RESPONSE_ACCEPT_ENCODING = "application/json+encrypt";

	private static String RESPONSE_ACCEPT = "encrypt/fhir+json";

	private static String OAUTH_CREATE_TOKEN = "/auth/token";

	private static String OAUTH_CHECK_TOKEN = "/auth/check";

	public static String getResponseAcceptEncoding() {
		return RESPONSE_ACCEPT_ENCODING;
	}

	public static String getResponseAccept() {
		return RESPONSE_ACCEPT;
	}

	public static String getOauthServerPort() {
		return OAUTH_SERVER_PORT;
	}

	public static String getOauthCreateToken() {
		return OAUTH_CREATE_TOKEN;
	}

	public static String getOauthCheckToken() {
		return OAUTH_CHECK_TOKEN;
	}
}
