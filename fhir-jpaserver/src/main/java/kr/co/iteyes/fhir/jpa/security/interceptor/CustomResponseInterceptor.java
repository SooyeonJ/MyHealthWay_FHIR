package kr.co.iteyes.fhir.jpa.security.interceptor;

import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.i18n.Msg;
import ca.uhn.fhir.interceptor.api.Hook;
import ca.uhn.fhir.interceptor.api.Pointcut;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.api.EncodingEnum;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.api.server.ResponseDetails;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.RestfulServerUtils;
import ca.uhn.fhir.rest.server.exceptions.InternalErrorException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.iteyes.fhir.jpa.security.config.OAuthConfig;
import kr.co.iteyes.fhir.jpa.security.service.HttpRequestService;
import kr.co.iteyes.fhir.jpa.security.service.IHttpRequestService;
import kr.co.iteyes.fhir.jpa.security.util.MymdLz4Util;
import kr.co.iteyes.fhir.jpa.security.util.MymdRsaUtil;
import kr.co.iteyes.fhir.jpa.security.util.MymdSeedCtrUtil;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.Set;

public class CustomResponseInterceptor {

	private static final Logger ourLog = LoggerFactory.getLogger(CustomResponseInterceptor.class);

	MymdLz4Util lz4Utils = new MymdLz4Util();

	ObjectMapper mapper = new ObjectMapper();

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



	@Hook(Pointcut.SERVER_OUTGOING_RESPONSE)
	public void outgoingResponse(RequestDetails theRequestDetails, ResponseDetails theResponseObject,IBaseResource iBaseResource, HttpServletRequest theServletRequest, HttpServletResponse theServletResponse) {

		boolean isValid = valideOutgoingResponse(theRequestDetails,theResponseObject,theServletRequest, theServletResponse);
		if(isValid){
			streamResponse(theRequestDetails,theServletResponse,iBaseResource,theServletRequest, 200);
		}
	}
	private boolean valideOutgoingResponse(RequestDetails theRequestDetails, ResponseDetails theResponseObject, HttpServletRequest theServletRequest, HttpServletResponse theServletResponse) {

		Set<String> highestRankedAcceptValues = RestfulServerUtils.parseAcceptHeaderAndReturnHighestRankedOptions(theServletRequest);
		if (highestRankedAcceptValues.contains(OAuthConfig.getResponseAcceptEncoding()) ) {
			return true;
		}
		return true;

	}


