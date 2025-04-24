package kr.co.iteyes.fhirmeta.controller;

import kr.co.iteyes.fhirmeta.dto.CommonDto;
import kr.co.iteyes.fhirmeta.dto.AgentStatusDto;
import kr.co.iteyes.fhirmeta.service.AgentStatusService;
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
@RequestMapping("status")
public class AgentStatusController {

    private final AgentStatusService agentStatusService;

    @PostMapping("/{cisn}")
    public ResponseEntity<CommonDto> createAgentStatus(@PathVariable String cisn, @RequestBody AgentStatusDto agentStatusDto) {
        CommonDto commonDto = CommonDto.builder()
                .result("SUCCESS")
                .build();

        agentStatusService.createAgentStatus(cisn, agentStatusDto);

        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return ResponseEntity.ok()
                .headers(header)
                .body(commonDto);
    }
}
