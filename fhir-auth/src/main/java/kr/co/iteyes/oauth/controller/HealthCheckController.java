package kr.co.iteyes.oauth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

@SessionAttributes("authorizationRequest")
@RestController
@RequestMapping("healthcheck")
public class HealthCheckController {
    @GetMapping()
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok().build();
    }
}