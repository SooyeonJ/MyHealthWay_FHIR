package kr.co.iteyes.fhirmeta.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import kr.co.iteyes.fhirmeta.code.IssueDivisionCode;
import kr.co.iteyes.fhirmeta.dto.*;
import kr.co.iteyes.fhirmeta.exception.CustomException;
import kr.co.iteyes.fhirmeta.exception.ExceptionEnum;
import kr.co.iteyes.fhirmeta.service.EncryptService;
import kr.co.iteyes.fhirmeta.service.LogicService;
import kr.co.iteyes.fhirmeta.utils.CommonUtils;
import kr.co.iteyes.fhirmeta.exception.RequestValidator;
import kr.co.iteyes.fhirmeta.utils.HttpUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("security")
public class SecurityController {

    private final EncryptService encryptService;
    private final LogicService logicService;

    @Autowired
    private RequestValidator requestValidator;

    /**
     * 플랫폼에서 최초 SEED 키를 등록 요청하기 전에 RSA 키를 요청
     * @return
     */
    @PostMapping("/rsa")
    public ResponseEntity<RsaDto.Response> getRsaKey(@RequestHeader("serverDomainNo") String serverDomainNo, @RequestBody RsaDto.Request request) {
        boolean isValidHash = CommonUtils.isValidHash(serverDomainNo, request.getHashValue());
        RsaDto.Response response = RsaDto.Response.from(isValidHash, encryptService.getRsaKey());

        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        return ResponseEntity.ok()
                .headers(header)
                .body(response);
    }

    /**
     * IF_MYHW_FHIR_009
     * 플랫폼 암호화 KEY 활성화 요청
     * @param request
     * @return
     */
    @PostMapping("/seed/active")
    public ResponseEntity<SeedActiveDto.Response> activeSeedKey(@RequestBody SeedActiveDto.Request request) {
        if(request.getResult().equals("ACTIVE")) {
            encryptService.activeSeedKey();
        } else {
            encryptService.discardSeedKey();
        }

        SeedActiveDto.Response response = new SeedActiveDto.Response("SUCCESS");

        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        return ResponseEntity.ok()
                .headers(header)
                .body(response);
    }

    /**
     * 제공기관 암호화(SEED) 키 조회
     * @param cisn
     * @return SeedDto.ResponseForAgent
     */
    @GetMapping("/seed/{cisn}")
    public ResponseEntity<SeedDto.ResponseForAgent> getSeedKey(@PathVariable("cisn") String cisn) {
        try {
            SeedDto.ResponseForAgent response = SeedDto.ResponseForAgent.from(encryptService.getSeedKey(cisn));

            HttpHeaders header = new HttpHeaders();
            header.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
            return ResponseEntity.ok()
                    .headers(header)
                    .body(response);
        } catch (InvalidKeyException e) {
            throw new CustomException(ExceptionEnum.INVALID_KEY_EXCEPTION);
        }
    }

    /**
     * 제공서버 암호화 키 등록(거점 제공서버 암호화키 발급계획승인 수신)
     * @param dto
     * @param errors
     */
    @PostMapping("/seed")
    public void createSeedKey(@RequestBody SeedDto.Request dto, Errors errors, HttpServletResponse httpServletResponse) throws Exception {
        requestValidator.validateParameters(dto, errors); //파라미터 유효성 검사
        if (!IssueDivisionCode.ISSU.getCode().equals(dto.getIssueDivisionCode())) //[10:발급,20:갱신,30:폐기]
            throw new CustomException(ExceptionEnum.INVALID_ISSUE_DIVISION_CODE_EXCEPTION);

        logicService.createProvideServerSeedKey(dto); //제공서버 암호화(SEED)키 등록
        CommonDto commonDto = CommonDto.builder()
                .result("SUCCESS")
                .hashValue(CommonUtils.createHashValue(dto.getServerDomainNo()))
                .build();

        String seedKey = dto.getEncryptionKey(); //새로 전송받은 SEED 키
        String responseMessage = new ObjectMapper().writeValueAsString(commonDto);
        HttpUtils.commonEncryptRes(httpServletResponse, seedKey, responseMessage, HttpStatus.OK.value()).flushBuffer();
    }

    /**
     * 제공서버 암호화 키 갱신
     * @param dto
     * @param errors
     * @return
     */
    @PostMapping("/seed/updt")
    public void updateSeedKey(@RequestBody SeedDto.Request dto, Errors errors, HttpServletResponse httpServletResponse) throws Exception {
        requestValidator.validateParameters(dto, errors); //파라미터 유효성 검사
        if(!IssueDivisionCode.UPDT.getCode().equals(dto.getIssueDivisionCode())) //[10:발급,20:갱신,30:폐기]
            throw new CustomException(ExceptionEnum.INVALID_ISSUE_DIVISION_CODE_EXCEPTION);

        logicService.updateProvideServerSeedKey(dto); //제공서버 암호화 키 갱신
        CommonDto commonDto = CommonDto.builder()
                .result("SUCCESS")
                .hashValue(CommonUtils.createHashValue(dto.getServerDomainNo()))
                .build();

        String seedKey = dto.getEncryptionKey(); //새로 전송받은 SEED 키
        String responseMessage = new ObjectMapper().writeValueAsString(commonDto);
        HttpUtils.commonEncryptRes(httpServletResponse, seedKey, responseMessage, HttpStatus.OK.value()).flushBuffer();
    }

