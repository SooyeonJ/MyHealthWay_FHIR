package kr.co.iteyes.fhirmeta.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;


public class OrganizationStatusDto {

    @Getter
    public static class Request {
        private List<String> careInstitutionSignList;
    }

    @Getter
    @AllArgsConstructor
    public static class Response {
        private String serverStatus;
        private List<Result> result;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Result {
        private String careInstitutionSign;
        private String status;
    }
}
