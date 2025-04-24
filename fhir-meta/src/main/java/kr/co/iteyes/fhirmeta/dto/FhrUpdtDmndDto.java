package kr.co.iteyes.fhirmeta.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import kr.co.iteyes.fhirmeta.entity.FhrUpdtDmnd;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FhrUpdtDmndDto {

    @Getter
    public static class CreateRequest {
        @NotBlank
        private String utilizationServiceNo;
        private String utilizationServiceName;
        @NotBlank
        private String utilizationUserNo;
        @NotNull
        private Long updateDemandSerialNumber;
        @NotBlank
        private String careInstitutionSign;
        @NotBlank
        private String beginningDate;
        @NotBlank
        private String endDate;
        @NotBlank
        private String registrationDate;
        private String updateStcd;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder
    public static class ResponseForAgent {
        private String utilizationServiceNo;
        private String utilizationServiceName;
        private Long utilizationUserNo;
        private String updateDemandSerialNumber;
        private String careInstitutionSign;
        private String beginningDate;
        private String endDate;
        private String registrationDate;

        public static List<ResponseForAgent> fromList(List<FhrUpdtDmnd> fhrUpdtDmndsList) {

            List<ResponseForAgent> result = new ArrayList<>();

            fhrUpdtDmndsList.forEach(fhrUpdtDmnd -> result.add(
                    ResponseForAgent.from(fhrUpdtDmnd)
            ));
            return result;
        }

        public static FhrUpdtDmndDto.ResponseForAgent from(FhrUpdtDmnd fhrUpdtDmnd) {
            return ResponseForAgent.builder()
                    .utilizationServiceNo(fhrUpdtDmnd.getAppId())
                    .utilizationServiceName(fhrUpdtDmnd.getAppName())
                    .utilizationUserNo(Long.valueOf(fhrUpdtDmnd.getMhId()))
                    .updateDemandSerialNumber(String.valueOf(fhrUpdtDmnd.getFhrUpdtDmndId().getUpdateDmndNo()))
                    .careInstitutionSign(fhrUpdtDmnd.getCisn())
                    .beginningDate(fhrUpdtDmnd.getUpdateDmndBgngYmd())
                    .endDate(fhrUpdtDmnd.getUpdateDmndEndYmd())
                    .registrationDate(fhrUpdtDmnd.getUpdateDmndYmd())
                    .build();
        }
    }

    @Getter
    public static class UpdateRequest {
        @NotNull
        private Long updateDemandSerialNumber;
        @NotBlank
        private String careInstitutionSign;
        @NotBlank
        private String utilizationUserNo;
        private String registerDate;
        private String phrUpdateDateTime;
        private String phrUpdateFlag;
        private String fhirUpdateDateTime;
        private String fhirUpdateFlag;
        private String updateStcd;
    }

    @Getter
    public static class RequestForPlt {
        @NotBlank
        private String baseDate;
    }

    @Getter
    @Builder
    public static class ResponseForPlt {
        private String utilizationServiceNo;
        private String utilizationUserNo;
        private Long updateDemandSerialNumber;
        private String careInstitutionSign;
        private String status;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime updateDateTime;

        public static List<ResponseForPlt> fromList(List<FhrUpdtDmnd> fhrUpdtDmndsList) {
            List<ResponseForPlt> result = new ArrayList<>();

            fhrUpdtDmndsList.forEach(fhrUpdtDmnd -> result.add(
                    ResponseForPlt.from(fhrUpdtDmnd)
            ));
            return result;
        }

        private static ResponseForPlt from(FhrUpdtDmnd fhrUpdtDmnd) {
            DateTimeFormatter dbFormat = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

            return ResponseForPlt.builder()
                    .utilizationServiceNo(fhrUpdtDmnd.getAppId())
                    .utilizationUserNo(fhrUpdtDmnd.getMhId())
                    .updateDemandSerialNumber(fhrUpdtDmnd.getFhrUpdtDmndId().getUpdateDmndNo())
                    .careInstitutionSign(fhrUpdtDmnd.getCisn())
                    .status(fhrUpdtDmnd.getUpdateStcd())
                    .updateDateTime(LocalDateTime.parse(fhrUpdtDmnd.getUpdateDt(), dbFormat))
                    .build();
        }
    }

}