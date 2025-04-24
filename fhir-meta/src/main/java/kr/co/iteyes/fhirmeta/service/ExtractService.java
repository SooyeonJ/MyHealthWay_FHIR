package kr.co.iteyes.fhirmeta.service;

import kr.co.iteyes.fhirmeta.dto.ConsentCreateDto;
import kr.co.iteyes.fhirmeta.dto.ConsentDeleteDto;
import kr.co.iteyes.fhirmeta.dto.ExtractDto;
import kr.co.iteyes.fhirmeta.entity.Consent;
import kr.co.iteyes.fhirmeta.entity.ConsentId;
import kr.co.iteyes.fhirmeta.entity.Extract;
import kr.co.iteyes.fhirmeta.entity.ExtractId;
import kr.co.iteyes.fhirmeta.repository.ExtractRepository;
import kr.co.iteyes.fhirmeta.utils.SeedCtrUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ExtractService {

    private final ExtractRepository extractRepository;
    private final ConsentService consentService;

    @Value("${database.encrytKey}")
    private String databaseEncrytKey;

    @Transactional(propagation = Propagation.REQUIRED)
    public void createExtract(ConsentCreateDto consentCreateDto) {
        String mhId = consentCreateDto.getUtilizationUserNo();
        String appId = consentCreateDto.getUtilizationServiceNo();
        String userNm = consentCreateDto.getUserName();
        List<String> cisnList = consentCreateDto.getCareInstitutionSignList();
        String rrno = consentCreateDto.getResidentRegistrationNumber();
        createExtract(mhId, appId, userNm, cisnList, rrno, "Y");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void createExtractForNotUsed(ConsentDeleteDto consentDeleteDto) {
        String mhId = consentDeleteDto.getUtilizationUserNo();
        String appId = consentDeleteDto.getUtilizationServiceNo();
        ConsentId consentId = ConsentId.builder().mhId(mhId).appId(appId).build();
        Consent consent = consentService.getConsent(consentId);
        if(consent != null) {
            List<String> cisnList = consentDeleteDto.getCareInstitutionSignList();
            String userNm = SeedCtrUtils.SEED_CTR_Decrypt(databaseEncrytKey, consent.getUserNm());
            String rrno = SeedCtrUtils.SEED_CTR_Decrypt(databaseEncrytKey, consent.getRrno());
            createExtract(mhId, appId, userNm, cisnList, rrno, "N");
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void deleteExtract(ConsentDeleteDto consentDeleteDto) {
        List<ExtractId> extractIds = getExtractIds(consentDeleteDto.getUtilizationUserNo(), consentDeleteDto.getCareInstitutionSignList());
        extractRepository.deleteAllByIds(extractIds);
    }

    private List<ExtractId> getExtractIds(String mhId,  List<String> cisnList) {

        // ExtractId(mhId+cisn) 생성
        List<ExtractId> extractIds = new ArrayList<>();
        for (String cisn : cisnList) {
            ExtractId extractId = ExtractId.builder()
                    .mhId(mhId)
                    .cisn(cisn)
                    .build();
            extractIds.add(extractId);
        }
        return extractIds;
    }

    public List<ExtractDto.Response> getExtract(String cisn) {
        List<Extract> extracts = extractRepository.findByCisn(cisn);
        return ExtractDto.Response.fromList(extracts, databaseEncrytKey);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void createExtract(String mhId, String appId, String userNm, List<String> cisnList, String rrno, String useYn) {
        List<ExtractId> extractIds = getExtractIds(mhId, cisnList);

        // 추출대상 테이블에서 ExtractId(mhId+cisn)에 해당하는 모든 Extract 리스트 조회
        List<Extract> extracts = extractRepository.findAllById(extractIds);

        // 조회된 모든 Extract 리스트 에서 cisn 추출
        List<String> registeredCisns = extracts.stream()
                .map(e -> e.getExtractId().getCisn())
                .collect(Collectors.toList());

        List<Extract> newExtracts = new ArrayList<>();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        for (String cisn : cisnList) {
            boolean isContainsCisn = registeredCisns.contains(cisn);
            if(!isContainsCisn) {
                ExtractId extractId = ExtractId.builder()
                        .mhId(mhId)
                        .cisn(cisn)
                        .build();
                Extract extract = Extract.builder()
                        .createDt(now)
                        .updateDt(now)
                        .useYn(useYn)
                        .extractId(extractId)
                        .userNm(SeedCtrUtils.SEED_CTR_Encrypt(databaseEncrytKey, userNm))
                        .rrno(SeedCtrUtils.SEED_CTR_Encrypt(databaseEncrytKey, rrno))
                        .appId(appId)
                        .build();
                newExtracts.add(extract);
            }
        }
        extractRepository.saveAll(newExtracts);
    }
}
