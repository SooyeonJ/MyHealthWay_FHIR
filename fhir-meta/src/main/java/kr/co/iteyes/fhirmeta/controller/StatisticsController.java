package kr.co.iteyes.fhirmeta.controller;

import kr.co.iteyes.fhirmeta.dto.StatisticsDto;
import kr.co.iteyes.fhirmeta.service.EncryptService;
import kr.co.iteyes.fhirmeta.service.StatisticsService;
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
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;
    private final EncryptService encryptService;


    @PostMapping
    public ResponseEntity<List<StatisticsDto.Response>> getStatistics(@RequestBody StatisticsDto.Request request) {

        List<String> cisnListByActiveSeedKey = encryptService.getActiveSeedKey().stream()
                .map(m -> m.getEncryptKeyId().getCisn())
                .collect(Collectors.toList());

        List<StatisticsDto.Response> responses = StatisticsDto.Response.fromList(statisticsService.getStatisticsList(cisnListByActiveSeedKey, request));

        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return ResponseEntity.ok()
                .headers(header)
                .body(responses);
    }
}
