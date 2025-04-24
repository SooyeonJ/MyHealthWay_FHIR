package kr.co.iteyes.fhirmeta.controller;

import javax.servlet.http.HttpServletRequest;
import kr.co.iteyes.fhirmeta.dto.*;
import kr.co.iteyes.fhirmeta.exception.RequestValidator;
import kr.co.iteyes.fhirmeta.service.AgentService;
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
@RequestMapping("agent")
public class AgentController {

    private final AgentService agentService;

    @Autowired
    private RequestValidator requestValidator;

    /**
     * 수집에이전트 및 설치파일 정보 등록
     * @param dto
     * @param errors
     * @param httpServletRequest
     * @return
     * @throws Exception
     */
    @PostMapping("/request/setup/create")
    public ResponseEntity<?> createAgentInstallFile(@RequestBody AgentDto.AgentInstallFileRequest dto,
                                                    Errors errors,
                                                    HttpServletRequest httpServletRequest) throws Exception {
        requestValidator.validateParameters(dto, errors); //파라미터 유효성 검사
        String serverDomainNo = httpServletRequest.getHeader("serverDomainNo"); //연계서버번호

        agentService.saveAgentInstallFile(dto); //에이전트 설치요청 목록 저장
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
     * 에이전트 업데이트 요청 등록
     * @param dto
     * @param errors
     * @param httpServletRequest
     * @return
     * @throws Exception
     */
    @PostMapping("/request/update/create")
    public ResponseEntity<?> createAgentUpdateRequest(@RequestBody List<AgentDto.AgentUpdateRequest> dto,
                                                      Errors errors,
                                                      HttpServletRequest httpServletRequest) throws Exception {
        requestValidator.validateParameters(dto, errors); //파라미터 유효성 검사
        String serverDomainNo = httpServletRequest.getHeader("serverDomainNo"); //연계서버번호

        agentService.saveAgentUpdate(dto); //에이전트 업데이트 요청 정보 저장
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
     * 에이전트 업데이트 대상 조회
     * @param dto
     * @param errors
     * @return
     * @throws Exception
     */
    @PostMapping("/target/update/list")
    public ResponseEntity<List<AgentDto.AgentUpdateTargetResponse>> getAgentUpdateTargetList(@RequestBody AgentDto.CisnRequest dto, Errors errors) throws Exception {
        requestValidator.validateParameters(dto, errors); //파라미터 유효성 검사

        List<AgentDto.AgentUpdateTargetResponse> responseList = agentService.getAgentUpdateTargetList(dto); //에이전트 업데이트 대상 조회
        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        return ResponseEntity.ok()
                .headers(header)
                .body(responseList);
    }

    /**
     * 에이전트 업데이트 결과 등록
     * @param dto
     * @param errors
     * @return
     * @throws Exception
     */
    @PostMapping("/target/result/create")
    public ResponseEntity<?> createAgentUpdateResultReg(@RequestBody AgentDto.AgentUpdateResultRegRequest dto, Errors errors) throws Exception {
        requestValidator.validateParameters(dto, errors); //파라미터 유효성 검사

        agentService.saveAgentUpdateResultReg(dto); //에이전트 업데이트 결과 저장
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
     * 에이전트 설치파일 업데이트 요청 처리결과 조회
     * @param dto
     * @param errors
     * @param httpServletRequest
     * @return
     * @throws Exception
     */
    @PostMapping("/request/result/list")
    public ResponseEntity<List<AgentDto.AgentUpdateRequestResultResponse>> getAgentUpdateResultList(@RequestBody AgentDto.BaseDateRequest dto,
                                                                                                    Errors errors,
                                                                                                    HttpServletRequest httpServletRequest) throws Exception {
        requestValidator.validateParameters(dto, errors); //파라미터 유효성 검사
        String serverDomainNo = httpServletRequest.getHeader("serverDomainNo"); //연계서버번호

        List<AgentDto.AgentUpdateRequestResultResponse> responseList = agentService.getAgentUpdateResultList(dto); //에이전트 업데이트 요청 처리결과 조회
        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        header.set("serverDomainNo", serverDomainNo);
        return ResponseEntity.ok()
                .headers(header)
                .body(responseList);
    }

    /**
     * 에이전트 서비스상태 체크 저장
     * @param dto
     * @param errors
     * @return CommonDto.result
     */
    @PostMapping("/target/status/create")
    public ResponseEntity<?> createAgentServiceStatusReg(@RequestBody AgentDto.AgentServiceStatusRegRequest dto, Errors errors) throws Exception {
        requestValidator.validateParameters(dto, errors); //파라미터 유효성 검사

        agentService.saveAgentServiceStatus(dto); //에이전트 서비스상태 검사정보 저장
        CommonDto commonDto = CommonDto.builder()
                .result("SUCCESS")
                .build();

        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        return ResponseEntity.ok()
                .headers(header)
                .body(commonDto);
    }

}
