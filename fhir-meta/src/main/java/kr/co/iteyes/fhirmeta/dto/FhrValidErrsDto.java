package kr.co.iteyes.fhirmeta.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import kr.co.iteyes.fhirmeta.entity.FhrDtaerr;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FhrValidErrsDto {

    @Getter
    public static class Request {
        @JsonFormat(pattern="yyyy-MM-dd")
        private Date baseDate;
    }

    @Getter
    @Builder
    public static class Response {
        private String stptPnstNo;
        private int logSn;
        private String ldgYmd;
        private String cisn;
        private String fhirRscTpcd;
        private int fhirRscNo;
        private String errCd;
        private String errCn;
        private String fhirSvrRegDt;

        public static List<Response> fromList(List<FhrDtaerr> tfhrDtaerrs) {
            List<Response> result = new ArrayList<>();

            tfhrDtaerrs.forEach(tfhrDtaerr -> result.add(
                    Response.from(tfhrDtaerr)
            ));
            return result;
        }

        private static Response from(FhrDtaerr fhrDtaerr) {
            return Response.builder()
                    .stptPnstNo(fhrDtaerr.getFhrDtaerrId().getStptPnstNo().trim())
                    .logSn(fhrDtaerr.getFhrDtaerrId().getLogSn())
                    .ldgYmd(fhrDtaerr.getLdgYmd())
                    .cisn(fhrDtaerr.getCisn())
                    .fhirRscTpcd(fhrDtaerr.getFhirRscTpcd())
                    .fhirRscNo(fhrDtaerr.getFhirRscNo())
                    .errCd(fhrDtaerr.getErrCd())
                    .errCn(fhrDtaerr.getErrCn())
                    .fhirSvrRegDt(fhrDtaerr.getRegDt())
                    .build();
        }
    }
}