    /**
     * 제공서버 암호화 키 폐기
     * @param dto
     * @param errors
     * @return
     */
    @PostMapping("/seed/dscd")
    public ResponseEntity<?> discardSeedKey(@RequestBody SeedDto.DscdRequest dto, Errors errors) throws Exception {
        requestValidator.validateParameters(dto, errors); //파라미터 유효성 검사
        if(!IssueDivisionCode.DSCD.getCode().equals(dto.getIssueDivisionCode())) //[10:발급,20:갱신,30:폐기]
            throw new CustomException(ExceptionEnum.INVALID_ISSUE_DIVISION_CODE_EXCEPTION);

        logicService.discardProvideServerSeedKey(dto); //제공서버 암호화 키 폐기
        CommonDto commonDto = CommonDto.builder()
                .result("SUCCESS")
                .build();

        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        return ResponseEntity.ok()
                .headers(header)
                .body(commonDto);
    }

    /**
     * 제공기관 암호화 키 폐기 상태 조회
     * @param dto
     * @param errors
     * @param httpServletRequest
     * @return
     * @throws Exception
     */
    @PostMapping("/seed/dscd/status")
    public ResponseEntity<SeedStatusDto.Response> getDiscardSeedKeyStatus(@RequestBody SeedStatusDto.Request dto,
                                                                          Errors errors,
                                                                          HttpServletRequest httpServletRequest) throws Exception {
        requestValidator.validateParameters(dto, errors); //파라미터 유효성 검사
        String serverDomainNo = httpServletRequest.getHeader("serverDomainNo"); //연계서버번호

        SeedStatusDto.Response response = encryptService.getSeedKeyStatus(dto);
        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        header.set("serverDomainNo", serverDomainNo);
        return ResponseEntity.ok()
                .headers(header)
                .body(response);
    }

    /**
     * 의료기관 등록(+token) 및 암호화 키 발급(의료기관 등록 및 암호화 키 발급 계획 승인 수신)
     * @param dto
     * @param errors
     * @param httpServletRequest
     * @return
     * @throws Exception
     */
    @PostMapping("/organization/seed")
    public ResponseEntity<?> createOrganizationSeedKey(@RequestBody List<OrganizationSeedDto.IssuRequest> dto,
                                                       Errors errors,
                                                       HttpServletRequest httpServletRequest) throws Exception {
        requestValidator.validateParameters(dto, errors); //파라미터 유효성 검사
        String serverDomainNo = httpServletRequest.getHeader("serverDomainNo"); //연계서버번호

        for (final OrganizationSeedDto.IssuRequest item : dto) {
            if(!IssueDivisionCode.ISSU.getCode().equals(item.getIssueDivisionCode()))
                throw new CustomException(ExceptionEnum.INVALID_ISSUE_DIVISION_CODE_EXCEPTION);
        }

        logicService.issueOrganizationSeedKey(dto);
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
     * 의료기관 암호화 키 갱신(의료기관 암호화 키 갱신 계획 승인 수신)
     * @param dto
     * @param errors
     * @param httpServletRequest
     * @return
     * @throws Exception
     */
    @PostMapping("/organization/seed/updt")
    public ResponseEntity<?> updateOrganizationSeedKey(@RequestBody List<OrganizationSeedDto.UpdtRequest> dto,
                                                       Errors errors,
                                                       HttpServletRequest httpServletRequest) throws Exception {
        requestValidator.validateParameters(dto, errors); //파라미터 유효성 검사
        String serverDomainNo = httpServletRequest.getHeader("serverDomainNo"); //연계서버번호

        for (final OrganizationSeedDto.UpdtRequest item : dto) {
            if(!IssueDivisionCode.UPDT.getCode().equals(item.getIssueDivisionCode()))
                throw new CustomException(ExceptionEnum.INVALID_ISSUE_DIVISION_CODE_EXCEPTION);
        }

        logicService.updateOrganizationSeedKey(dto);
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
     * 의료기관 암호화 키 폐기(의료기관 암호화 키 폐기 계획 승인 수신)
     * @param dto
     * @param errors
     * @param httpServletRequest
     * @return
     * @throws Exception
     */
    @PostMapping("/organization/seed/dscd")
    public ResponseEntity<?> discardOrganizationSeedKey(@RequestBody List<OrganizationSeedDto.DscdRequest> dto,
                                                        Errors errors,
                                                        HttpServletRequest httpServletRequest) throws Exception {
        requestValidator.validateParameters(dto, errors); //파라미터 유효성 검사
        String serverDomainNo = httpServletRequest.getHeader("serverDomainNo"); //연계서버번호

        for (final OrganizationSeedDto.DscdRequest item : dto) {
            if(!IssueDivisionCode.DSCD.getCode().equals(item.getIssueDivisionCode()))
                throw new CustomException(ExceptionEnum.INVALID_ISSUE_DIVISION_CODE_EXCEPTION);
        }

        logicService.discardOrganizationSeedKey(dto);
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
     * 의료기관 암호화 키 발급 상태 조회
     * @param dto
     * @param errors
     * @param httpServletRequest
     * @return
     * @throws Exception
     */
    @PostMapping("/organization/seed/status")
    public ResponseEntity<List<OrganizationSeedStatusDto.Response>> getOrganizationSeedKeyStatus(@RequestBody OrganizationSeedStatusDto.Request dto,
                                                                                                 Errors errors,
                                                                                                 HttpServletRequest httpServletRequest) throws Exception {
        requestValidator.validateParameters(dto, errors); //파라미터 유효성 검사
        String serverDomainNo = httpServletRequest.getHeader("serverDomainNo"); //연계서버번호

        List<OrganizationSeedStatusDto.Response> responseList = encryptService.getOrganizationSeedKeyStatus(dto);
        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        header.set("serverDomainNo", serverDomainNo);
        return ResponseEntity.ok()
                .headers(header)
                .body(responseList);
    }

}
