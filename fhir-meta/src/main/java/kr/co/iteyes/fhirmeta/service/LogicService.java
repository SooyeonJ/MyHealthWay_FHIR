package kr.co.iteyes.fhirmeta.service;

import kr.co.iteyes.fhirmeta.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Service
public class LogicService {

    private final ConsentService consentService;
    private final ExtractService extractService;
    private final IndexService indexService;
    private final ServerStatusService serverStatusService;
    private final EncryptService encryptService;
    private final AuthService authService;
    private final AgentService agentService;
    private final MstMdstService mstMdstService;

    @Transactional(propagation = Propagation.REQUIRED)
    public void createConsent(ConsentCreateDto consentCreateDto) {

        // 의료 데이터 전송 대상자 정보 생성
        consentService.createConsent(consentCreateDto);
        // 추출대상 생성
        extractService.createExtract(consentCreateDto);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void disagreeConsent(ConsentDeleteDto consentDeleteDto) {
        // 의료 데이터 전송 대상자
        consentService.disagreeConsent(consentDeleteDto, false);

        // 추출대상 삭제
        extractService.deleteExtract(consentDeleteDto);

        // Not Used인 추출대상 재생성
        extractService.createExtractForNotUsed(consentDeleteDto);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteConsent(ConsentDeleteDto consentDeleteDto) {
        // 의료 데이터 전송 대상자
        consentService.disagreeConsent(consentDeleteDto, true);

        // 추출대상 삭제
        extractService.deleteExtract(consentDeleteDto);

        // Not Used인 추출대상 재생성
        extractService.createExtractForNotUsed(consentDeleteDto);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateIndex(IndexDto.RequestForUpdate request, String cisn) {
        // index 정보 수정
        indexService.updateIndex(request, cisn);

        updateServerStatus(cisn);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateIndex(IndexDto.RequestForUpdatePost request, String cisn) {
        // index 정보 수정
        indexService.updateIndex(request, cisn);

        updateServerStatus(cisn);
    }

    public void createIndex(IndexDto.RequestForInsert request) {
        // index 생성
        indexService.createIndex(request);

        updateServerStatus(request.getProvideInstitutionCode());
    }

    private void updateServerStatus(String cisn) {
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String strDate = dateFormat.format(date);
        // 업데이트(Agent가 리소스 적재 등) 완료후에 SERVER_STATUS 테이블에 fhirLastUpdatedYmd 업데이트
        serverStatusService.updateServerStatus(cisn, strDate);
    }

    /**
     * 제공서버 암호화(SEED)키 등록
     * @param dto
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void createProvideServerSeedKey(SeedDto.Request dto) throws Exception {
        String issueDemandNo = dto.getIssueDemandNo(); //발급요청번호
        LocalDate approvalDate = dto.getApprovalDate(); //승인일자
        LocalDateTime validDt = dto.getValidityDateTime(); //유효일시
        String seedKey = dto.getEncryptionKey(); //새로 전송받은 SEED 키

        // [제공서버 암호화(SEED) 키 폐기]
        encryptService.revocateSeedEncryptKey(EncryptService.PLATFORM_CISN, issueDemandNo, approvalDate);
        // [암호화(SEED) 키 생성]
        encryptService.createSeedKey(seedKey, EncryptService.PLATFORM_CISN, validDt, issueDemandNo, approvalDate, null, dto.getIssueDivisionCode());
    }

    /**
     * 제공서버 암호화 키 갱신
     * @param dto
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateProvideServerSeedKey(SeedDto.Request dto) throws Exception {
        String issueDemandNo = dto.getIssueDemandNo(); //발급요청번호
        LocalDate approvalDate = dto.getApprovalDate(); //승인일자
        LocalDateTime validDt = dto.getValidityDateTime(); //유효일시
        String seedKey = dto.getEncryptionKey(); //새로 전송받은 SEED 키

        // [제공서버 암호화(SEED) 키 폐기]
        encryptService.revocateSeedEncryptKey(EncryptService.PLATFORM_CISN, issueDemandNo, approvalDate);
        // [제공서버 암호화(SEED) 키 생성]
        encryptService.createSeedKey(seedKey, EncryptService.PLATFORM_CISN, validDt, issueDemandNo, approvalDate, null, dto.getIssueDivisionCode());
    }

    /**
     * 제공서버 암호화 키 폐기
     * @param dto
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void discardProvideServerSeedKey(SeedDto.DscdRequest dto) throws Exception {
        String issueDemandNo = dto.getIssueDemandNo(); //발급요청번호
        LocalDate approvalDate = dto.getApprovalDate(); //승인일자

        // [전체 제공기관 암호화(SEED) 키 폐기]
        encryptService.revocateAllOrganizationSeedEncryptKey(issueDemandNo, approvalDate);
        // [제공서버 암호화(SEED) 키 폐기]
        encryptService.revocateSeedEncryptKey(EncryptService.PLATFORM_CISN, issueDemandNo, approvalDate);
    }

    /**
     * 의료기관 등록(+token) 및 암호화 키 발급
     * @param list
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void issueOrganizationSeedKey(List<OrganizationSeedDto.IssuRequest> list) throws Exception {
        for (final OrganizationSeedDto.IssuRequest item : list) {
            // [기관정보 마스터 등록]
            mstMdstService.createMstMdst(item);

            // [제공기관 EMR 시스템 등록]
            List<OrganizationRegistDto.ProvideEmrSystem> provideEmrSystemList = item.getProvideEMRSystemList();
            if (provideEmrSystemList != null && !provideEmrSystemList.isEmpty()) {
                agentService.saveProvideEmrSystem(item);
            }

            // [외부 에이전트 설정 등록]
            OrganizationRegistDto.PublicAgentSystem publicAgentSystem = item.getOutsideAgentSystem();
            if (publicAgentSystem != null) {
                // [외부 에이전트 설정 저장]
                agentService.createPbAgntConf(item);
                // [토큰 생성]
                authService.createAuthToken(item.getOutsideAgentSystem().getClientId(), item.getOutsideAgentSystem().getClientSecretKey());
            }

            // [에이전트 설치 목록 등록]
            List<OrganizationRegistDto.AgentInstallationDemand> agentInstallationDemand = item.getAgentInstallationDemandList();
            if (agentInstallationDemand != null && !agentInstallationDemand.isEmpty()) {
                agentService.saveAgntInstlDmnd(item);
            }

            // [암호화(SEED) 키 발급여부 검사]
            if (!encryptService.isValidKey(item.getCareInstitutionSign(), "SEED")) {
                // [암호화(SEED) 키 폐기]
                encryptService.revocateSeedEncryptKey(item.getCareInstitutionSign(), item.getIssueDemandNo(), item.getApprovalDate());
                // [암호화 키 생성]
                encryptService.createSeedKey(item);
                // [암호화 키 활성화]
                encryptService.activeSeedKey(item.getCareInstitutionSign());
            }
        }

    }

    /**
     * 의료기관 암호화 키 갱신
     * @param list
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateOrganizationSeedKey(List<OrganizationSeedDto.UpdtRequest> list) throws Exception {
        for (final OrganizationSeedDto.UpdtRequest item : list) {
            String cisn = item.getCareInstitutionSign(); //요양기관번호
            String issueDemandNo = item.getIssueDemandNo(); //발급요청번호
            LocalDate approvalDate = item.getApprovalDate(); //승인일자
            LocalDateTime validDt = item.getValidityDateTime(); //유효일시

            // [암호화(SEED) 키 폐기]
            encryptService.revocateSeedEncryptKey(cisn, issueDemandNo, approvalDate);
            // [암호화 키 생성]
            encryptService.createSeedKey(cisn, validDt, issueDemandNo, approvalDate, item.getProvideInstitutionNo(), item.getIssueDivisionCode()); //암호화(SEED) 키 생성
            // [암호화 키 활성화]
            encryptService.activeSeedKey(cisn); //암호화(SEED) 키 활성화
        }

    }

    /**
     * 의료기관 암호화 키 폐기
     * @param list
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void discardOrganizationSeedKey(List<OrganizationSeedDto.DscdRequest> list) throws Exception {
        for (final OrganizationSeedDto.DscdRequest item : list) {
            String cisn = item.getCareInstitutionSign(); //요양기관번호
            String issueDemandNo = item.getIssueDemandNo(); //발급요청번호
            LocalDate approvalDate = item.getApprovalDate(); //승인일자

            // [암호화(SEED) 키 폐기]
            encryptService.revocateSeedEncryptKey(cisn, issueDemandNo, approvalDate);
        }
    }
}
