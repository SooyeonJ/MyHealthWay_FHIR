package kr.co.iteyes.fhirmeta.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class OrganizationSeedStatusDto {

    @Getter
    public static class Request {
        @JsonFormat(pattern="yyyy-MM-dd")
        @NotNull
        private LocalDate baseDate;
    }

    @Getter
    public static class Response {
        private String issueDemandNo;
        private String careInstitutionSign;
        private String issueDivisionCode;
        private String status;
        private String issueDateTime;
        private String validBeginningDateTime;
        private String validEndDateTime;
        private String discardDateTime;
    }
}
