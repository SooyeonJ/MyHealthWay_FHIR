package kr.co.iteyes.fhirmeta.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrganizationRegistDto {
    @Getter
    public static class ProvideEmrSystem {
        @NotBlank
        private String emrSystemNo;
        private String emrEnterpriseCode;
        private String emrSystemName;
        private String emrSystemVersionName;
        private String databaseKindCode;
        private String databaseVersionName;
        private String emrTypeCode;
        private String registrationStatusCode;
        private String useYN;
        @JsonFormat(pattern="yyyy-MM-dd")
        private LocalDate useBeginningDate;
        @JsonFormat(pattern="yyyy-MM-dd")
        private LocalDate useEndDate;
    }

    @Getter
    public static class PublicAgentSystem {
        private String serverDomainAddress;
        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
        private LocalDateTime thread1StartTime;
        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
        private LocalDateTime thread1EndTime;
        private int thread1Cycle;
        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
        private LocalDateTime thread2StartTime;
        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
        private LocalDateTime thread2EndTime;
        private int thread2Cycle;
        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
        private LocalDateTime thread3StartTime;
        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
        private LocalDateTime thread3EndTime;
        private int thread3Cycle;
        private int firstLoadingPeriod;
        private int loadingCycle;
        private String clientId;
        private String clientSecretKey;
        private String useYN;
    }

    @Getter
    public static class AgentInstallationDemand {
        private String installationFileNo;
        private String installationDemandNo;
        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
        private LocalDateTime installationBeginningDate;
        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
        private LocalDateTime installationDemandDate;
    }

}
