package kr.co.iteyes.fhirmeta.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@Getter
public class AgntInstlDmndDtlId implements Serializable {
    private int instlDmndNo;
    @Column(length = 8)
    private String cisn;
}
