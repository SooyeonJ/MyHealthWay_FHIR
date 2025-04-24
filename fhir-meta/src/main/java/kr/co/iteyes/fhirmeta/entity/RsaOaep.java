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
public class RsaOaep {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
                   generator = "RsaOaep_SEQ_GENERATOR")
    @SequenceGenerator(
            name = "RsaOaep_SEQ_GENERATOR",
            sequenceName = "RsaOaep_SEQ",
            allocationSize = 1)
    private Long id;

    @Column(length = 8)
    private String cisn;

    @Column(length = 10000)
    @ColumnTransformer(
            read = "pls_decrypt_b64_id( PUBLIC_KEY, 101 )",
            write = "pls_encrypt_b64_id( ?, 101 )"
    )
    private String publicKey;

    @ColumnTransformer(
            read = "pls_decrypt_b64_id( PRIVATE_KEY, 101 )",
            write = "pls_encrypt_b64_id( ?, 101 )"
    )
    @Column(length = 10000)
    private String privateKey;

    @Column(columnDefinition = "TIMESTAMP")
    private Timestamp createDt;

    private String issuRsnDvcd;
}
