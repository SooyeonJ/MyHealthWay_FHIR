package kr.co.iteyes.fhirmeta.service;

import kr.co.iteyes.fhirmeta.dto.OrganizationStatusDto;
import kr.co.iteyes.fhirmeta.entity.AgentStatus;
import kr.co.iteyes.fhirmeta.entity.ServerStatus;
import kr.co.iteyes.fhirmeta.repository.AgentStatusRepository;
import kr.co.iteyes.fhirmeta.repository.ServerStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RequiredArgsConstructor
@Service
public class ServerStatusService {

    private final ServerStatusRepository serverStatusRepository;
    private final AgentStatusRepository agentStatusRepository;
    private final EncryptService encryptService;

    public List<OrganizationStatusDto.Result> getStatus(List<String> careInstitutionSignList) {
        List<OrganizationStatusDto.Result> results = new ArrayList<>();
        Date now = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String strToday = dateFormat.format(now);

        List<AgentStatus> agentStatuses = agentStatusRepository.findAllById(careInstitutionSignList);

        for (String cisn : careInstitutionSignList) {
            String status = "50"; //에이전트 미설치 상태
            //에이전트 당일 적재 여부(리소스 적재 또는 동의대상조회)
            boolean isToday = agentStatuses.stream().anyMatch(agentStatus -> agentStatus.getCisn().equals(cisn)
                    && agentStatus.getUpdateDt() != null
                    && agentStatus.getUpdateDt().toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyyMMdd")).equals(strToday));

            if (isToday) {
                status = "10"; //에이전트 당일적재
            }
            else {
                boolean isExist = agentStatuses.stream().anyMatch(agentStatus -> agentStatus.getCisn().equals(cisn)); //에이전트 설치여부
                if (isExist) {
                    boolean isEncryptKey = encryptService.isValidKey(cisn, "SEED");
                    if (isEncryptKey)
                        status = "40"; //에이전트 미적재
                }
            }

            OrganizationStatusDto.Result result = new OrganizationStatusDto.Result(cisn, status);
            results.add(result);
        }
        return results;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateServerStatus(String cisn, String fhirLastUpdatedYmd) {
        ServerStatus serverStatus = ServerStatus.builder()
                .cisn(cisn)
                .fhirLastUpdatedYmd(fhirLastUpdatedYmd)
                .build();
        serverStatusRepository.save(serverStatus);
    }
}
