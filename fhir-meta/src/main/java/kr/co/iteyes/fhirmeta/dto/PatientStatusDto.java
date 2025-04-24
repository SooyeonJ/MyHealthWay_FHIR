package kr.co.iteyes.fhirmeta.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.hl7.fhir.r4.model.Base;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Property;
import org.hl7.fhir.r4.model.Resource;

import java.util.*;


public class PatientStatusDto {

    @Getter
    public static class Request {
        @JsonFormat(pattern="yyyy-MM-dd")
        private Date baseDate;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Response {
        private String utilizationUserNo;
        private List<careInstitutionSignResult> careInstitutionSignResultList;

        public static List<Response> from(Bundle bundle) {
            // Map<mhId, List<identifier>>
            Map<String, List<Property>> resultMap = new HashMap<>();

            for (Bundle.BundleEntryComponent bundleEntryComponent : bundle.getEntry()) {

                Resource resource = bundleEntryComponent.getResource();
                Property property = resource.getNamedProperty("identifier");

                for (Base base : property.getValues()) {
                    for (Base codingBase : base.getNamedProperty("type").getValues().get(0).getNamedProperty("coding").getValues()) {
                        String code = codingBase.getNamedProperty("code").getValues().get(0).toString();
                        if("MHID".equals(code)) {
                            String mhid = base.getNamedProperty("value").getValues().get(0).toString();
                            if(resultMap.get(mhid) == null) {
                                resultMap.put(mhid, new ArrayList<>(Arrays.asList(property)));
                            } else {
                                List<Property> properties = resultMap.get(mhid);
                                properties.add(property);
                                resultMap.put(mhid, properties);
                            }
                        }
                    }
                }
            }

            List<Response> responses = new ArrayList<>();

            for (Map.Entry<String, List<Property>> entry : resultMap.entrySet()) {
                List<PatientStatusDto.careInstitutionSignResult> careInstitutionSignResultList = new ArrayList<>();
                List<Property> properties = entry.getValue();
                for (Property property : properties) {
                    for (Base base : property.getValues()) {
                        Property systemProperty = base.getNamedProperty("system");
                        Property valueProperty = base.getNamedProperty("value");
                        if (systemProperty.hasValues() && systemProperty.getValues().size() > 0) {
                            String system = base.castToUri(systemProperty.getValues().get(0)).getValue();
                            String[] systemArr = system.split("\\.");
                            if (system.contains("urn:oid") && systemArr.length == 6 && valueProperty.hasValues() && valueProperty.getValues().size() > 0) {
                                String value = valueProperty.getValues().get(0).toString();
                                PatientStatusDto.careInstitutionSignResult careInstitutionSignResult = new PatientStatusDto.careInstitutionSignResult(systemArr[5], value);
                                careInstitutionSignResultList.add(careInstitutionSignResult);
                            }
                        }
                    }
                }
                PatientStatusDto.Response response = PatientStatusDto.Response.builder()
                        .utilizationUserNo(entry.getKey())
                        .careInstitutionSignResultList(careInstitutionSignResultList)
                        .build();
                responses.add(response);
            }
            return responses;
        }
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class careInstitutionSignResult {
        private String careInstitutionSign;
        private String patientId;
    }
}
