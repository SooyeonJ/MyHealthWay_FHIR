package kr.co.iteyes.fhirmeta.controller;

import kr.co.iteyes.fhirmeta.dto.CommonDto;
import kr.co.iteyes.fhirmeta.dto.IndexDto;
import kr.co.iteyes.fhirmeta.entity.Index;
import kr.co.iteyes.fhirmeta.service.IndexService;
import kr.co.iteyes.fhirmeta.service.LogicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.Charset;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("visit")
public class IndexController {

    private final IndexService indexService;

    private final LogicService logicService;

    /**
     * 파일럿 기준 메소드명 : insertVisitPatientIndex
     * @param request
     * @return
     */
    @PostMapping("/index")
    public ResponseEntity<CommonDto> createIndex(@RequestBody IndexDto.RequestForInsert request) {
        logicService.createIndex(request);

        CommonDto commonDto = CommonDto.builder()
                .result("SUCCESS")
                .build();

        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return ResponseEntity.ok()
                .headers(header)
                .body(commonDto);
    }

    /**
     * 파일럿 기준 메소드명 : updateVisitPatientIndex
     * @param request
     * @param cisn
     * @return
     */
    @PutMapping("/index/{cisn}")
    public ResponseEntity<CommonDto> updateIndex(@RequestBody IndexDto.RequestForUpdate request, @PathVariable String cisn) {
        logicService.updateIndex(request, cisn);

        CommonDto commonDto = CommonDto.builder()
                .result("SUCCESS")
                .build();

        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return ResponseEntity.ok()
                .headers(header)
                .body(commonDto);
    }

    @PostMapping("/index/{cisn}")
    public ResponseEntity<CommonDto> updateIndex(@RequestBody IndexDto.RequestForUpdatePost request, @PathVariable String cisn) {
        logicService.updateIndex(request, cisn);

        CommonDto commonDto = CommonDto.builder()
                .result("SUCCESS")
                .build();

        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return ResponseEntity.ok()
                .headers(header)
                .body(commonDto);
    }

    /**
     * 파일럿 기준 메소드명 : getVisitIndexByUserIdNCisn
     * @param mhId
     * @param cisn
     * @return
     */
    @GetMapping(value = "/index")
    public ResponseEntity<List<IndexDto.Response>> getIndexListByKey(String mhId, String cisn) {
        List<Index> indexList = indexService.getIndexListByKey(mhId, cisn);

        List<IndexDto.Response> responses = IndexDto.fromList(indexList);

        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return ResponseEntity.ok()
                .headers(header)
                .body(responses);
    }
}
