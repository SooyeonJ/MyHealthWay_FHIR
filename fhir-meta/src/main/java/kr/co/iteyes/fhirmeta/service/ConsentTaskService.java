package kr.co.iteyes.fhirmeta.service;

import javax.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class ConsentTaskService {

    @Resource
    private ConsentDao consentDao;

    /**
     * 동의정보 대사처리
     * @param serverDomainNo
     * @param comparisionJobNo
     * @throws Exception
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveComparePostProcess(String serverDomainNo, String comparisionJobNo) throws Exception {

        String jobStDt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("stptNo",Integer.parseInt(serverDomainNo));
        paramMap.put("cmpsJobNo",Integer.parseInt(comparisionJobNo));

        // 1. 동의정보 보정 자료 생성
        consentDao.insertFhrAgreCmpsCrct(paramMap);

        // 2. 동의 추출대상 추가
        consentDao.insertExtractCmpsAll(paramMap);

        // 3. 동의 추출대상 업데이트
        consentDao.updateExtractCmpsAll(paramMap);

        // 4. 동의정보 업데이트
        consentDao.updateConsentCmpsAll(paramMap);

        // 5. 추출정보 없는 동의정보 업데이트
        consentDao.clearConsent(paramMap);

        // 6. 동의정보 보정결과 업데이트
        consentDao.updateFhrAgreCmpsCrctAll(paramMap);

        String jobEdDt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        // 7. 동의정보 기본등록 완료
        paramMap.put("jobStDt", jobStDt);
        paramMap.put("jobEdDt", jobEdDt);
        consentDao.updateFhrAgreCmps(paramMap);

    }
}
