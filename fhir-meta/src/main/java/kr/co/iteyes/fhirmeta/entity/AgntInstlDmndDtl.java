package kr.co.iteyes.fhirmeta.entity;

import javax.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "TFHR_AGNTINSTLDMND_DTL")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class AgntInstlDmndDtl {
    @EmbeddedId
    private AgntInstlDmndDtlId id;
    String dwnldUrlAddr;
    @Column(length = 1)
    @ColumnDefault("N")
    String instlYn;
    @ColumnDefault(" ")
    String agntInstlStcd;
    @Column(length = 8)
    String instlYmd;
}
