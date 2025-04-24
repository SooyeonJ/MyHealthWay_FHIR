package kr.co.iteyes.fhirmeta.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("healthcheck")
public class HealthCheckController {

    @GetMapping()
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok().build();
    }
}
