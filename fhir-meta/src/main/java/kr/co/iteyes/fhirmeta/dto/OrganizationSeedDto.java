package kr.co.iteyes.fhirmeta.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class OrganizationSeedDto {

    @Getter
    public static class IssuRequest {
        private String issueDemandNo;
        @NotBlank
        private String careInstitutionSign;
        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
        private LocalDateTime validityDateTime;
        @JsonFormat(pattern="yyyy-MM-dd")
        private LocalDate approvalDate;
        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
        private LocalDateTime deliveryDateTime;
        @NotBlank
        private String issueDivisionCode;
        private String provideInstitutionNo;
        private String provideInstitutionName;
        private List<OrganizationRegistDto.ProvideEmrSystem> provideEMRSystemList;
        private String privateAgentSystemUseYN;
        private OrganizationRegistDto.PublicAgentSystem outsideAgentSystem;
        private List<OrganizationRegistDto.AgentInstallationDemand> agentInstallationDemandList;
    }

    @Getter
    public static class UpdtRequest {
        private String issueDemandNo;
        @NotBlank
        private String careInstitutionSign;
        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
        private LocalDateTime validityDateTime;
        @JsonFormat(pattern="yyyy-MM-dd")
        private LocalDate approvalDate;
        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
        private LocalDateTime deliveryDateTime;
        @NotBlank
        private String issueDivisionCode;
        private String provideInstitutionNo;
    }

    @Getter
    public static class DscdRequest {
        private String issueDemandNo;
        @NotBlank
        private String careInstitutionSign;
        @JsonFormat(pattern="yyyy-MM-dd")
        private LocalDate approvalDate;
        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
        private LocalDateTime deliveryDateTime;
        @NotBlank
        private String issueDivisionCode;
        private String provideInstitutionNo;
    }
}
