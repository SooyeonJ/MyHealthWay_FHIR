package kr.co.iteyes.fhir.jpa.security.service;

public interface IHttpRequestService {

//	public String restTemplateRequest(String targetURL);
	public String  restTemplateRequestAuth(String targetURL,String clientId, String secretKey);

	public String restTemplateRequestWithAuth(String targetURL,  String auth);


}
