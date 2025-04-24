package kr.co.iteyes.fhirmeta.entity;

import javax.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Table(name = "TFHR_PBAGNTCONF")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class PbAgntConf {
    @Column(length = 8)
    @Id
    private String cisn;
    private String fhirSrvrDomnAddr;
    private String prvtagntDomnAddr;
    @Column(columnDefinition = "TIMESTAMP", name = "THRD_1_BGNG_TM")
    private Timestamp thrd1BgngTm;
    @Column(columnDefinition = "TIMESTAMP", name = "THRD_1_END_TM")
    private Timestamp thrd1EndTm;
    @Column(name = "THRD_1_FLFMT_CY")
    private int thrd1FlfmtCy;
    @Column(columnDefinition = "TIMESTAMP", name = "THRD_2_BGNG_TM")
    private Timestamp thrd2BgngTm;
    @Column(columnDefinition = "TIMESTAMP", name = "THRD_2_END_TM")
    private Timestamp thrd2EndTm;
    @Column(name = "THRD_2_FLFMT_CY")
    private int thrd2FlfmtCy;
    @Column(columnDefinition = "TIMESTAMP", name = "THRD_3_BGNG_TM")
    private Timestamp thrd3BgngTm;
    @Column(columnDefinition = "TIMESTAMP", name = "THRD_3_END_TM")
    private Timestamp thrd3EndTm;
    @Column(name = "THRD_3_FLFMT_CY")
    private int thrd3FlfmtCy;
    private int frstLdgPrd;
    private int ldgInqCy;
    private String clntId;
    private String clntSrtVal;
    private String pbagntInstlPath;
    private String osKdcd;
    private String useYn;
    @Column(length = 14)
    private String regDt;
    @Column(length = 14)
    private String mdfcnDt;
}
