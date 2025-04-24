package kr.co.iteyes.fhirmeta.entity;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnTransformer;

import java.sql.Timestamp;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeedCtr {

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

    @ColumnTransformer(
            read = "pls_decrypt_b64_id( KEY, 101 )",
            write = "pls_encrypt_b64_id( ?, 101 )"
    )
    private String key;

    @Column(columnDefinition = "TIMESTAMP")
    private Timestamp createDt;

    @Column(columnDefinition = "TIMESTAMP")
    private Timestamp validDt;
    private Integer issuDmndNo;
    @Column(length = 8)
    private String aprvYmd;
    @Column(length = 6)
    private String pnstNo;

    private String issuRsnDvcd;
}
