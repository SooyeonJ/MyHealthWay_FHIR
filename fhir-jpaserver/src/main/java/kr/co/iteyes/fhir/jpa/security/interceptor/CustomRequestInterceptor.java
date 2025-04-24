package kr.co.iteyes.fhir.jpa.security.interceptor;

import ca.uhn.fhir.interceptor.api.Hook;
import ca.uhn.fhir.interceptor.api.Pointcut;
import ca.uhn.fhir.rest.api.EncodingEnum;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.server.RestfulServerUtils;
import ca.uhn.fhir.rest.server.exceptions.AuthenticationException;
import ca.uhn.fhir.rest.server.method.ResourceParameter;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.iteyes.fhir.jpa.security.config.CryptoConfig;
import kr.co.iteyes.fhir.jpa.security.service.HttpRequestService;
import kr.co.iteyes.fhir.jpa.security.service.IHttpRequestService;
import kr.co.iteyes.fhir.jpa.security.util.MymdLz4Util;
import kr.co.iteyes.fhir.jpa.security.util.MymdSeedCtrUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.Charset;
import java.util.Map;

public class CustomRequestInterceptor {
	private static final org.slf4j.Logger ourLog = org.slf4j.LoggerFactory.getLogger(CustomRequestInterceptor.class);

	ObjectMapper mapper = new ObjectMapper();

	MymdLz4Util lz4Utils = new MymdLz4Util();

	IHttpRequestService httpRequestService = new HttpRequestService();


	// CryptoConfig start
	private static String rsaKeyRequestUrl;
	public void setRsa_key_request_url(String rsaKeyRequestUrl) {
		this.rsaKeyRequestUrl = rsaKeyRequestUrl;
	}



	// CryptoConfig end

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



	@Hook(Pointcut.SERVER_INCOMING_REQUEST_POST_PROCESSED)
	public void incomingRequestPostProcessed(RequestDetails theRequestDetails, HttpServletRequest theRequest, HttpServletResponse theResponse) throws AuthenticationException {
		EncodingEnum encoding = RestfulServerUtils.determineRequestEncodingNoDefault(theRequestDetails);
		String requestText = "";
		String cisn = theRequestDetails.getHeader("Cisn");
		try {
			if (theRequestDetails != null){

				Charset charset = ResourceParameter.determineRequestCharset(theRequestDetails);

				String mymdPublicKey = theRequestDetails.getHeader("mymdPublicKey");
				ourLog.debug(" CustomRequestInterceptor cisn ::: {}", cisn);
				ourLog.debug(" CustomRequestInterceptor mymdPublicKey ::: {}", mymdPublicKey);

				String authHeader = theRequestDetails.getHeader("Authorization");
				String splitToken = authHeader.split(" ")[1];
				ourLog.debug(" CustomRequestInterceptor authHeader ::: {}", authHeader);
				ourLog.debug(" CustomRequestInterceptor splitToken ::: {}", splitToken);

				if (mymdPublicKey != null || cisn != null || splitToken != null){
					requestText = new String(theRequestDetails.loadRequestContents(), charset);
					if (requestText != null && requestText.length() > 1 ) {

						String result = null;

						if (cisn != null && cisn.length() >0){
							result = 	httpRequestService.restTemplateRequestWithAuth(rsaKeyRequestUrl+cisn,splitToken);
						}else{
							result = 	httpRequestService.restTemplateRequestWithAuth(rsaKeyRequestUrl+CryptoConfig.BASE_CISN , splitToken);
						}

						ourLog.debug(" CustomRequestInterceptor SeedKey Request result ::: {}", result); // temp 로그
						if (result == null || result.length() <= 0){
							throw new RuntimeException("SEED 키 요청에 실패하였습니다 - 1");
						}
						Map<String, String> map = mapper.readValue(result, Map.class);

						String symmetricKey = null;
						symmetricKey = map.get("key");

						ourLog.debug(" CustomRequestInterceptor symmetricKey ::: {}", symmetricKey); // temp 로그

						if(symmetricKey == null || symmetricKey.length() <= 0){
							throw new RuntimeException("SEED 키 요청에 실패하였습니다 - 2");
						}
//						ourLog.info("Request Text : " , requestText);
						byte[] decryptedData = MymdSeedCtrUtil.SEED_CTR_Decrypt_byte(symmetricKey, requestText);

						byte[] decompressedData = lz4Utils.decompress(decryptedData);

//						ourLog.info("Request decompressedData : " , new String(decompressedData));
						theRequestDetails.setRequestContents(decompressedData);
					}
				}
			}

			ourLog.debug(" incomingRequestPostProcessed finish ::: "); // temp 로그

		} catch (Exception e) {
			ourLog.error(" CustomRequestInterceptor cisn ::: {}", cisn);
			ourLog.error(" CustomRequestInterceptor requestText ::: {}", requestText);

			ourLog.error(e.getMessage());
			throw new RuntimeException(e);
		}
	}



}
