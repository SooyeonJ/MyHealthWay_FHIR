package kr.co.iteyes.fhirmeta.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Table(name = "TFHR_DTAERR")
public class FhrDtaerr {
    @EmbeddedId
    private FhrDtaerrId fhrDtaerrId;
    private String ldgYmd;
    private String cisn;
    private String fhirRscTpcd;
    private int fhirRscNo;
    private String errCd;
    private String errCn;
    private String regDt;
}
