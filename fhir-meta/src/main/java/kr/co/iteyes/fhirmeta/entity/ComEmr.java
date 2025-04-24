package kr.co.iteyes.fhirmeta.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.*;

@Entity(name = "TCOM_EMR")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class ComEmr {
    @Id
    private int emrSysNo;
    private String emrSysNm;
    private String emrSysVerNm;
    private String dbmsKdcd;
    private String dbmsVerNm;
    private String emrTpcd;
    private String regStcd;
    @Column(length = 14, updatable = false)
    private String regDt;
    @Column(length = 14)
    private String mdfcnDt;
    private String emrBzentyCd;
}
