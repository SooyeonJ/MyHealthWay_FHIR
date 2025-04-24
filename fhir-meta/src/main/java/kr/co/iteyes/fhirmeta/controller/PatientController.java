package kr.co.iteyes.fhirmeta.controller;

import kr.co.iteyes.fhirmeta.dto.CommonDto;
import kr.co.iteyes.fhirmeta.dto.ConsentCreateDto;
import kr.co.iteyes.fhirmeta.dto.ConsentDeleteDto;
import kr.co.iteyes.fhirmeta.dto.PatientStatusDto;
import kr.co.iteyes.fhirmeta.service.FhirService;
import kr.co.iteyes.fhirmeta.service.LogicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.Charset;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("patient")
public class PatientController {

    private final LogicService logicService;

    private final FhirService fhirService;

    @PostMapping("/consent/approve")
    public ResponseEntity<CommonDto> createConsent(@RequestBody ConsentCreateDto consentCreateDto) {
        CommonDto commonDto = CommonDto.builder()
                .result("SUCCESS")
                .build();

        logicService.createConsent(consentCreateDto);

        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return ResponseEntity.ok()
                .headers(header)
                .body(commonDto);
    }

    @PostMapping("/consent/cancel")
    public ResponseEntity<CommonDto> disagreeConsent(@RequestBody ConsentDeleteDto consentDeleteDto) {
        CommonDto commonDto = CommonDto.builder()
                .result("SUCCESS")
                .build();

        logicService.disagreeConsent(consentDeleteDto);

        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return ResponseEntity.ok()
                .headers(header)
                .body(commonDto);
    }

    @PostMapping("/consent/delete")
    public ResponseEntity<CommonDto> deleteConsent(@RequestBody ConsentDeleteDto consentDeleteDto) {
        CommonDto commonDto = CommonDto.builder()
                .result("SUCCESS")
                .build();

        logicService.deleteConsent(consentDeleteDto);

        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return ResponseEntity.ok()
                .headers(header)
                .body(commonDto);
    }

    @PostMapping("/status")
    public ResponseEntity<List<PatientStatusDto.Response>> getStatus(@RequestBody PatientStatusDto.Request request) {

        // 플랫폼에서 데일리 배치로 호출
        // 응답결과는 해당일에 최초 적재된 데이터만 응답
        // 최초 적재된 데이터 여부는 patient 리소스 _lastUpdated 기준으로 조회 (ex: _lastUpdated=ge2023-01-01)
        List<PatientStatusDto.Response> responses = PatientStatusDto.Response.from(fhirService.searchPatientByLastUpdated(request.getBaseDate()));

        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return ResponseEntity.ok()
                .headers(header)
                .body(responses);
    }
}
