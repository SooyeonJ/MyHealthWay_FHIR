package kr.co.iteyes.fhirmeta.entity;

import javax.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class FhrUpdtDmndId implements Serializable {
    private Long stptNo;
    private Long updateDmndNo;
}
