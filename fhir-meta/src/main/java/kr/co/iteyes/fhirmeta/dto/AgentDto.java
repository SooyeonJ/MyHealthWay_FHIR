package kr.co.iteyes.fhirmeta.dto;

import com.fasterxml.jackson.annotation.*;
import lombok.*;
import javax.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class AgentDto {

    /**
     * 기준일자 파라미터
     */
    @Getter
    public static class BaseDateRequest {
        @JsonFormat(pattern="yyyy-MM-dd")
        @NotNull
        private LocalDate baseDate;
    }

    /**
     * 요양기관번호 파라미터
     */
    @Getter
    public static class CisnRequest {
        @NotBlank
        private String cisn;
    }

    /**
     * 수집에이전트 및 설치파일 정보 요청
     */
    @Getter
    public static class AgentInstallFileRequest {
        @NotBlank
        private String agentKindCode;
        @NotNull
        private int agentVersionNumber;
        @NotBlank
        private String agentName;
        private String versionName;
        private String mainChangeContents;
        @NotBlank
        private String registrationStatusCode;
        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
        private LocalDateTime registrationDateTime;
        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
        private LocalDateTime updateDateTime;
        @NotNull
        private int installFileNumber;
        @NotBlank
        private String databaseKindCode;
        @NotBlank
        private String checksum;
        @NotBlank
        private String fileRegistrationStatusCode;
        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
        private LocalDateTime fileRegistrationDateTime;
    }

    /**
     * 에이전트 설치 요청
     */
    @Getter
    public static class AgentUpdateRequest {
        @NotBlank
        private String agentKindCode;
        @NotNull
        private int installFileNumber;
        @NotNull
        private int installDemandNumber;
        @NotNull
        private int agentVersionNumber;
        private String versionName;
        @NotBlank
        private String careInstitutionSign;
        @NotBlank
        private String downloadUrl;
        private String registrationDateTime;
        private String installDemandDateTime;
        @NotNull
        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
        private LocalDateTime installBeginningPrearrangementDateTime; //계획일시
    }

    /**
     * 에이전트 업데이트 결과 등록 요청
     */
    @Getter
    public static class AgentUpdateResultRegRequest {
        @NotBlank
        private String cisn;
        @NotNull(message = "설치요청번호는 필수 항목 입니다.")
        private int installDemandNumber;
        @NotBlank
        private String agentKindCode;
        @NotNull
        private int agentVersionNumber;
        private String versionName;
        @NotNull
        @JsonFormat(pattern="yyyy-MM-dd")
        private LocalDate installationDate;
        @NotBlank
        private String installationStatusCode;

    }

    /**
     * 에이전트 서비스상태 검사정보 등록 요청
     */
    @Getter
    public static class AgentServiceStatusRegRequest {
        @NotBlank
        private String cisn;
        @NotBlank
        private String agentKindCode;
        private int agentVersionNumber;
        @NotBlank
        private String chckDt;
        private String versionName;
        @NotBlank
        private String serviceStatusCode;
    }

    /**
     * 에이전트 업데이트 대상 응답
     */
    @Getter
    @Setter
    public static class AgentUpdateTargetResponse {
        private String agentKindCode;
        private Integer agentVersionNumber;
        private Integer installFileNumber;
        private Integer installDemandNumber;
        private String versionName;
        private String downloadUrl;

        public void setAgentVersionNumber(Object value) {
            if (value != null) agentVersionNumber = Integer.parseInt(value.toString());
        }

        public void setInstallFileNumber(Object value) {
            if (value != null) installFileNumber = Integer.parseInt(value.toString());
        }

        public void setInstallDemandNumber(Object value) {
            if (value != null) installDemandNumber = Integer.parseInt(value.toString());
        }
    }

    /**
     * 에이전트 업데이트 요청 결과 응답
     */
    @Getter
    @Setter
    public static class AgentUpdateRequestResultResponse {
        private Integer installationFileNo;
        private Integer installationDemandNo;
        private String careInstitutionSign;
        private String provideInstitutionNo;
        private String installationDate;
        private String installationStatusCode;

        public void setInstallationFileNo(Object value) {
            if (value != null) installationFileNo = Integer.parseInt(value.toString());
        }

        public void setInstallationDemandNo(Object value) {
            if (value != null) installationDemandNo = Integer.parseInt(value.toString());
        }
    }

    /**
     * 에이전트 버전 번호 결과 응답
     */
    @Getter
    @Setter
    public static class AgentVersionResult {
        private Integer agentVersionNumber;

        public void setAgentVersionNumber(Object value) {
            if (value != null) agentVersionNumber = Integer.parseInt(value.toString());
        }
    }

}
