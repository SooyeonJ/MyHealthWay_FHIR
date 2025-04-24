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
public class ComAgntId implements Serializable {
    private String agntKdcd;
    private int agntVerNo;
}
