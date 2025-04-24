package kr.co.iteyes.fhirmeta.entity;

import javax.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@Getter
public class FhrAgreCmpsId implements Serializable {
    private int stptNo;
    private int cmpsJobNo;
}
