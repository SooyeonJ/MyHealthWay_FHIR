package kr.co.iteyes.fhirmeta.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.*;

@Entity(name = "TFHR_AGNTINSTLDMND")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class AgntInstlDmnd {
    @Id
    private int instlDmndNo;
    int instlFileNo;
    String instlBgngPrnmntDt;
    String instlDmndDt;
    @Column(length = 14, updatable = false)
    String regDt;
    @Column(length = 14)
    String mdfcnDt;
}
