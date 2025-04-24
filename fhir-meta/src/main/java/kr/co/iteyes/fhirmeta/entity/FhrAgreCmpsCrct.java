package kr.co.iteyes.fhirmeta.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.*;

@Entity
@Table(name = "TFHR_AGRECMPSCRCT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class FhrAgreCmpsCrct {
    @EmbeddedId
    private FhrAgreCmpsCrctId id;
    @Column(length = 1)
    private String crctScsYn;
    private String crctBfrAgreStcd;
    private String crctAftrAgreStcd;
    @Column(length = 14)
    private String crctDt;
}
