package kr.co.iteyes.fhirmeta.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class PhrDbValLog {
    @EmbeddedId
    private PhrDbValLogId phrDbValLogId;
    private String tableName;
    private String columnName;
    private String errCode;
    private String exceptionMsg;
    private String regYmd;
}
