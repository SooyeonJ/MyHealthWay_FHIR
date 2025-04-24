package kr.co.iteyes.fhirmeta.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonDto {
    private String result;
    private String hashValue;
}
