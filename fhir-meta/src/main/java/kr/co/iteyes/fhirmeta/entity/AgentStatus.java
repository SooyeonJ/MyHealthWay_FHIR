package kr.co.iteyes.fhirmeta.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class AgentStatus {

    @Id
    private String cisn;
    @Column(name = "agentVer")
    private String publicAgentVer;
    @Column(name = "gatewayVer")
    private String privateAgentVer;
    @Column(columnDefinition = "TIMESTAMP")
    private Timestamp updateDt;
}
