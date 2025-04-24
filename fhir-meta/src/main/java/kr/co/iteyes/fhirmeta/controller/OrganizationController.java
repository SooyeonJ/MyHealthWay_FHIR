package kr.co.iteyes.fhirmeta.controller;

import kr.co.iteyes.fhirmeta.dto.OrganizationStatusDto;
import kr.co.iteyes.fhirmeta.service.ServerStatusService;
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
@RequestMapping("organization")
public class OrganizationController {

    private final ServerStatusService serverStatusService;

    @PostMapping("/status")
    public ResponseEntity<OrganizationStatusDto.Response> getStatus(@RequestBody OrganizationStatusDto.Request request) {
        List<OrganizationStatusDto.Result> list = serverStatusService.getStatus(request.getCareInstitutionSignList());

        OrganizationStatusDto.Response response = new OrganizationStatusDto.Response("10", list);

        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return ResponseEntity.ok()
                .headers(header)
                .body(response);
    }
}
