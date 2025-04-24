package kr.co.iteyes.fhir.jpa.security.interceptor;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.interceptor.RequestValidatingInterceptor;
import ca.uhn.fhir.rest.server.interceptor.ResponseValidatingInterceptor;
import ca.uhn.fhir.validation.ResultSeverityEnum;
import org.hl7.fhir.common.hapi.validation.validator.FhirInstanceValidator;

//@WebServlet(urlPatterns = {"/fhir/*"}, displayName = "FHIR Server")
public class ValidateServerWithLogging extends RestfulServer {

	@Override
	protected void initialize(){

		FhirContext ctx =FhirContext.forR4();

		setFhirContext(ctx);

		//Define your resource providers

		//Create an interceptor to validate incoming requests
		RequestValidatingInterceptor requestInterceptor = new RequestValidatingInterceptor();

		//Register a validator module (you could also use SchemaBaseValidator and/or SchematronBaseValidator)
		requestInterceptor.addValidatorModule(new FhirInstanceValidator(ctx));

		requestInterceptor.setFailOnSeverity(ResultSeverityEnum.ERROR);

		requestInterceptor.setAddResponseHeaderOnSeverity(ResultSeverityEnum.INFORMATION);
		requestInterceptor.setResponseHeaderValue("Validation on ${line}: ${message} ${severity}");
		requestInterceptor.setResponseHeaderValueNoIssues("No issues detected");

		// Now register the validating interceptor
		registerInterceptor(requestInterceptor);

		//Create an interceptor to validate responses
		//This is configured in the same way as above
		ResponseValidatingInterceptor responseInterceptor = new ResponseValidatingInterceptor();
		responseInterceptor.addValidatorModule(new FhirInstanceValidator(ctx));

		responseInterceptor.setFailOnSeverity(ResultSeverityEnum.ERROR);
		responseInterceptor.setAddResponseHeaderOnSeverity(ResultSeverityEnum.INFORMATION);

		responseInterceptor.setResponseHeaderValue("Validation on ${line}: ${message} ${severity}");
		responseInterceptor.setResponseHeaderValueNoIssues("No issues detected");

		registerInterceptor(responseInterceptor);

	}

}
