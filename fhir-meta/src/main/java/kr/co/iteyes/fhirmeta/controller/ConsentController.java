package kr.co.iteyes.fhirmeta.controller;

import javax.servlet.http.HttpServletRequest;
import kr.co.iteyes.fhirmeta.dto.AgentDto;
import kr.co.iteyes.fhirmeta.dto.CommonDto;
import kr.co.iteyes.fhirmeta.dto.ConsentDto;
import kr.co.iteyes.fhirmeta.exception.CustomException;
import kr.co.iteyes.fhirmeta.exception.ExceptionEnum;
import kr.co.iteyes.fhirmeta.exception.RequestValidator;
import kr.co.iteyes.fhirmeta.service.AgentService;
import kr.co.iteyes.fhirmeta.service.ConsentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("consent")
public class ConsentController {

    private final ConsentService consentService;

    @Autowired
    private RequestValidator requestValidator;

    /**
     * 사용자 동의상태 대사처리 요청
     * @param dto
     * @param errors
     * @param httpServletRequest
     * @return
     * @throws Exception
     */
    @PostMapping("/compare/request/create")
    public ResponseEntity<?> createCompareRequest(@RequestBody ConsentDto.CompareRequest dto,
                                                  Errors errors,
                                                  HttpServletRequest httpServletRequest) throws Exception {
        requestValidator.validateParameters(dto, errors); //파라미터 유효성 검사
        requestValidator.validateParameters(dto.getAgreementStatusProcessList(), errors); //파라미터 유효성 검사
        String serverDomainNo = httpServletRequest.getHeader("serverDomainNo"); //연계서버번호
        if (serverDomainNo == null || serverDomainNo.isEmpty())
            throw new CustomException(ExceptionEnum.INVALID_SERVER_DOMAIN_NO_EXCEPTION);

        consentService.saveCompareRequest(dto, serverDomainNo); //사용자 동의상태 대사처리
        CommonDto commonDto = CommonDto.builder()
                .result("SUCCESS")
                .build();

        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        header.set("serverDomainNo", serverDomainNo);
        return ResponseEntity.ok()
                .headers(header)
                .body(commonDto);
    }

    /**
     * 사용자 동의상태 대사처리 결과 조회
     * @param dto
     * @param errors
     * @param httpServletRequest
     * @return
     * @throws Exception
     */
    @PostMapping("/compare/result/list")
    public ResponseEntity<List<ConsentDto.CompareResultResponse>> getCompareResultList(@RequestBody ConsentDto.BaseDateRequest dto,
                                                                                       Errors errors,
                                                                                       HttpServletRequest httpServletRequest) throws Exception {
        requestValidator.validateParameters(dto, errors); //파라미터 유효성 검사
        String serverDomainNo = httpServletRequest.getHeader("serverDomainNo"); //연계서버번호
        if (serverDomainNo == null || serverDomainNo.isEmpty())
            throw new CustomException(ExceptionEnum.INVALID_SERVER_DOMAIN_NO_EXCEPTION);

        dto.setServerDomainNo(serverDomainNo);

        List<ConsentDto.CompareResultResponse> responseList = consentService.getCompareResultList(dto); //사용자 동의상태 대사처리 결과 조회
        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        header.set("serverDomainNo", serverDomainNo);
        return ResponseEntity.ok()
                .headers(header)
                .body(responseList);
    }

}
