package kr.co.iteyes.fhirmeta.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import kr.co.iteyes.fhirmeta.entity.Statistics;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StatisticsDto {
    @Getter
    public static class Request {
        @JsonFormat(pattern="yyyy-MM-dd")
        private Date baseDate;
    }

    @Getter
    @Builder
    public static class Response {
        private String careInstitutionSign;
        private Long patient;
        private Long organization;
        private Long practitioner;
        private Long condition;
        private Long medicationRequest;
        private Long observationLaboratory;
        private Long observationExam;
        private Long imagingDiagnosticReport;
        private Long pathologyDiagnosticReport;
        private Long procedure;
        private Long allergyIntolerance;
        private Long documentReference;
        private Long practitionerRole;
        private Long encounter;
        private Long imagingstudy;
        private Long media;
        private Long endpoint;
        private Long patientTotalCount;
        private Long organizationTotalCount;
        private Long practitionerTotalCount;
        private Long conditionTotalCount;
        private Long medicationRequestTotalCount;
        private Long observationLaboratoryTotalCount;
        private Long observationExamTotalCount;
        private Long imagingDiagnosticReportTotalCount;
        private Long pathologyDiagnosticReportTotalCount;
        private Long procedureTotalCount;
        private Long allergyIntoleranceTotalCount;
        private Long documentReferenceTotalCount;
        private Long practitionerRoleTotalCount;
        private Long encounterTotalCount;
        private Long imagingstudyTotalCount;
        private Long mediaTotalCount;
        private Long endpointTotalCount;

        public static List<Response> fromList(List<Statistics> statisticsList) {
            List<Response> result = new ArrayList<>();

            statisticsList.forEach(statistics -> result.add(
                    Response.from(statistics)
            ));
            return result;
        }

        private static Response from(Statistics statistics) {
            return Response.builder()
                    .careInstitutionSign(statistics.getStatisticsId().getCisn())
                    .patient(statistics.getPatient())
                    .organization(statistics.getOrganization())
                    .practitioner(statistics.getPractitioner())
                    .condition(statistics.getCondition())
                    .medicationRequest(statistics.getMedicationRequest())
                    .observationLaboratory(statistics.getObservationLaboratory())
                    .observationExam(statistics.getObservationExam())
                    .imagingDiagnosticReport(statistics.getImagingDiagnosticReport())
                    .pathologyDiagnosticReport(statistics.getPathologyDiagnosticReport())
                    .procedure(statistics.getProcedure())
                    .allergyIntolerance(statistics.getAllergyIntolerance())
                    .documentReference(statistics.getDocumentReference())
                    .practitionerRole(statistics.getPractitionerRole())
                    .encounter(statistics.getEncounter())
                    .imagingstudy(statistics.getImagingstudy())
                    .media(statistics.getMedia())
                    .endpoint(statistics.getEndpoint())
                    .patientTotalCount(statistics.getPatientTotalCount())
                    .organizationTotalCount(statistics.getOrganizationTotalCount())
                    .practitionerTotalCount(statistics.getPractitionerTotalCount())
                    .conditionTotalCount(statistics.getConditionTotalCount())
                    .medicationRequestTotalCount(statistics.getMedicationRequestTotalCount())
                    .observationLaboratoryTotalCount(statistics.getObservationLaboratoryTotalCount())
                    .observationExamTotalCount(statistics.getObservationExamTotalCount())
                    .imagingDiagnosticReportTotalCount(statistics.getImagingDiagnosticReportTotalCount())
                    .pathologyDiagnosticReportTotalCount(statistics.getPathologyDiagnosticReportTotalCount())
                    .procedureTotalCount(statistics.getProcedureTotalCount())
                    .allergyIntoleranceTotalCount(statistics.getAllergyIntoleranceTotalCount())
                    .documentReferenceTotalCount(statistics.getDocumentReferenceTotalCount())
                    .practitionerRoleTotalCount(statistics.getPractitionerRoleTotalCount())
                    .encounterTotalCount(statistics.getEncounterTotalCount())
                    .imagingstudyTotalCount(statistics.getImagingstudyTotalCount())
                    .mediaTotalCount(statistics.getMediaTotalCount())
                    .endpointTotalCount(statistics.getEndpointTotalCount())
                    .build();
        }
    }
}
