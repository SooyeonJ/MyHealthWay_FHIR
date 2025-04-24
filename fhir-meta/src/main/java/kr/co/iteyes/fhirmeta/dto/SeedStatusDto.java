package kr.co.iteyes.fhirmeta.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

public class SeedStatusDto {

    @Getter
    public static class Request {
        @JsonFormat(pattern="yyyy-MM-dd")
        @NotNull
        private LocalDate baseDate;
    }

    @Getter
    @Setter
    public static class Response {
        private String issueDemandNo;
        private char allEncryptionKeyDiscardYN;
        private List<CareInstitutionSignResult> careInstitutionSignResultList;
    }

    @Getter
    @Setter
    public static class CareInstitutionSignResult {
        private String careInstitutionSign;
        private String discardDate;
        private String provideInstitutionNo;
    }
}
