package kr.co.iteyes.fhirmeta.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConsentCreateDto {

    private String utilizationServiceNo;
    private String utilizationUserNo;
    private String userName;
    private String residentRegistrationNumber;
    private List<String> careInstitutionSignList;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime agreementDateTime;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deliveryDateTime;
}
