package kr.co.iteyes.fhirmeta.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.ServerValidationModeEnum;
import ca.uhn.fhir.rest.client.interceptor.BearerTokenAuthInterceptor;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import kr.co.iteyes.fhirmeta.exception.CustomException;
import kr.co.iteyes.fhirmeta.exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Service
public class FhirService {

    @Value("${fhir.server.url}")
    private String fhirServerUrl;

    private final AuthService authService;

    public Bundle searchPatientByLastUpdated(Date baseDate) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = dateFormat.format(baseDate);

        FhirContext ctx  = FhirContext.forR4();
        ctx.getRestfulClientFactory().setServerValidationMode(ServerValidationModeEnum.NEVER);

        String token = authService.getAuthToken();
        BearerTokenAuthInterceptor authInterceptor = new BearerTokenAuthInterceptor(token);

        IGenericClient client = ctx.newRestfulGenericClient(fhirServerUrl);
        client.registerInterceptor(authInterceptor);
        client.registerInterceptor(new LoggingInterceptor());


        String searchUrl = "Patient?_elements=identifier&_lastUpdated=ge" + date;

        Bundle results = null;
        try {
            results = client
                    .search()
                    .byUrl(searchUrl)
                    .returnBundle(Bundle.class)
                    .count(1000)
                    .execute();

            Bundle bundle = new Bundle();
            for (Bundle.BundleEntryComponent bundleEntryComponent : results.getEntry()) {
                Resource resource = bundleEntryComponent.getResource();
                Bundle bundletmp = searchEncountByPatientId(resource.getIdElement().getIdPart());
                if (bundletmp.getTotal()>0) {
                    bundle.addEntry(bundleEntryComponent);
                }
            }
            results = bundle;

        } catch (Exception e) {
            if(e.getMessage().contains("403")) {
                throw new CustomException(ExceptionEnum.INTERNAL_AUTH_SERVER_ERROR);
            } else {
                log.error(e.getMessage(), e);
                throw new CustomException(ExceptionEnum.INTERNAL_SERVER_ERROR);
            }
        }
        return results;
    }

    public Bundle searchEncountByPatientId(String patientId) {

        FhirContext ctx  = FhirContext.forR4();
        ctx.getRestfulClientFactory().setServerValidationMode(ServerValidationModeEnum.NEVER);

        String token = authService.getAuthToken();
        BearerTokenAuthInterceptor authInterceptor = new BearerTokenAuthInterceptor(token);

        IGenericClient client = ctx.newRestfulGenericClient(fhirServerUrl);
        client.registerInterceptor(authInterceptor);
        client.registerInterceptor(new LoggingInterceptor());


        String searchUrl = "Encounter?subject=" + patientId;
        Bundle results = client
                .search()
                .byUrl(searchUrl)
                .returnBundle(Bundle.class)
                .count(1000)
                .execute();

        return results;
    }
}
