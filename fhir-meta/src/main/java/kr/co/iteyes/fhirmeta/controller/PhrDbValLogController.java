package kr.co.iteyes.fhirmeta.controller;

import kr.co.iteyes.fhirmeta.dto.CommonDto;
import kr.co.iteyes.fhirmeta.dto.PhrDbValLogDto;
import kr.co.iteyes.fhirmeta.dto.PhrDbValLogWrapperDto;
import kr.co.iteyes.fhirmeta.service.PhrDbValLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.Charset;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("logs")
public class PhrDbValLogController {

    private final PhrDbValLogService phrDbValLogService;

    @PostMapping("/{cisn}")
    public ResponseEntity<CommonDto> createPhrDbValLog(@PathVariable String cisn, @RequestBody PhrDbValLogWrapperDto phrDbValLogWrapperDto) {
        CommonDto commonDto = CommonDto.builder()
                .result("SUCCESS")
                .build();

        phrDbValLogService.createPhrDbValLog(cisn, phrDbValLogWrapperDto);

        HttpHeaders header = new HttpHeaders();

        header.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return ResponseEntity.ok()
                .headers(header)
                .body(commonDto);
    }

    @GetMapping("/{cisn}")
    public ResponseEntity<PhrDbValLogDto> getPhrDbValLog(@PathVariable String cisn) {
        PhrDbValLogDto phrDbValLogDto = phrDbValLogService.getPhrDbValLog(cisn);

        HttpHeaders header = new HttpHeaders();

        header.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return ResponseEntity.ok()
                .headers(header)
                .body(phrDbValLogDto);
    }
}
