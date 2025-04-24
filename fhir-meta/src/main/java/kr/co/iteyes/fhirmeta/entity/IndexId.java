package kr.co.iteyes.fhirmeta.entity;

import javax.persistence.Column;
import lombok.*;

import java.io.Serializable;

@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class IndexId implements Serializable {
    @Column(length = 8)
    private String mhId;

    @Column(length = 8)
    private String cisn;
}
