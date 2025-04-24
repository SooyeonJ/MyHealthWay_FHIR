package kr.co.iteyes.fhirmeta.dto;

import kr.co.iteyes.fhirmeta.entity.Index;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Getter
public class IndexDto {
    @Getter
    public static class Request {
        private String userId;
        private String userName;
        private String utilizationServiceCode;
        private String baseDate;
        private List<String> provideInstitutionCodes;
        private String residentRegistrationNumber;

        public String getBaseDate() {
            try {
                return new SimpleDateFormat("yyyyMMdd").format(new SimpleDateFormat("yyyy-MM-dd").parse(baseDate));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return new SimpleDateFormat("yyyyMMdd").format(new Date());
            }
        }
    }

    @Getter
    @Builder
    public static class Response {
        private String userId;
        private String patientId;
        private String provideInstitutionCode;
        private String recentlyVisitDate;
        private String fhirPatientId;
    }

    public static List<IndexDto.Response> fromList(List<Index> indexList) {
        return indexList.stream().map(index -> from(index)).collect(Collectors.toList());
    }

    public static IndexDto.Response from(Index index) {
        if (index == null) return null;

        String fhirLastUpdatedYmd = index.getFhirLastUpdatedYmd();

        IndexDto.Response response = Response.builder()
                .userId(index.getMhId())
                .patientId(index.getPatientId())
                .provideInstitutionCode(index.getCisn())
                .recentlyVisitDate(fhirLastUpdatedYmd)
                .fhirPatientId(index.getFhirPatientResourceId())
                .build();
        return response;
    }

    @Getter
    public static class RequestForInsert {
        private String userId;
        private String utilizationServiceCode;
        private String patientId;
        private String provideInstitutionCode;
        private String careInstitutionSign;
    }

    @Getter
    public static class RequestForUpdate {
        private String patientId;
        private String recentlyVisitDate;
        private String registrationDate;
        private String fhirPatientId;
        private String fhirOrganizationId;
        private String userId;
    }

    @Getter
    public static class RequestForUpdatePost {
        private String patientId;
        private String recentlyVisitDate;
        private String registrationDate;
        private String fhirPatientId;
        private String fhirOrganizationId;
        private String userId;
    }

}
