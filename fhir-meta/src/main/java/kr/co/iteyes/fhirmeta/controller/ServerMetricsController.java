package kr.co.iteyes.fhirmeta.controller;

import kr.co.iteyes.fhirmeta.dto.ServerMetricsDto;
import kr.co.iteyes.fhirmeta.service.ServerMetricsService;
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
@RequestMapping("metrics")
public class ServerMetricsController {

    private final ServerMetricsService serverMetricsService;

    @PostMapping()
    public ResponseEntity<List<ServerMetricsDto.Response>> getServerMetrics(@RequestBody ServerMetricsDto.Request request) {
        List<ServerMetricsDto.Response> responses = ServerMetricsDto.Response.fromList(serverMetricsService.getServerMetrics(request));

        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return ResponseEntity.ok()
                .headers(header)
                .body(responses);
    }
}