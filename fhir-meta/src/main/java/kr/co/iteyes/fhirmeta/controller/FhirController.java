package kr.co.iteyes.fhirmeta.controller;

import kr.co.iteyes.fhirmeta.dto.FhrValidErrsDto;
import kr.co.iteyes.fhirmeta.dto.FhrValidStatsDto;
import kr.co.iteyes.fhirmeta.service.FhirValidateService;
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
@RequestMapping("fhir")
public class FhirController {

    private final FhirValidateService fhirValidateService;

    @PostMapping("/validate/errors")
    public ResponseEntity<List<FhrValidErrsDto.Response>> getFhrValidErrs(@RequestBody FhrValidErrsDto.Request request) {

        List<FhrValidErrsDto.Response> responses = FhrValidErrsDto.Response.fromList(fhirValidateService.getFhrValidErrs(request));

        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return ResponseEntity.ok()
                .headers(header)
                .body(responses);
    }

    @PostMapping("/validate/statistics")
    public ResponseEntity<List<FhrValidStatsDto.Response>> getFhrValidStats(@RequestBody FhrValidStatsDto.Request request) {

        List<FhrValidStatsDto.Response> responses = FhrValidStatsDto.Response.fromList(fhirValidateService.getFhrValidStats(request));

        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return ResponseEntity.ok()
                .headers(header)
                .body(responses);
    }
}
