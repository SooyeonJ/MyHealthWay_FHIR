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
public class FhrPnstAgntChkId implements Serializable {
    @Column(length = 8)
    private String cisn;
    private String agntKdcd;
    private int agntVerNo;
    @Column(length = 14)
    private String chckDt;
}
