package kr.co.iteyes.fhirmeta.controller;

import javax.servlet.http.HttpServletRequest;
import kr.co.iteyes.fhirmeta.dto.CommonDto;
import kr.co.iteyes.fhirmeta.dto.FhrRscUpdtDto;
import kr.co.iteyes.fhirmeta.dto.FhrUpdtDmndDto;
import kr.co.iteyes.fhirmeta.exception.CustomException;
import kr.co.iteyes.fhirmeta.exception.ExceptionEnum;
import kr.co.iteyes.fhirmeta.exception.RequestValidator;
import kr.co.iteyes.fhirmeta.service.FhrUpdtDmndService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.Charset;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("updtdmnd")
public class FhrUpdtDmndController {

    private final FhrUpdtDmndService fhrUpdtDmndService;

    @Autowired
    private RequestValidator requestValidator;

    /**
     * IF_MHI_SB38
     * 활용서비스 사용자의 의료데이터 갱신 요청 저장
     *
     * @param createRequests
     * @param httpServletRequest
     * @return CommonDto.result
     *
     * 중계 호출
     */
    @PostMapping("/request")
    public ResponseEntity<CommonDto> createFhrUpdtDmnd(@RequestBody List<FhrUpdtDmndDto.CreateRequest> createRequests, HttpServletRequest httpServletRequest, Errors errors) throws Exception {

        // [유효성 검사]
        requestValidator.validateParameters(createRequests, errors);

        CommonDto commonDto = CommonDto.builder()
                .result("SUCCESS")
                .build();

        fhrUpdtDmndService.createFhrUpdtDmnd(createRequests, httpServletRequest);

        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return ResponseEntity.ok()
                .headers(header)
                .body(commonDto);
    }

    /**
     * IF-MHI-SB31
     * 활용서비스 사용자의 의료데이터 업데이트 요청 정보 조회
     *
     * @param cisn
     * @return List<FhrUpdtDmndDto.ResponseForAgent>
     *
     * agent 호출
     */

    @GetMapping("/{cisn}")
    public ResponseEntity<List<FhrUpdtDmndDto.ResponseForAgent>> getRequestList(@PathVariable String cisn) throws Exception {

        List<FhrUpdtDmndDto.ResponseForAgent> response = fhrUpdtDmndService.getRequestList(cisn);

        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return ResponseEntity.ok()
                .headers(header)
                .body(response);
    }

    /**
     * IF-MHI-SB39
     * 활용서비스 사용자의 의료데이터 갱신 요청 상태 결과 저장
     *
     * @param updateRequest
     * @return CommonDto.result
     *
     * agent 호출
     */

    @PostMapping("/result")
    public ResponseEntity<CommonDto> updateFhrUpdtDmnd(@RequestBody FhrUpdtDmndDto.UpdateRequest updateRequest, Errors errors) throws Exception {
        // [유효성 검사]
        requestValidator.validateParameters(updateRequest, errors);

        if (updateRequest == null) {
            throw new CustomException(ExceptionEnum.INTERNAL_PARAM_EXCEPTION);
        }
        CommonDto commonDto = CommonDto.builder()
                .result("SUCCESS")
                .build();

        fhrUpdtDmndService.updateFhrUpdtDmnd(updateRequest);

        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return ResponseEntity.ok()
                .headers(header)
                .body(commonDto);
    }

    /**
     * IF-MHI-SB32
     * 활용서비스 사용자의 의료데이터 업데이트 상태 조회
     *
     * @param requestForPlt
     * @return List<FhrUpdtDmndDto.ResponseForPlt>
     *
     * 중계 호출
     */
    @PostMapping("/status")
    public ResponseEntity<List<FhrUpdtDmndDto.ResponseForPlt>> getFhrUpdtDmndStatus(@RequestBody FhrUpdtDmndDto.RequestForPlt requestForPlt, Errors errors) throws Exception {
        // [유효성 검사]
        requestValidator.validateParameters(requestForPlt, errors);

        List<FhrUpdtDmndDto.ResponseForPlt> responseForPlts = FhrUpdtDmndDto.ResponseForPlt.fromList(fhrUpdtDmndService.getFhrUpdtDmndStatus(requestForPlt));

        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        return ResponseEntity.ok()
                .headers(header)
                .body(responseForPlts);

    }

    /**
     * IF-MHI-SB44
     * 기존 FHIR 리소스 삭제 처리
     *
     * @param requestDelete (tokenHashSpSystem, tokenHashSpValue)
     * @return commonDto (성공 : SUCCESS, 실패 : FAIL)
     *
     * agent 호출
     */

    @PostMapping("/delete")
    public ResponseEntity<CommonDto> deleteResource(@RequestBody FhrRscUpdtDto.RequestDelete requestDelete,HttpServletRequest httpServletRequest, Errors errors) throws Exception {

        if (requestDelete == null) {
            throw new CustomException(ExceptionEnum.INTERNAL_PARAM_EXCEPTION);
        }

        // [유효성 검사]
        requestValidator.validateParameters(requestDelete, errors);

        // 하위 리소스 delete
        fhrUpdtDmndService.deleteResource(requestDelete);
        // 하위 리소스 expunge
        fhrUpdtDmndService.expungeResource(requestDelete, httpServletRequest);
        // Encounter delete
        fhrUpdtDmndService.deleteEncounter(requestDelete);
        // Encounter expunge
        fhrUpdtDmndService.expungeEncounter(requestDelete, httpServletRequest);

        CommonDto commonDto = CommonDto.builder()
                .result("SUCCESS")
                .build();

        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return ResponseEntity.ok()
                .headers(header)
                .body(commonDto);
    }

}
