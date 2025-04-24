package kr.co.iteyes.fhirmeta.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class ServerStatus {

    @Id
    private String cisn;

    private String fhirLastUpdatedYmd;
}
