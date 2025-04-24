package kr.co.iteyes.fhirmeta.dto;

import lombok.*;

public class SeedActiveDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder
    public static class Request {
        private String result;
    }

    @Getter
    @AllArgsConstructor
    public static class Response {
        private String result;
    }
}
