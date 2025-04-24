package kr.co.iteyes.fhirmeta.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

public class ConsentDto {

    /**
     * 기준일자 파라미터
     */
    @Getter
    @Setter
    public static class BaseDateRequest {
        @NotBlank
        private String baseDate;
        private String serverDomainNo;
    }

    /**
     * 사용자 동의상태 대사처리 요청
     */
    @Getter
    public static class CompareRequest {
        @NotBlank
        private String comparisionJobNo;
        @NotNull
        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
        private LocalDateTime baseDateTime;
        @NotNull
        private int totalPages;
        @NotNull
        private int page;
        @NotNull
        private List<AgreementStatusProces> agreementStatusProcessList;
    }

    /**
     * 활용서비스 사용자 동의 목록
     */
    @Getter
    public static class AgreementStatusProces {
        @NotBlank
        private String utilizationServiceNo;
        @NotBlank
        private String utilizationUserNo;
        @NotBlank
        private String careInstitutionSign;
    }

    /**
     * 사용자 동의상태 대사처리 결과
     */
    @Getter
    @Setter
    public static class CompareResultResponse {
        private String comparisionJobNo;
        private String stptPnstNo;
        private String careInstitutionSign;
        private int agreementCorrectionCount;
        private int withdrawalCorrectionCount;
        private int lastAgreementCount;

        public void setAgreementCorrectionCount(Object value) {
            if (value != null) agreementCorrectionCount = Integer.parseInt(value.toString());
        }

        public void setWithdrawalCorrectionCount(Object value) {
            if (value != null) withdrawalCorrectionCount = Integer.parseInt(value.toString());
        }

        public void setLastAgreementCount(Object value) {
            if (value != null) lastAgreementCount = Integer.parseInt(value.toString());
        }

    }

}
