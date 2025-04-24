package kr.co.iteyes.fhirmeta.service;

import org.apache.commons.lang3.StringUtils;
import kr.co.iteyes.fhirmeta.dto.AgentStatusDto;
import kr.co.iteyes.fhirmeta.entity.AgentStatus;
import kr.co.iteyes.fhirmeta.repository.AgentStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AgentStatusService {

    private final AgentStatusRepository agentStatusRepository;


    public void createAgentStatus(String cisn, AgentStatusDto agentStatusDto) {
        Timestamp now = new Timestamp(System.currentTimeMillis());

        Optional<AgentStatus> agentStatusOptional = agentStatusRepository.findByCisn(cisn);

        String publicAgentVer = agentStatusDto.getAgentVer();
        String privateAgentVer = agentStatusDto.getGatewayVer();

        // TODO Agent가 주기적으로 getExtract를 호출할때마다 Public/Private Agent version을 "Unknown"으로 상태 변경함(임시)
        if (agentStatusOptional.isPresent()) {
            if(StringUtils.isBlank(publicAgentVer) || !("Unknown").equals(agentStatusOptional.get().getPublicAgentVer())) {
                publicAgentVer = agentStatusOptional.get().getPublicAgentVer();
            }
            if(StringUtils.isBlank(privateAgentVer) || !("Unknown").equals(agentStatusOptional.get().getPrivateAgentVer())) {
                privateAgentVer = agentStatusOptional.get().getPrivateAgentVer();
            }
        }

        AgentStatus agentStatus = AgentStatus.builder()
                .cisn(cisn)
                .publicAgentVer(publicAgentVer)
                .privateAgentVer(privateAgentVer)
                .updateDt(now)
                .build();

        agentStatusRepository.save(agentStatus);
    }
}