	private void streamResponse(RequestDetails theRequestDetails, HttpServletResponse theServletResponse, IBaseResource theResource,  ServletRequest theServletRequest, int theStatusCode) {
		EncodingEnum encoding;
		String encoded;
		Map<String, String[]> parameters = theRequestDetails.getParameters();

		IParser p;
		if (parameters.containsKey(Constants.PARAM_FORMAT)) {
			FhirVersionEnum forVersion = theResource.getStructureFhirVersionEnum();
			p = RestfulServerUtils.getNewParser(theRequestDetails.getServer().getFhirContext(), forVersion, theRequestDetails);
		} else {
			EncodingEnum defaultResponseEncoding = theRequestDetails.getServer().getDefaultResponseEncoding();
			p = defaultResponseEncoding.newParser(theRequestDetails.getServer().getFhirContext());
			RestfulServerUtils.configureResponseParser(theRequestDetails, p);
		}

		// This interceptor defaults to pretty printing unless the user
		// has specifically requested us not to
		boolean prettyPrintResponse = true;
		String[] prettyParams = parameters.get(Constants.PARAM_PRETTY);
		if (prettyParams != null && prettyParams.length > 0) {
			if (Constants.PARAM_PRETTY_VALUE_FALSE.equals(prettyParams[0])) {
				prettyPrintResponse = false;
			}
		}
		if (prettyPrintResponse) {
			p.setPrettyPrint(true);
		}

		encoded = p.encodeResourceToString(theResource);

		if (theRequestDetails.getServer() instanceof RestfulServer) {
			RestfulServer rs = (RestfulServer) theRequestDetails.getServer();
			rs.addHeadersToResponse(theServletResponse);
		}

		String publicKey = theRequestDetails.getHeader("mymdPublicKey");
		String serverDomainNo = theRequestDetails.getHeader("serverDomainNo");
		String cisn = theRequestDetails.getHeader("Cisn");
		String authHeader = theRequestDetails.getHeader("Authorization");
		String splitToken = authHeader.split(" ")[1];

		ourLog.debug(" publicKey ::: {}", publicKey);
		ourLog.debug(" serverDomainNo ::: {}", serverDomainNo);
		ourLog.debug(" cisn ::: {}", cisn);
		ourLog.debug(" authHeader ::: {}", authHeader);
		ourLog.debug(" splitToken ::: {}", splitToken);
		ourLog.debug(" rsaKeyRequestUrl ::: {}", rsaKeyRequestUrl);


		if (serverDomainNo != null && serverDomainNo.length() > 0){
			theServletResponse.setHeader("serverDomainNo", serverDomainNo);
		}


		try{
			// 플랫폼
			if ( publicKey != null && publicKey.length() > 0 ) {
				byte[] bytePublicKey = Base64.getDecoder().decode(publicKey);
				String strPublicKey = new String(bytePublicKey);
				byte[] compressedData = lz4Utils.compress(encoded.getBytes());
				byte[] encryptedText = MymdRsaUtil.encrypt(compressedData, strPublicKey);

				if (encryptedText != null){
					String strCompressedBundle = DatatypeConverter.printBase64Binary(encryptedText);
					theServletResponse.setStatus(theStatusCode);
					theServletResponse.setContentType(Constants.CT_FHIR_JSON_NEW);
					StringBuilder outputBuffer = new StringBuilder();
					outputBuffer.append(strCompressedBundle);
					theServletResponse.getWriter().append(outputBuffer);
					theServletResponse.getWriter().flush();
					theServletResponse.getWriter().close(); 
				}else {
					throw new RuntimeException("Data를 찾을 수 없습니다. ");
				}
			}

			// 의료기관 agent
			if( cisn != null && cisn.length() >0 && splitToken!= null){
				byte[] compressedData = lz4Utils.compress(encoded.getBytes());
				String result = null;

				result = httpRequestService.restTemplateRequestWithAuth(rsaKeyRequestUrl+cisn, splitToken);

				if (result == null || result.length() <= 0){
					throw new RuntimeException("SEED 키 요청에 실패하였습니다. - 1");
				}
				Map<String, String> map = mapper.readValue(result, Map.class);

				String symmetricKey = null;
				symmetricKey = map.get("key");
				if(symmetricKey == null || symmetricKey.length() <= 0){
					throw new RuntimeException("SEED 키 요청에 실패하였습니다. - 2");
				}
				ourLog.debug(" CustomResponseInterceptor symmetricKey ::: {}", symmetricKey);

				byte[] encryptedText = MymdSeedCtrUtil.SEED_CTR_Encrypt_byte(symmetricKey, compressedData);
				if (encryptedText != null){
					String strCompressedBundle = DatatypeConverter.printBase64Binary(encryptedText);
					theServletResponse.setStatus(theStatusCode);
					theServletResponse.setContentType(Constants.CT_FHIR_JSON_NEW);
					StringBuilder outputBuffer = new StringBuilder();
					outputBuffer.append(strCompressedBundle);
					theServletResponse.getWriter().append(outputBuffer);
					theServletResponse.getWriter().flush(); 
					theServletResponse.getWriter().close(); 
				}else {
					throw new RuntimeException("Data를 찾을 수 없습니다. ");
				}
			}

			ourLog.debug(" streamResponse finish ::: "); // temp 로그

		} catch (IOException e) {
			e.printStackTrace();
			throw new InternalErrorException(Msg.code(322) + e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

}
