package kr.co.iteyes.fhirmeta.entity;

import javax.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnTransformer;

import java.sql.Timestamp;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Consent {
    @EmbeddedId
    private ConsentId consentId;
    @Lob
    private String cisnList;
    @Column(length = 1)
    private String destructYn;
    @Column(columnDefinition = "TIMESTAMP")
    private Timestamp agreeDt;
    @Column(columnDefinition = "TIMESTAMP")
    private Timestamp disagreeDt;
    @Column(columnDefinition = "TIMESTAMP")
    private Timestamp mhSendDt;
    @Column(columnDefinition = "TIMESTAMP")
    private Timestamp createDt;
    @Column(columnDefinition = "TIMESTAMP")
    private Timestamp updateDt;

    // 신환환자 로직때문에 추가됨
    @ColumnTransformer(
            read = "pls_decrypt_b64_id( USER_NM, 101 )",
            write = "pls_encrypt_b64_id( ?, 101 )"
    )
    private String userNm;

    @ColumnTransformer(
            read = "pls_decrypt_b64_id( RRNO, 101 )",
            write = "pls_encrypt_b64_id( ?, 101 )"
    )
    private String rrno;

    @Column(columnDefinition = "DATE")
    private LocalDate destructDt;

    public void setDisagreeDt(Timestamp disagreeDt) {
        this.disagreeDt = disagreeDt;
    }

    public void setMhSendDt(Timestamp mhSendDt) {
        this.mhSendDt = mhSendDt;
    }

    public void setUpdateDt(Timestamp updateDt) {
        this.updateDt = updateDt;
    }

    public void setCreateDt(Timestamp createDt) {
        this.createDt = createDt;
    }

    public void setCisnList(String cisnList) {
        this.cisnList = cisnList;
    }
}
