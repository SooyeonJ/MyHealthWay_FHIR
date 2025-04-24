package kr.co.iteyes.fhirmeta.entity;

import javax.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnTransformer;

import java.sql.Timestamp;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Extract {

    @EmbeddedId
    private ExtractId extractId;

    @Column(columnDefinition = "TIMESTAMP")
    private Timestamp createDt;

    @Column(columnDefinition = "TIMESTAMP")
    private Timestamp updateDt;

    @Column(length = 1)
    private String useYn;

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

    private String appId;
}
