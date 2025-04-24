package kr.co.iteyes.fhirmeta.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.iteyes.fhirmeta.entity.PhrDbValLog;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PhrDbValLogDto {
    private String logId;
    private String tableName;
    private String columnName;
    private String errCode;
    private String exceptionMsg;
    private String regYmd;

    public static PhrDbValLogDto fromList(List<PhrDbValLog> phrDbValLogs) {
        if (phrDbValLogs == null || phrDbValLogs.size() < 1) return PhrDbValLogDto.builder().build();
        PhrDbValLogDto phrDbValLogDto = PhrDbValLogDto.builder()
                .logId(phrDbValLogs.get(0).getPhrDbValLogId().getLogId())
                .tableName(phrDbValLogs.get(0).getTableName())
                .columnName(phrDbValLogs.get(0).getColumnName())
                .errCode(phrDbValLogs.get(0).getErrCode())
                .exceptionMsg(phrDbValLogs.get(0).getExceptionMsg())
                .regYmd(phrDbValLogs.get(0).getRegYmd())
                .build();
        return phrDbValLogDto;
    }
}
