package kr.co.iteyes.fhirmeta.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import kr.co.iteyes.fhirmeta.entity.EncryptKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class SeedDto {

    @Getter
    public static class Request {
        private String issueDemandNo;
        @NotBlank(message = ":유효한 서버 도메인 코드가 아닙니다.")
        private String serverDomainNo;
        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
        private LocalDateTime validityDateTime;
        @JsonFormat(pattern="yyyy-MM-dd")
        private LocalDate approvalDate;
        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
        private LocalDateTime deliveryDateTime;
        @NotBlank
        private String issueDivisionCode;
        @NotBlank
        private String encryptionKey;
    }

    @Getter
    public static class DscdRequest {
        private String issueDemandNo;
        @NotBlank(message = ":유효한 서버 도메인 코드가 아닙니다.")
        private String serverDomainNo;
        @JsonFormat(pattern="yyyy-MM-dd")
        private LocalDate approvalDate;
        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
        private LocalDateTime deliveryDateTime;
        @NotBlank
        private String issueDivisionCode;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ResponseForAgent {
        private String key;

        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
        private LocalDateTime validityDateTime;

        public static SeedDto.ResponseForAgent from(EncryptKey encryptKey) {
            if(encryptKey == null || encryptKey.getSeedCtr() == null) return null;

            return ResponseForAgent.builder()
                    .key(encryptKey.getSeedCtr().getKey())
                    .validityDateTime(encryptKey.getValidDt().toLocalDateTime())
                    .build();
        }
    }
}
