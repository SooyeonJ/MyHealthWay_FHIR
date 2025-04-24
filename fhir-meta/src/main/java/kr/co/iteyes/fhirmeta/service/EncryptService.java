package kr.co.iteyes.fhirmeta.service;

import javax.annotation.Resource;
import kr.co.iteyes.fhirmeta.dto.OrganizationSeedDto;
import kr.co.iteyes.fhirmeta.dto.OrganizationSeedStatusDto;
import kr.co.iteyes.fhirmeta.dto.SeedStatusDto;
import kr.co.iteyes.fhirmeta.entity.*;
import kr.co.iteyes.fhirmeta.exception.CustomException;
import kr.co.iteyes.fhirmeta.exception.ExceptionEnum;
import kr.co.iteyes.fhirmeta.repository.EncryptKeyRepository;
import kr.co.iteyes.fhirmeta.repository.RsaOaepRepository;
import kr.co.iteyes.fhirmeta.repository.SeedCtrRepository;
import kr.co.iteyes.fhirmeta.utils.RsaUtils;
import kr.co.iteyes.fhirmeta.utils.SeedCtrUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.security.InvalidKeyException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class EncryptService {

    private final EncryptKeyRepository encryptKeyRepository;
    private final RsaOaepRepository rsaOaepRepository;
    private final SeedCtrRepository seedCtrRepository;

    public static final String PLATFORM_CISN = "00000000";
    public static final LocalDateTime DEFAULT_VALID_DATE = LocalDateTime.of(2029, 12, 31, 23, 59, 59);
    public static final String TYPE_RSA = "RSA";
    public static final String TYPE_SEED = "SEED";

    @Resource
    private EncryptKeyDao encryptKeyDao;

    public EncryptKey getValidKey(String cisn, String type) throws InvalidKeyException {
        Optional<EncryptKey> encryptKey = encryptKeyRepository.findByEncryptKeyIdAndValidDtAfter(EncryptKeyId.builder().cisn(cisn).type(type).build(), new Timestamp(System.currentTimeMillis()));
        return encryptKey.orElseThrow(InvalidKeyException::new);
    }

    public boolean isValidKey(String cisn, String type) {
        boolean isValidKey = false;
        Optional<EncryptKey> encryptKey = encryptKeyRepository.findByEncryptKeyIdAndValidDtAfter(EncryptKeyId.builder().cisn(cisn).type(type).build(), new Timestamp(System.currentTimeMillis()));
        if(encryptKey.isPresent()) isValidKey = true;
        return isValidKey;
    }

    public EncryptKey getValidKey(String type) throws InvalidKeyException {
        return getValidKey(PLATFORM_CISN, type);
    }

    public EncryptKey getRsaKey() {
        try {
            return getValidKey(TYPE_RSA);
        } catch (InvalidKeyException e) {
            return createRsaKey();
        }
    }

    public EncryptKey createRsaKey(String cisn, String issuRsnDvcd) {
        Map<String, String> keypair = RsaUtils.createKeyPair();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        RsaOaep rsaOaep = RsaOaep.builder()
                .cisn(cisn)
                .publicKey(keypair.get("publicKey"))
                .privateKey(keypair.get("privateKey"))
                .createDt(now)
                .issuRsnDvcd(issuRsnDvcd)
                .build();
        rsaOaepRepository.save(rsaOaep);

        EncryptKey encryptKey = EncryptKey.builder()
                .encryptKeyId(EncryptKeyId.builder().cisn(cisn).type(TYPE_RSA).build())
                .rsaOaep(rsaOaep)
                .validDt(Timestamp.valueOf(DEFAULT_VALID_DATE))
                .createDt(now)
                .updateDt(now)
                .build();
        encryptKeyRepository.save(encryptKey);
        return encryptKey;
    }

    public EncryptKey createRsaKey() {
        return createRsaKey(PLATFORM_CISN, "10");
    }

    public SeedCtr createSeedKey(String seedKey, String cisn, LocalDateTime validDt, String issuRsnDvcd) {
        // 요청 정보로 SEED 생성
        issuRsnDvcd = (issuRsnDvcd == null || issuRsnDvcd.isEmpty()) ? "10" : issuRsnDvcd; //[10:발급,20:갱신,30:폐기]
        Timestamp now = new Timestamp(System.currentTimeMillis());
        SeedCtr seedCtr = SeedCtr.builder()
                .key(seedKey)
                .createDt(now)
                .validDt(Timestamp.valueOf(validDt))
                .cisn(cisn)
                .issuDmndNo(null)
                .issuRsnDvcd(issuRsnDvcd)
                .build();

        seedCtrRepository.save(seedCtr);
        return seedCtr;
    }

    public SeedCtr createSeedKey(String seedKey, OrganizationSeedDto.IssuRequest issuRequest) {
        // 요청 정보로 SEED 생성
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Timestamp validDt = Timestamp.valueOf(issuRequest.getValidityDateTime());
        String cisn = issuRequest.getCareInstitutionSign();
        int issuDmndNo = Integer.parseInt(issuRequest.getIssueDemandNo());
        String aprvYmd = issuRequest.getApprovalDate().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String pnstNo = issuRequest.getProvideInstitutionNo();
        String issuRsnDvcd = issuRequest.getIssueDivisionCode();
        issuRsnDvcd = (issuRsnDvcd == null || issuRsnDvcd.isEmpty()) ? "10" : issuRsnDvcd; //[10:발급,20:갱신,30:폐기]

        SeedCtr seedCtr = SeedCtr.builder()
                .key(seedKey)
                .createDt(now)
                .validDt(validDt)
                .cisn(cisn)
                .issuDmndNo(issuDmndNo)
                .aprvYmd(aprvYmd)
                .pnstNo(pnstNo)
                .issuRsnDvcd(issuRsnDvcd)
                .build();

        seedCtrRepository.save(seedCtr);
        return seedCtr;
    }

    public SeedCtr createSeedKey(OrganizationSeedDto.IssuRequest issuRequest) {
        String key = SeedCtrUtils.SEED_CTR_create();
        return createSeedKey(key, issuRequest);
    }

    public SeedCtr createSeedKey(String cisn, LocalDateTime validDt, String issuDmndNo, LocalDate approvalDate, String pnstNo, String issuRsnDvcd) {
        String seedKey = SeedCtrUtils.SEED_CTR_create();
        return createSeedKey(seedKey, cisn, validDt, issuDmndNo, approvalDate, pnstNo, issuRsnDvcd);
    }

    public SeedCtr createSeedKey(String seedKey, String cisn, LocalDateTime validDt, String issuDmndNo, LocalDate approvalDate, String pnstNo, String issuRsnDvcd) {
        // 요청 정보로 SEED 생성
        Timestamp now = new Timestamp(System.currentTimeMillis());
        String aprvYmd = approvalDate != null ? approvalDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")) : "";
        issuRsnDvcd = (issuRsnDvcd == null || issuRsnDvcd.isEmpty()) ? "10" : issuRsnDvcd; //[10:발급,20:갱신,30:폐기]

        SeedCtr seedCtr = SeedCtr.builder()
                .key(seedKey)
                .createDt(now)
                .validDt(Timestamp.valueOf(validDt))
                .cisn(cisn)
                .issuDmndNo(Integer.parseInt(issuDmndNo))
                .aprvYmd(aprvYmd)
                .pnstNo(pnstNo)
                .issuRsnDvcd(issuRsnDvcd)
                .build();

        seedCtrRepository.save(seedCtr);
        return seedCtr;
    }

    public void discardSeedKey() {
        discardSeedKey(PLATFORM_CISN);
    }

    public void discardSeedKey(String cisn) {
        EncryptKeyId encryptKeyId = EncryptKeyId.builder()
                .cisn(cisn)
                .type(TYPE_SEED)
                .build();
        encryptKeyRepository.deleteById(encryptKeyId);
    }

    /**
     * 암호화(SEED) 키 폐기
     * @param cisn
     * @param issueDemandNo
     * @param approvalDate
     * @throws Exception
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public void revocateSeedEncryptKey(String cisn, String issueDemandNo, LocalDate approvalDate) throws Exception {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("cisn", cisn);
        paramMap.put("issueDemandNo", Integer.parseInt(issueDemandNo));
        paramMap.put("approvalDate", approvalDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        encryptKeyDao.expireSeedKey(paramMap); //암호화 키 만료
        encryptKeyDao.deActivateSeedEncryptKey(paramMap); //암호화(SEED) 키 비활성화
    }

    /**
     * 전체 제공기관 암호화(SEED) 키 폐기
     * @param issueDemandNo
     * @param approvalDate
     * @throws Exception
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public void revocateAllOrganizationSeedEncryptKey(String issueDemandNo, LocalDate approvalDate) throws Exception {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("platform_cisn", PLATFORM_CISN);
        paramMap.put("issueDemandNo", Integer.parseInt(issueDemandNo));
        paramMap.put("approvalDate", approvalDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        encryptKeyDao.expireAllOrganizationSeedKey(paramMap); //제공기관 전체 SEED 키 만료
        encryptKeyDao.deActivateAllOrganizationSeedEncryptKey(paramMap); //전체 제공기관 암호화(SEED) 키 비활성화
    }

    public EncryptKey activeSeedKey() {
        return activeSeedKey(PLATFORM_CISN);
    }

    public EncryptKey activeSeedKey(String cisn) {
        Timestamp now = new Timestamp(System.currentTimeMillis());

        SeedCtr seedCtr = getTempSeedKey(cisn);

        EncryptKeyId encryptKeyId = EncryptKeyId.builder()
            .cisn(cisn)
            .type(TYPE_SEED)
            .build();

        EncryptKey encryptKey = EncryptKey.builder()
                .encryptKeyId(encryptKeyId)
                .createDt(seedCtr.getCreateDt())
                .seedCtr(seedCtr)
                .validDt(seedCtr.getValidDt())
                .updateDt(now)
                .build();

        encryptKeyRepository.save(encryptKey);
        return encryptKey;
    }

    public EncryptKey getSeedKey(String cisn) throws InvalidKeyException {
        return getValidKey(cisn, TYPE_SEED);
    }

    public SeedCtr getTempSeedKey(String cisn) {
        List<SeedCtr> seedCtrList = seedCtrRepository.findByCisnOrderByCreateDtAsc(cisn);
        if(seedCtrList == null || seedCtrList.size() < 1) throw new CustomException(ExceptionEnum.INVALID_KEY_EXCEPTION);
        return seedCtrList.get(seedCtrList.size() - 1);
    }

    public SeedCtr getTempSeedKey() {
        return getTempSeedKey(PLATFORM_CISN);
    }

    public List<EncryptKey> getActiveSeedKey() {
        return encryptKeyRepository.findAllByType("SEED");
    }

    public List<OrganizationSeedStatusDto.Response> getOrganizationSeedKeyStatus(OrganizationSeedStatusDto.Request request) throws Exception {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("baseDate", request.getBaseDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")));

        return encryptKeyDao.getOrganizationSeedStatusList(paramMap);
    }

    public SeedStatusDto.Response getSeedKeyStatus(SeedStatusDto.Request request) throws Exception {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("baseDate", request.getBaseDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        paramMap.put("platform_cisn", PLATFORM_CISN);

        SeedStatusDto.Response response = encryptKeyDao.getSeedStatusInfo(paramMap); //제공기관 폐기상태 헤더정보
        if (response.getAllEncryptionKeyDiscardYN() == 'Y') {
            paramMap.put("issuDmndNo", response.getIssueDemandNo());
            List<SeedStatusDto.CareInstitutionSignResult> list = encryptKeyDao.getOrganizationDiscardList(paramMap); //제공기관 암호화 키 폐기상태 조회
            if (list != null && list.size() > 0) {
                response.setCareInstitutionSignResultList(list);
            }
        }
        return response;
    }

}
