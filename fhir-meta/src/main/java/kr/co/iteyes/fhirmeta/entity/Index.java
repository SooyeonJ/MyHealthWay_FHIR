package kr.co.iteyes.fhirmeta.entity;

import javax.persistence.*;
import kr.co.iteyes.fhirmeta.dto.IndexDto;
import lombok.*;

@IdClass(IndexId.class)
@Entity(name = "IDX")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Index {
    @Id
    @Column(length = 8)
    private String mhId;
    @Id
    @Column(length = 8)
    private String cisn;
    private String patientId;
    private String fhirLastUpdatedYmd;
    private String fhirPatientResourceId;
    private String fhirOrganizationResourceId;
    private String createYmd;

    public void updateAll(IndexDto.RequestForUpdate request) {
        this.fhirLastUpdatedYmd = request.getRecentlyVisitDate();
        this.fhirPatientResourceId = request.getFhirPatientId();
        this.fhirOrganizationResourceId = request.getFhirOrganizationId();
//        this.createYmd = request.getRegistrationDate();
    }

    public void updateAll(IndexDto.RequestForUpdatePost request) {
        this.fhirLastUpdatedYmd = request.getRecentlyVisitDate();
        this.fhirPatientResourceId = request.getFhirPatientId();
        this.fhirOrganizationResourceId = request.getFhirOrganizationId();
//        this.createYmd = request.getRegistrationDate();
    }

    public void update(IndexDto.RequestForUpdate request) {
        this.fhirLastUpdatedYmd = request.getRecentlyVisitDate();
    }

    public void update(IndexDto.RequestForUpdatePost request) {
        this.fhirLastUpdatedYmd = request.getRecentlyVisitDate();
    }
}
