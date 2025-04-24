package kr.co.iteyes.fhirmeta.entity;

import javax.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class EncryptKey {

    @EmbeddedId
    private EncryptKeyId encryptKeyId;

    @OneToOne
    @JoinColumn(name = "seed_ctr_id", referencedColumnName = "id")
    private SeedCtr seedCtr;

    @OneToOne
    @JoinColumn(name = "rsa_oaep_id", referencedColumnName = "id")
    private RsaOaep rsaOaep;

    @Column(columnDefinition = "TIMESTAMP")
    private Timestamp validDt;

    @Column(columnDefinition = "TIMESTAMP", insertable = true, updatable = false)
    private Timestamp createDt;

    @Column(columnDefinition = "TIMESTAMP")
    private Timestamp updateDt;
}
