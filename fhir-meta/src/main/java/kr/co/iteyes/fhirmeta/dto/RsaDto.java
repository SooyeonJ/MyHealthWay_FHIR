package kr.co.iteyes.fhirmeta.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.iteyes.fhirmeta.entity.EncryptKey;
import lombok.*;

import java.time.LocalDateTime;

public class RsaDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder
    public static class Request {
        private String hashValue;
        private String strongPointServerYN;
        private String careInstitutionSign;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Getter
    @Builder
    public static class Response {
        private String result;
        private String publicKey;
        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
        private LocalDateTime validityDateTime;

        private String message;

        public static Response from(boolean isValidHash, EncryptKey encryptKey) {
            if(encryptKey == null || encryptKey.getRsaOaep() == null) return null;

            if(isValidHash) {
                return Response.builder()
                        .result("SUCCESS")
                        .publicKey(encryptKey.getRsaOaep().getPublicKey())
                        .validityDateTime(encryptKey.getValidDt().toLocalDateTime())
                        .build();
            } else {
                return Response.builder()
                        .result("FAIL")
                        .message("Invalid HashValue")
                        .build();
            }
        }
    }
}
