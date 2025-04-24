package kr.co.iteyes.fhirmeta.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import kr.co.iteyes.fhirmeta.entity.FhrRscldg;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FhrValidStatsDto {

    @Getter
    public static class Request {
        @JsonFormat(pattern="yyyy-MM-dd")
        private Date baseDate;
    }

    @Getter
    @Builder
    public static class Response {
        private String stptPnstNo;
        private String ldgYmd;
        private String cisn;
        private String fhirRscTpcd;
        private int ldgNocs;
        private int errNocs;
        private String fhirSvrRegDt;

        public static List<Response> fromList(List<FhrRscldg> tfhrRscldgs) {
            List<Response> result = new ArrayList<>();

            tfhrRscldgs.forEach(tfhrRscldg -> result.add(
                    Response.from(tfhrRscldg)
            ));
            return result;
        }

        private static Response from(FhrRscldg fhrRscldg) {
            return Response.builder()
                    .stptPnstNo(fhrRscldg.getFhrRscldgId().getStptPnstNo().trim())
                    .ldgYmd(fhrRscldg.getFhrRscldgId().getLdgYmd())
                    .cisn(fhrRscldg.getFhrRscldgId().getCisn())
                    .fhirRscTpcd(fhrRscldg.getFhrRscldgId().getFhirRscTpcd())
                    .ldgNocs(fhrRscldg.getLdgNocs())
                    .errNocs(fhrRscldg.getErrNocs())
                    .fhirSvrRegDt(fhrRscldg.getRegDt())
                    .build();
        }
    }
}
