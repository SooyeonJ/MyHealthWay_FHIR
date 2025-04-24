package kr.co.iteyes.fhirmeta.service;

import javax.annotation.Resource;
import kr.co.iteyes.fhirmeta.dto.AgentDto;
import kr.co.iteyes.fhirmeta.dto.OrganizationRegistDto;
import kr.co.iteyes.fhirmeta.dto.OrganizationSeedDto;
import kr.co.iteyes.fhirmeta.entity.*;
import kr.co.iteyes.fhirmeta.exception.CustomException;
import kr.co.iteyes.fhirmeta.exception.ExceptionEnum;
import kr.co.iteyes.fhirmeta.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class AgentService {

    private final PbAgntConfRepository pbAgntConfRepository;
    private final ComEmrRepository comEmrRepository;
    private final PnstEmrRepository pnstEmrRepository;
    private final AgntInstlDmndRepository agntInstlDmndRepository;
    private final AgntInstlDmndDtlRepository agntInstlDmndDtlRepository;
    private final AgntInstlDmndHisRepository agntInstlDmndHisRepository;
    private final ComAgntRepository comAgntRepository;
    private final FhrAgntFileRepository fhrAgntFileRepository;
    private final FhrPnstAgntRepository fhrPnstAgntRepository;
    private final FhrPnstAgntChkRepository fhrPnstAgntChkRepository;

    @Resource
    private ComAgntDao comAgntDao;

    /**
     * 에이전트 설정 저장
     * @param issuRequest
     * @return
     */
    public PbAgntConf createPbAgntConf(OrganizationSeedDto.IssuRequest issuRequest) {
        String nowDt = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        OrganizationRegistDto.PublicAgentSystem publicAgentSystem = issuRequest.getOutsideAgentSystem();

        String fhirSrvrDomnAddr = publicAgentSystem.getServerDomainAddress();
        if (fhirSrvrDomnAddr == null || fhirSrvrDomnAddr.isEmpty()) fhirSrvrDomnAddr = " ";

        PbAgntConf pbAgntConf = PbAgntConf.builder()
                .cisn(issuRequest.getCareInstitutionSign())
                .fhirSrvrDomnAddr(fhirSrvrDomnAddr)
                .thrd1BgngTm(Timestamp.valueOf(publicAgentSystem.getThread1StartTime()))
                .thrd1EndTm(Timestamp.valueOf(publicAgentSystem.getThread1EndTime()))
                .thrd1FlfmtCy(publicAgentSystem.getThread1Cycle())
                .thrd2BgngTm(Timestamp.valueOf(publicAgentSystem.getThread2StartTime()))
                .thrd2EndTm(Timestamp.valueOf(publicAgentSystem.getThread2EndTime()))
                .thrd2FlfmtCy(publicAgentSystem.getThread2Cycle())
                .thrd3BgngTm(Timestamp.valueOf(publicAgentSystem.getThread3StartTime()))
                .thrd3EndTm(Timestamp.valueOf(publicAgentSystem.getThread3EndTime()))
                .thrd3FlfmtCy(publicAgentSystem.getThread3Cycle())
                .frstLdgPrd(publicAgentSystem.getFirstLoadingPeriod())
                .ldgInqCy(publicAgentSystem.getLoadingCycle())
                .clntId(publicAgentSystem.getClientId())
                .clntSrtVal(publicAgentSystem.getClientSecretKey())
                .useYn("Y")
                .regDt(nowDt)
                .mdfcnDt(nowDt)
                .build();

        pbAgntConfRepository.save(pbAgntConf);

        // TODO PRIVATE AGENT 설정은 PUBLIC AGENT가 설치되고, 반대로 서버로 업데이트 요청 (JSON 통으로)
        return pbAgntConf;
    }

    /**
     * EMR 시스템 저장
     * @param issuRequest
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public void saveProvideEmrSystem(OrganizationSeedDto.IssuRequest issuRequest) {
        String cisn = issuRequest.getCareInstitutionSign(); //요양기관번호
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        List<OrganizationRegistDto.ProvideEmrSystem> list = issuRequest.getProvideEMRSystemList();
        if (list == null || list.isEmpty()) return;

        for (final OrganizationRegistDto.ProvideEmrSystem item : list){

            String regStcd = item.getRegistrationStatusCode();
            if (regStcd == null || regStcd.isEmpty()) regStcd = " ";

            //EMR 시스템 저장
            ComEmr comEmr = ComEmr.builder()
                    .emrSysNo(Integer.parseInt(item.getEmrSystemNo()))
                    .emrSysNm(item.getEmrSystemName())
                    .emrSysVerNm(item.getEmrSystemVersionName())
                    .dbmsKdcd(item.getDatabaseKindCode())
                    .dbmsVerNm(item.getDatabaseVersionName())
                    .emrTpcd(item.getEmrTypeCode())
                    .regStcd(regStcd)
                    .regDt(now)
                    .mdfcnDt(now)
                    .emrBzentyCd(item.getEmrEnterpriseCode())
                    .build();

            comEmrRepository.save(comEmr);

            //제공기관 EMR 시스템 저장
            PnstEmr pnstEmr = PnstEmr.builder()
                    .emrSysNo(Integer.parseInt(item.getEmrSystemNo()))
                    .useBgngYmd(getLocalDateString(item.getUseBeginningDate()))
                    .useEndYmd(getLocalDateString(item.getUseEndDate()))
                    .regDt(now)
                    .mdfcnDt(now)
                    .useYn(item.getUseYN())
                    .cisn(cisn)
                    .build();

            pnstEmrRepository.save(pnstEmr);
        }
    }

    /**
     * 에이전트 설치요청 리스트 저장(의료기관 등록)
     * @param issuRequest
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public void saveAgntInstlDmnd(OrganizationSeedDto.IssuRequest issuRequest) throws Exception {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String agntInstlStcd = "41";

        List<OrganizationRegistDto.AgentInstallationDemand> list = issuRequest.getAgentInstallationDemandList();
        if (list == null || list.isEmpty()) return;

        for (final OrganizationRegistDto.AgentInstallationDemand item : list) {
            int installationFileNo = Integer.parseInt(item.getInstallationFileNo());
            FhrAgntFile fhrAgntFile = checkAgentFileNo(installationFileNo); //에이전트 설치파일 정보 검사

            //에이전트 설치요청
            AgntInstlDmnd agntInstlDmnd = AgntInstlDmnd.builder()
                    .instlDmndNo(Integer.parseInt(item.getInstallationDemandNo()))
                    .instlFileNo(installationFileNo)
                    .instlBgngPrnmntDt(getLocalDateTimeDefaultString(item.getInstallationBeginningDate()))
                    .instlDmndDt(getLocalDateTimeDefaultString(item.getInstallationDemandDate()))
                    .regDt(now)
                    .mdfcnDt(now)
                    .build();

            agntInstlDmndRepository.save(agntInstlDmnd);

            //에이전트 설치요청 상세 등록(설치완료)
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("instlDmndNo", Integer.parseInt(item.getInstallationDemandNo()));
            paramMap.put("cisn", issuRequest.getCareInstitutionSign());
            paramMap.put("downloadUrl", ' ');
            paramMap.put("instlYn", "Y");
            paramMap.put("instlYmd", today);
            paramMap.put("agntInstlStcd", agntInstlStcd); // [30: 설치요청접수, 41: 설치완료]

            comAgntDao.insertAgntInstlDmndDtl(paramMap);

            //에이전트 설치상태 이력 생성
            Integer maxHistSeq = agntInstlDmndHisRepository.getMaxHistSeq(Integer.parseInt(item.getInstallationDemandNo()), issuRequest.getCareInstitutionSign());
            int histSeq = maxHistSeq != null ? maxHistSeq + 1 : 1;

            AgntInstlDmndHisId newAgntInstlDmndHisId = new AgntInstlDmndHisId(Integer.parseInt(item.getInstallationDemandNo()), issuRequest.getCareInstitutionSign(), histSeq);
            AgntInstlDmndHis agntInstlDmndHis = AgntInstlDmndHis.builder()
                    .id(newAgntInstlDmndHisId)
                    .agntInstlStcd(agntInstlStcd)
                    .stsChgDt(now)
                    .build();

            agntInstlDmndHisRepository.save(agntInstlDmndHis);

            //제공기관 에이전트 등록
            savePnstAgnt(issuRequest.getCareInstitutionSign(), fhrAgntFile.getAgntKdcd(), fhrAgntFile.getAgntVerNo(),
                    Integer.parseInt(item.getInstallationDemandNo()), agntInstlStcd);

        }
    }

    /**
     * 수집에이전트 및 설치파일 정보 저장
     * @param item
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveAgentInstallFile(AgentDto.AgentInstallFileRequest item) {
        String regDt = getLocalDateTimeDefaultString(item.getRegistrationDateTime());
        String mdfcnDt = getLocalDateTimeDefaultString(item.getUpdateDateTime());
        String fileRegDt = getLocalDateTimeString(item.getFileRegistrationDateTime());

        ComAgntId comAgntId = new ComAgntId(item.getAgentKindCode(), item.getAgentVersionNumber());

        //수집에이전트 저장
        ComAgnt comAgnt = ComAgnt.builder()
                .id(comAgntId)
                .agntNm(item.getAgentName())
                .agntVerNm(item.getVersionName())
                .mainChgCn(item.getMainChangeContents())
                .regStcd(item.getRegistrationStatusCode())
                .regDt(regDt)
                .mdfcnDt(mdfcnDt)
                .build();

        comAgntRepository.save(comAgnt);

        //에이전트 설치파일 정보 저장
        FhrAgntFile fhrAgntFile = FhrAgntFile.builder()
                .instlFileNo(item.getInstallFileNumber())
                .agntKdcd(item.getAgentKindCode())
                .agntVerNo(item.getAgentVersionNumber())
                .dbmsKdcd(item.getDatabaseKindCode())
                .fileChksm(item.getChecksum())
                .regStcd(item.getFileRegistrationStatusCode())
                .regDt(fileRegDt)
                .build();

        fhrAgntFileRepository.save(fhrAgntFile);
    }

    /**
     * 에이전트 업데이트 요청 정보 저장
     * @param list
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveAgentUpdate(List<AgentDto.AgentUpdateRequest> list) throws Exception {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        for (final AgentDto.AgentUpdateRequest item : list) {

            FhrAgntFile fhrAgntFile = checkAgentFileNo(item.getInstallFileNumber()); //에이전트 설치파일 정보 검사

            String agntKdcd = fhrAgntFile.getAgntKdcd(); //에이전트 종류코드
            String instlBgngPrnmntDt = getLocalDateTimeDefaultString(item.getInstallBeginningPrearrangementDateTime());
            String instlDmndDt = getDateTimeFormatString(item.getInstallDemandDateTime());
            String planYmd = instlBgngPrnmntDt.substring(0,8); //중복요청 취소처리을 위한 계획일자

            //파라미터 설정
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("instlDmndNo", item.getInstallDemandNumber());
            paramMap.put("cisn", item.getCareInstitutionSign());
            paramMap.put("downloadUrl", item.getDownloadUrl());
            paramMap.put("instlYn", "N");
            paramMap.put("instlYmd", "");
            paramMap.put("agntInstlStcd", "30"); // [30: 설치요청접수]
            paramMap.put("agntKdcd", agntKdcd);
            paramMap.put("planYmd", planYmd);

            //에이전트 중복 요청 건 취소 처리
            comAgntDao.cancelAgntInstlDmndDtl(paramMap);

            //에이전트 설치요청
            AgntInstlDmnd agntInstlDmnd = AgntInstlDmnd.builder()
                    .instlDmndNo(item.getInstallDemandNumber())
                    .instlFileNo(item.getInstallFileNumber())
                    .instlBgngPrnmntDt(instlBgngPrnmntDt)
                    .instlDmndDt(instlDmndDt)
                    .regDt(now)
                    .mdfcnDt(now)
                    .build();

            agntInstlDmndRepository.save(agntInstlDmnd);

            //에이전트 설치요청 상세 등록
            comAgntDao.insertAgntInstlDmndDtl(paramMap);
        }
    }

    /**
     * 에이전트 업데이트 대상 조회
     * @param item
     * @return
     */
    public List<AgentDto.AgentUpdateTargetResponse> getAgentUpdateTargetList(AgentDto.CisnRequest item) throws Exception {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("cisn", item.getCisn());

        return comAgntDao.getAgentUpdateTargetList(paramMap);
    }

    /**
     * 에이전트 업데이트 결과 저장
     * @param item
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveAgentUpdateResultReg(AgentDto.AgentUpdateResultRegRequest item) throws Exception {
        //설치상태코드(installationStatusCode): [41: 성공, 42: 실패, 43: 취소, 44: 무효]
        String instl_yn = (item.getInstallationStatusCode().equals("41") ? "Y" : "N");
        //설치일자는 성공일 경우 저장
        String installationDate = instl_yn.equals("Y") ? getLocalDateString(item.getInstallationDate()) : null;
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        //중복설치 무효화(기존 성공(41) 건을 무효(44) 처리함)
        if (instl_yn.equals("Y")) {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("cisn", item.getCisn());
            paramMap.put("instlYmd", installationDate);
            paramMap.put("agntKdcd", item.getAgentKindCode());
            comAgntDao.invalidAgntInstlDmndDtl(paramMap);
        }

        //에이전트 설치요청 상세 - 처리결과 업데이트
        AgntInstlDmndDtlId agntInstlDmndDtlId = new AgntInstlDmndDtlId(item.getInstallDemandNumber(), item.getCisn());
        agntInstlDmndDtlRepository.updateInstlResult(instl_yn, item.getInstallationStatusCode(), installationDate, agntInstlDmndDtlId);

        //에이전트 설치상태 이력 생성
        Integer maxHistSeq = agntInstlDmndHisRepository.getMaxHistSeq(item.getInstallDemandNumber(), item.getCisn());
        int histSeq = maxHistSeq != null ? maxHistSeq + 1 : 1;

        AgntInstlDmndHisId newAgntInstlDmndHisId = new AgntInstlDmndHisId(item.getInstallDemandNumber(), item.getCisn(), histSeq);
        AgntInstlDmndHis agntInstlDmndHis = AgntInstlDmndHis.builder()
                .id(newAgntInstlDmndHisId)
                .agntInstlStcd(item.getInstallationStatusCode())
                .stsChgDt(now)
                .build();

        agntInstlDmndHisRepository.save(agntInstlDmndHis);

        //제공기관 에이전트 등록
        if (instl_yn.equals("Y")) {
            savePnstAgnt(item.getCisn(), item.getAgentKindCode(), item.getAgentVersionNumber(), item.getInstallDemandNumber(), item.getInstallationStatusCode());
        }
    }

    /**
     * 에이전트 업데이트 처리결과 조회
     * @param item
     * @return
     */
    public List<AgentDto.AgentUpdateRequestResultResponse> getAgentUpdateResultList(AgentDto.BaseDateRequest item) throws Exception {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("baseDate", item.getBaseDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")));

        return comAgntDao.getAgentUpdateResultList(paramMap);
    }

    /**
     * 에이전트 서비스상태 검사정보 저장
     * @param item
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveAgentServiceStatus(AgentDto.AgentServiceStatusRegRequest item) throws Exception {
        String chkDt = getDateTimeFormatString(item.getChckDt());

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("agntKdcd", item.getAgentKindCode());
        paramMap.put("cisn", item.getCisn());
        AgentDto.AgentVersionResult result = comAgntDao.getCurrentAgntVerNo(paramMap);
        int verNo = (result != null && result.getAgentVersionNumber() != null) ? result.getAgentVersionNumber() : 0;
        FhrPnstAgntChkId fhrPnstAgntChkId = new FhrPnstAgntChkId(item.getCisn(), item.getAgentKindCode(), verNo, chkDt);
        FhrPnstAgntChk fhrPnstAgntChk = FhrPnstAgntChk.builder()
                .id(fhrPnstAgntChkId)
                .srvcStcd(item.getServiceStatusCode())
                .agntVerNm(item.getVersionName())
                .build();

        fhrPnstAgntChkRepository.save(fhrPnstAgntChk);
    }

    /**
     * 제공기관 에이전트 등록
     * @param cisn
     * @param agentKindCode
     * @param agentVersionNumber
     * @param installDemandNumber
     * @param installationStatusCode
     */
    private void savePnstAgnt(String cisn, String agentKindCode, int agentVersionNumber, int installDemandNumber, String installationStatusCode) {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        //최종여부 초기화
        fhrPnstAgntRepository.updateAllLastYn(now, cisn, agentKindCode);

        //제공기관 에이전트 등록
        FhrPnstAgntId fhrPnstAgntId = new FhrPnstAgntId(installDemandNumber, cisn, agentKindCode, agentVersionNumber);
        FhrPnstAgnt fhrPnstAgnt = FhrPnstAgnt.builder()
                .id(fhrPnstAgntId)
                .lastYn("Y")
                .regStcd(installationStatusCode)
                .regDt(now)
                .mdfcnDt(now)
                .build();

        fhrPnstAgntRepository.save(fhrPnstAgnt);
    }

    /**
     * 에이전트 설치파일 정보 검사
     * @param installationFileNo
     * @throws Exception
     */
    private FhrAgntFile checkAgentFileNo(int installationFileNo) throws Exception {
        //에이전트 설치파일 정보 검사
        Optional<FhrAgntFile> fhrAgntFile = fhrAgntFileRepository.findById(installationFileNo);
        if (fhrAgntFile.isEmpty()) {
            throw new CustomException(ExceptionEnum.INVALID_PARAMETER_EXCEPTION,
                    new String[]{"[INSTALL_FILE_INFO_REQUIRED]설치파일번호", "[" + installationFileNo + "] 에이전트 설치파일 정보가 등록되어 있지 않습니다."});
        }
        return fhrAgntFile.get();
    }

    private String getLocalDateString(LocalDate localDate) {
        String result = "";
        if (localDate != null)
            result = localDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return result;
    }

    private String getLocalDateTimeString(LocalDateTime localDateTime) {
        String result = "";
        if (localDateTime != null)
            result = localDateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return result;
    }

    private String getLocalDateTimeDefaultString(LocalDateTime localDateTime) {
        String result = "";
        if (localDateTime != null)
            result = localDateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        else
            result = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return result;
    }

    private String getDateTimeFormatString(String value) {
        if (value != null)
            value = value.replaceAll("-","").replaceAll(" ","").replaceAll(":","");
        if (StringUtils.isEmpty(value)) value = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return value;
    }

}
