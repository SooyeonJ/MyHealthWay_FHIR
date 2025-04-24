package kr.co.iteyes.fhirmeta.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class PhrDbValLogId implements Serializable {
    @Column(length = 8)
    private String cisn;

    private String logId;
}
