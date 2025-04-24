package kr.co.iteyes.fhirmeta.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.*;

@Entity
@Table(name = "TFHR_AGRECMPSRCPTN")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class FhrAgreCmpsRcptn {
    @EmbeddedId
    private FhrAgreCmpsRcptnId id;
}
