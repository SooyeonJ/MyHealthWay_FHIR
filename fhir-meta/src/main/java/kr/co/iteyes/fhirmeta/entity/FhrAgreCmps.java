package kr.co.iteyes.fhirmeta.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.*;

@Entity
@Table(name = "TFHR_AGRECMPS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class FhrAgreCmps {
    @EmbeddedId
    private FhrAgreCmpsId id;
    private String crtrDt;
    @Column(length = 14, updatable = false)
    private String cmpsJobBgngDt;
    @Column(length = 14)
    private String cmpsJobEndDt;
    private int agreRcptnNocs;
    private int agreCrctNocs;
    private int rcnttCrctNocs;
    @Column(length = 14, updatable = false)
    private String regDt;
}
