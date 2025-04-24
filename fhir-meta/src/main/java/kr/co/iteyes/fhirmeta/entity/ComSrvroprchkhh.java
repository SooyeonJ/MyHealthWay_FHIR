package kr.co.iteyes.fhirmeta.entity;

import javax.persistence.*;
import kr.co.iteyes.fhirmeta.utils.DateUtils;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Table(name = "TCOM_SRVROPRCHKHH")
public class ComSrvroprchkhh {
    @EmbeddedId
    private ComSrvroprchkhhId comSrvroprchkhhId;
    private Double cpuUsgrt;
    private Double mmryUsgrt;
    private Double diskUsgrt;
    @Column(length = 14)
    private String regDt;

    @PrePersist
    protected void onCreate() {
        if (this.regDt == null) {
            this.regDt = DateUtils.formatLocalDateTime(LocalDateTime.now());
        }
    }
}
