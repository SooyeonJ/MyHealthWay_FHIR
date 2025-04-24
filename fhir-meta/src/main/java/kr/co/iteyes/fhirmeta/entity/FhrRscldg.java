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
@Table(name = "TFHR_RSCLDG")
public class FhrRscldg {
    @EmbeddedId
    private FhrRscldgId fhrRscldgId;
    private int ldgNocs;
    private int errNocs;
    private String regDt;
}
