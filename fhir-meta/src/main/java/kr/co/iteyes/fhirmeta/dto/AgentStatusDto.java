package kr.co.iteyes.fhirmeta.dto;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class AgentStatusDto {
    // publicAgentVer
    private String agentVer;
    // privateAgentVer
    private String gatewayVer;
}
