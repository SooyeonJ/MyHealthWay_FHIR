package kr.co.iteyes.fhirmeta.dto;

import kr.co.iteyes.fhirmeta.code.AgreementCode;
import kr.co.iteyes.fhirmeta.entity.Extract;
import kr.co.iteyes.fhirmeta.utils.SeedCtrUtils;
import lombok.Builder;
import lombok.Getter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ExtractDto {
    @Getter
    public static class Request {
        private String cisn;
    }

    @Getter
    @Builder
    public static class Response {
        private String userId;                      // MH사용자ID
        private String serviceUID;                  // 활용서비스UID
        private String provideInstitutionCode;      // 제공기관 코드
        private String residentRegistrationNumber;  // 주민번호
        private String agreementCode;               // 동의상태
        private String registerDate;                // 등록 일자
        private String extractDate;                 // 추출 일자

        public static List<Response> fromList(List<Extract> extracts, String key) {
            List<Response> result = new ArrayList<>();

            extracts.forEach(extract -> result.add(
                    Response.from(extract, key)
            ));
            return result;
        }

        private static Response from(Extract extract, String key) {

            SimpleDateFormat simpleDateFormat = new java.text.SimpleDateFormat("yyyyMMddHHmmss");

            return Response.builder()
                    .userId(extract.getExtractId().getMhId())
                    .serviceUID(extract.getAppId())
                    .provideInstitutionCode(extract.getExtractId().getCisn())
                    .residentRegistrationNumber(SeedCtrUtils.SEED_CTR_Decrypt(key, extract.getRrno()))
                    .agreementCode(AgreementCode.getCode(extract.getUseYn()))
                    .registerDate(simpleDateFormat.format(extract.getCreateDt()))
                    .extractDate(simpleDateFormat.format(extract.getCreateDt()))
                    .build();
        }
    }


}
