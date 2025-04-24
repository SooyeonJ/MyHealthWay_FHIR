package kr.co.iteyes.fhirmeta.controller;

import kr.co.iteyes.fhirmeta.dto.ServerMetricsDto;
import kr.co.iteyes.fhirmeta.service.ServerMetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("insight")
public class InsightController {
    private final ServerMetricsService serverMetricsService;

    @PostMapping()
    public ResponseEntity<?> addServerMetrics(@RequestBody ServerMetricsDto.CreateResource createResource) throws Exception {
        serverMetricsService.addServerMetrics(createResource);
        return ResponseEntity.ok().build();
    }
}