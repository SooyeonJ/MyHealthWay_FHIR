package kr.co.iteyes.fhirmeta.service;

import kr.co.iteyes.fhirmeta.dto.PhrDbValLogDto;
import kr.co.iteyes.fhirmeta.dto.PhrDbValLogWrapperDto;
import kr.co.iteyes.fhirmeta.entity.PhrDbValLog;
import kr.co.iteyes.fhirmeta.entity.PhrDbValLogId;
import kr.co.iteyes.fhirmeta.repository.PhrDbValLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PhrDbValLogService {

    private final PhrDbValLogRepository phrDbValLogRepository;


    public void createPhrDbValLog(String cisn, PhrDbValLogWrapperDto phrDbValLogWrapperDto) {
        List<PhrDbValLog> phrDbValLogs = new ArrayList<>();

        for (PhrDbValLogDto phrDbValLogDto : phrDbValLogWrapperDto.getLogs()) {
            PhrDbValLogId phrDbValLogId = PhrDbValLogId.builder()
                    .cisn(cisn)
                    .logId(phrDbValLogDto.getLogId())
                    .build();

            PhrDbValLog phrDbValLog = PhrDbValLog.builder()
                    .phrDbValLogId(phrDbValLogId)
                    .tableName(phrDbValLogDto.getTableName())
                    .columnName(phrDbValLogDto.getColumnName())
                    .errCode(phrDbValLogDto.getErrCode())
                    .exceptionMsg(phrDbValLogDto.getExceptionMsg())
                    .regYmd(phrDbValLogDto.getRegYmd())
                    .build();
            phrDbValLogs.add(phrDbValLog);
        }
        phrDbValLogRepository.saveAll(phrDbValLogs);
    }

    public PhrDbValLogDto getPhrDbValLog(String cisn) {
        List<PhrDbValLog> phrDbValLogs = phrDbValLogRepository.findAllByCisn(cisn);
        return PhrDbValLogDto.fromList(phrDbValLogs);
    }
}
