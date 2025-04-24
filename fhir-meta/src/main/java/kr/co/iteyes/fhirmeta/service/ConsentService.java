package kr.co.iteyes.fhirmeta.service;

import javax.annotation.Resource;
import kr.co.iteyes.fhirmeta.dto.*;
import kr.co.iteyes.fhirmeta.entity.*;
import kr.co.iteyes.fhirmeta.exception.CustomException;
import kr.co.iteyes.fhirmeta.exception.ExceptionEnum;
import kr.co.iteyes.fhirmeta.repository.ConsentRepository;
import kr.co.iteyes.fhirmeta.repository.FhrAgreCmpsRcptnRepository;
import kr.co.iteyes.fhirmeta.repository.FhrAgreCmpsRepository;
import kr.co.iteyes.fhirmeta.utils.SeedCtrUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class ConsentService {

    @Value("${database.encrytKey}")
    private String databaseEncrytKey;

    private final ConsentRepository consentRepository;
    private final FhrAgreCmpsRepository fhrAgreCmpsRepository;
    private final FhrAgreCmpsRcptnRepository fhrAgreCmpsRcptnRepository;

    private final ConsentTaskService consentTaskService;

    @Resource
    private ConsentDao consentDao;

    @Resource
    private ComAgntDao comAgntDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public void createConsent(ConsentCreateDto consentCreateDto) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        ConsentId consentId = ConsentId.builder()
                .mhId(consentCreateDto.getUtilizationUserNo())
                .appId(consentCreateDto.getUtilizationServiceNo())
                .build();

        // 의료데이터 전송 대상자 정보 생성
        Consent consent = Consent.builder()
                .consentId(consentId)
                .agreeDt(Timestamp.valueOf(consentCreateDto.getAgreementDateTime()))
                .mhSendDt(Timestamp.valueOf(consentCreateDto.getDeliveryDateTime()))
                .updateDt(now)
                .destructYn("N")
                .userNm(SeedCtrUtils.SEED_CTR_Encrypt(databaseEncrytKey, consentCreateDto.getUserName()))
                .rrno(SeedCtrUtils.SEED_CTR_Encrypt(databaseEncrytKey, consentCreateDto.getResidentRegistrationNumber()))
                .build();

        Optional<Consent> consentOptional = consentRepository.findById(consentId);

        if (consentOptional.isPresent()) {
            consent.setCreateDt(consentOptional.get().getCreateDt());
            Set<String> set = new LinkedHashSet<>();
            if(StringUtils.isNotBlank(consentOptional.get().getCisnList())) {
                set = new LinkedHashSet<>(Arrays.asList(consentOptional.get().getCisnList().split(",")));
                set.addAll(consentCreateDto.getCareInstitutionSignList());
            }
            consent.setCisnList(StringUtils.join(new ArrayList<>(set), ","));
        } else {
            consent.setCreateDt(now);

            consent.setCisnList(StringUtils.join(consentCreateDto.getCareInstitutionSignList(), ","));
        }

        consentRepository.save(consent);
    }

    public void disagreeConsent(ConsentDeleteDto consentDeleteDto, boolean isDestruct) {
        String mhId = consentDeleteDto.getUtilizationUserNo();
        String appId = consentDeleteDto.getUtilizationServiceNo();
        Timestamp now = new Timestamp(System.currentTimeMillis());

        ConsentId consentId = ConsentId.builder().mhId(mhId).appId(appId).build();

        Optional<Consent> consentOptional = consentRepository.findById(consentId);

        consentOptional.ifPresent(consent -> {
            consent.setDisagreeDt(Timestamp.valueOf(consentDeleteDto.getWithdrawalDateTime()));
            consent.setMhSendDt(Timestamp.valueOf(consentDeleteDto.getDeliveryDateTime()));
            consent.setUpdateDt(now);

            Set<String> set = new LinkedHashSet<>();
            if(StringUtils.isNotBlank(consentOptional.get().getCisnList())) {
                set = new LinkedHashSet<>(Arrays.asList(consentOptional.get().getCisnList().split(",")));
                set.removeAll(consentDeleteDto.getCareInstitutionSignList());
            }
            consent.setCisnList(StringUtils.join(new ArrayList<>(set), ","));

            consentRepository.save(consent);

            if(isDestruct && consentDeleteDto.getDeleteYN().equals("Y")) {
                consentRepository.updateConsentDestruct(mhId, now);
            }
        });
    }

    public Consent getConsent(ConsentId consentId){
        Optional<Consent> consentOptional = consentRepository.findById(consentId);
        if(consentOptional.isPresent()) {
            return consentOptional.get();
        } else {
            return null;
        }
    }

    /**
     * 사용자 동의상태 대사처리
     * @param dto
     * @param serverDomainNo
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveCompareRequest(ConsentDto.CompareRequest dto, String serverDomainNo) throws Exception {
        LocalDateTime nowDateTime = LocalDateTime.now();
        String currentTime = nowDateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int stptNo = Integer.parseInt(serverDomainNo);
        int cmpsJobNo = Integer.parseInt(dto.getComparisionJobNo());
        int pageNo = dto.getPage();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("stptNo", stptNo);
        paramMap.put("cmpsJobNo", cmpsJobNo);

        //동의정보 대사기본 등록
        if (pageNo == 1) {
            FhrAgreCmpsId fhrAgreCmpsId = new FhrAgreCmpsId(Integer.parseInt(serverDomainNo), Integer.parseInt(dto.getComparisionJobNo()));
            FhrAgreCmps fhrAgreCmps = FhrAgreCmps.builder()
                    .id(fhrAgreCmpsId)
                    .crtrDt(getLocalDateTimeString(dto.getBaseDateTime()))
                    .cmpsJobBgngDt(null)
                    .cmpsJobEndDt(null)
                    .agreRcptnNocs(1)
                    .agreCrctNocs(0)
                    .rcnttCrctNocs(0)
                    .regDt(currentTime)
                    .build();
            fhrAgreCmpsRepository.saveAndFlush(fhrAgreCmps);
        }

        //동의정보 수신 자료 등록
        if (dto.getAgreementStatusProcessList() != null) {
            //페이지 일련번호 검사
            if (pageNo > 1) {
                Optional<FhrAgreCmps> fhrAgreCmps = fhrAgreCmpsRepository.findById(new FhrAgreCmpsId(stptNo, cmpsJobNo));
                int expectedPageNo = 1;
                if (fhrAgreCmps.isPresent()) {
                    expectedPageNo = fhrAgreCmps.get().getAgreRcptnNocs() + 1;
                }
                if (expectedPageNo < pageNo) {
                    throw new CustomException(ExceptionEnum.INVALID_PARAMETER_EXCEPTION,
                            new String[]{"페이지 일련번호 오류", String.valueOf(expectedPageNo) + " 페이지 수신을 대기하고 있습니다."});
                }
            }

            consentDao.insertFhrAgreCmpsRcptnBulk(stptNo, cmpsJobNo, dto.getAgreementStatusProcessList()); //동의정보 수신 자료 저장

            if (pageNo > 1) {
                paramMap.put("pageNo", pageNo);
                consentDao.updateFhrAgrePageNo(paramMap); //수신 페이지번호 업데이트
            }

            //로깅
            Duration duration = Duration.between(nowDateTime, LocalDateTime.now());
            log.info("동의정보 수신 자료 등록 :: [cmpsJobNo: {}, pageNo: {}/{}, count: {}, duration(ms): {}]",
                    cmpsJobNo, pageNo, dto.getTotalPages(), dto.getAgreementStatusProcessList().size(), duration.toMillis());
        }

        //동의정보 대사처리
        if (pageNo == dto.getTotalPages()) {
            consentTaskService.saveComparePostProcess(serverDomainNo, dto.getComparisionJobNo());
        }
    }

    /**
     * 사용자 동의상태 대사처리 결과 조회
     * @param dto
     * @return
     * @throws Exception
     */
    public List<ConsentDto.CompareResultResponse> getCompareResultList(ConsentDto.BaseDateRequest dto) throws Exception {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("baseDate", getDateString(dto.getBaseDate()));
        paramMap.put("serverDomainNo", dto.getServerDomainNo());
        return consentDao.getCompareResultList(paramMap);
    }

    private String getLocalDateTimeString(LocalDateTime localDateTime) {
        String result = "";
        if (localDateTime != null)
            result = localDateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return result;
    }

    private String getDateString(String value) {
        if (value != null)
            value = value.replaceAll("\\D","");
        return value;
    }
}
