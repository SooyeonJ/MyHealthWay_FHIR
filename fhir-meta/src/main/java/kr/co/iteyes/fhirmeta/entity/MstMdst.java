package kr.co.iteyes.fhirmeta.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.*;

@Entity(name = "TMST_MDST")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class MstMdst {
    @Column(length = 8)
    @Id
    private String cisn;
    @Column(length = 6)
    private String pnstNo;
    private String careInstNm;
    private String emrDvcd;
}
