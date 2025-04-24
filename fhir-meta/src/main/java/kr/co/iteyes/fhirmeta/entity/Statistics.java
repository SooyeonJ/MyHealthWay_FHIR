package kr.co.iteyes.fhirmeta.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.sql.Timestamp;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Statistics {

    @EmbeddedId
    private StatisticsId statisticsId;
    private String hspName;
    @ColumnDefault("0")
    private Long patient;
    @ColumnDefault("0")
    private Long organization;
    @ColumnDefault("0")
    private Long practitioner;
    @ColumnDefault("0")
    private Long condition;
    @ColumnDefault("0")
    private Long medicationRequest;
    @ColumnDefault("0")
    private Long observationLaboratory;
    @ColumnDefault("0")
    private Long observationExam;
    @ColumnDefault("0")
    private Long imagingDiagnosticReport;
    @ColumnDefault("0")
    private Long pathologyDiagnosticReport;
    @ColumnDefault("0")
    private Long procedure;
    @ColumnDefault("0")
    private Long allergyIntolerance;
    @ColumnDefault("0")
    private Long documentReference;
    @ColumnDefault("0")
    private Long practitionerRole;
    @ColumnDefault("0")
    private Long encounter;
    @ColumnDefault("0")
    private Long imagingstudy;
    @ColumnDefault("0")
    private Long media;
    @ColumnDefault("0")
    private Long endpoint;
    @ColumnDefault("0")
    private Long patientTotalCount;
    @ColumnDefault("0")
    private Long organizationTotalCount;
    @ColumnDefault("0")
    private Long practitionerTotalCount;
    @ColumnDefault("0")
    private Long conditionTotalCount;
    @ColumnDefault("0")
    private Long medicationRequestTotalCount;
    @ColumnDefault("0")
    private Long observationLaboratoryTotalCount;
    @ColumnDefault("0")
    private Long observationExamTotalCount;
    @ColumnDefault("0")
    private Long imagingDiagnosticReportTotalCount;
    @ColumnDefault("0")
    private Long pathologyDiagnosticReportTotalCount;
    @ColumnDefault("0")
    private Long procedureTotalCount;
    @ColumnDefault("0")
    private Long allergyIntoleranceTotalCount;
    @ColumnDefault("0")
    private Long documentReferenceTotalCount;
    @ColumnDefault("0")
    private Long practitionerRoleTotalCount;
    @ColumnDefault("0")
    private Long encounterTotalCount;
    @ColumnDefault("0")
    private Long imagingstudyTotalCount;
    @ColumnDefault("0")
    private Long mediaTotalCount;
    @ColumnDefault("0")
    private Long endpointTotalCount;
    @Column(columnDefinition = "TIMESTAMP")
    private Timestamp createDt;
}
