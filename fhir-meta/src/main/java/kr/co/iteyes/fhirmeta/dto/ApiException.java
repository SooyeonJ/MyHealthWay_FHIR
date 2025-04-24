package kr.co.iteyes.fhirmeta.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
@AllArgsConstructor
public class ApiException {
    private String result;
    private String message;
}
