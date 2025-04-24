package kr.co.iteyes.fhirmeta.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.*;

@Entity
@Table(name = "TFHR_PNSTEMR")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class PnstEmr {
    @Id
    private int emrSysNo;
    String useBgngYmd;
    String useEndYmd;
    @Column(length = 14, updatable = false)
    String regDt;
    @Column(length = 14)
    String mdfcnDt;
    String useYn;
    String cisn;
}
