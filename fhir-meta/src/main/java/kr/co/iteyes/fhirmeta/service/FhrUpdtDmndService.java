package kr.co.iteyes.fhirmeta.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.ServerValidationModeEnum;
import ca.uhn.fhir.rest.client.interceptor.BearerTokenAuthInterceptor;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import javax.servlet.http.HttpServletRequest;
import kr.co.iteyes.fhirmeta.code.FhrUpdtStatusCode;
import kr.co.iteyes.fhirmeta.dto.FhrRscUpdtDto;
import kr.co.iteyes.fhirmeta.dto.FhrUpdtDmndDto;
import kr.co.iteyes.fhirmeta.entity.FhrUpdtDmnd;
import kr.co.iteyes.fhirmeta.entity.FhrUpdtDmndId;
import kr.co.iteyes.fhirmeta.exception.CustomException;
import kr.co.iteyes.fhirmeta.exception.ExceptionEnum;
import kr.co.iteyes.fhirmeta.repository.FhrUpdtDmndRepository;
import kr.co.iteyes.fhirmeta.utils.Lz4Utils;
import kr.co.iteyes.fhirmeta.utils.SeedCtrUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class FhrUpdtDmndService {

    @Value("${fhirdb.schema}")
    private String fhirDbSchema;
    @Value("${fhir.server.url}")
    private String fhirServerUrl;

    private final AuthService authService;

    private final FhrUpdtDmndRepository fhrUpdtDmndRepository;
    private final FhrRscUpdtDao fhrRscUpdtDao;
    private final EncryptService encryptService;


    // YMD = yyyy-MM-dd
    private String getYmd() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return now.format(formatter).replaceAll("[^0-9]", "");
    }

    // DT = yyyy-MM-dd HH:mm:ss
    private String getDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return now.format(formatter).replaceAll("[^0-9]", "");
    }

    /**
     * IF-MHI-SB38
     * <p>
     * headers : serverDomainNo 추출
     * INITIAL : 적재요청 (20)
     **/
    @Transactional(propagation = Propagation.REQUIRED)
    public void createFhrUpdtDmnd(List<FhrUpdtDmndDto.CreateRequest> createRequests, HttpServletRequest httpServletRequest) throws Exception {

        List<FhrUpdtDmnd> fhrUpdtDmndList = new ArrayList<>();

        String stptNo = httpServletRequest.getHeader("serverDomainNo");
        String updateStcd = FhrUpdtStatusCode.INITIAL.getFhrUpdtDmndStcd();
        String regDt = getDateTime();

        for (FhrUpdtDmndDto.CreateRequest createRequest : createRequests) {
            FhrUpdtDmndId fhrUpdtDmndId = FhrUpdtDmndId.builder()
                    .stptNo(Long.valueOf(stptNo))
                    .updateDmndNo(createRequest.getUpdateDemandSerialNumber())
                    .build();

            FhrUpdtDmnd fhrUpdtDmnd = FhrUpdtDmnd.builder()
                    .fhrUpdtDmndId(fhrUpdtDmndId)
                    .appId(createRequest.getUtilizationServiceNo())
                    .appName(createRequest.getUtilizationServiceName())
                    .mhId(createRequest.getUtilizationUserNo())
                    .cisn(createRequest.getCareInstitutionSign())
                    .updateDmndBgngYmd(createRequest.getBeginningDate().replaceAll("[^0-9]", ""))
                    .updateDmndEndYmd(createRequest.getEndDate().replaceAll("[^0-9]", ""))
                    .updateDmndYmd(createRequest.getRegistrationDate().replaceAll("[^0-9]", ""))
                    .updateStcd(updateStcd)
                    .regDt(regDt)
                    .build();
            fhrUpdtDmndList.add(fhrUpdtDmnd);
        }
        fhrUpdtDmndRepository.saveAll(fhrUpdtDmndList);
    }

    /**
     * IF-MHI-SB31
     * <p>
     *
     * @param cisn
     **/
    public List<FhrUpdtDmndDto.ResponseForAgent> getRequestList(String cisn) throws Exception {

        // 요청정보 조회
        List<FhrUpdtDmnd> fhrUpdtDmndList = fhrUpdtDmndRepository.findAllByCisn(cisn);

        return FhrUpdtDmndDto.ResponseForAgent.fromList(fhrUpdtDmndList);
    }

    /**
     * IF-MHI-SB39
     * <p>
     *
     * @param updateRequest
     **/
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateFhrUpdtDmnd(FhrUpdtDmndDto.UpdateRequest updateRequest) throws Exception {
        String updateDt = getDateTime();

        try {
            // 기존 업데이트 요청 정보 조회
            FhrUpdtDmnd fhrUpdtDmnd = fhrUpdtDmndRepository.findAllByUpdateDmndNo(updateRequest.getUpdateDemandSerialNumber());

            // Agent 적재 결과 및 updateDt 변경
            fhrUpdtDmnd.update(updateRequest, updateDt);
            fhrUpdtDmndRepository.save(fhrUpdtDmnd);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * IF-MHI-SB32
     * <p>
     *
     * @param requestForPlt
     **/
    public List<FhrUpdtDmnd> getFhrUpdtDmndStatus(FhrUpdtDmndDto.RequestForPlt requestForPlt) {
        String date = requestForPlt.getBaseDate().replaceAll("[^0-9]", "");
        log.debug("request date : " + date);

        String startDate = date + "000000";
        String endDate = date + "235959";

        return fhrUpdtDmndRepository.findAllByUpdateDt(startDate, endDate);
    }

    /**
     * IF-MHI-SB44
     * <p>
     * delete 처리   (Resource - Encounter 순서)
     * expunge 처리  (Resource - Encounter 순서)
     *
     * @param requestDelete
     * @return
     **/

    // 하위 리소스 delete
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteResource(FhrRscUpdtDto.RequestDelete requestDelete) throws Exception {

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("tokenHashSpSystem", requestDelete.getTokenHashSpSystem());
        paramMap.put("tokenHashSpValue", requestDelete.getTokenHashSpValue());

        FhirContext ctx = FhirContext.forR4();
        ctx.getRestfulClientFactory().setServerValidationMode(ServerValidationModeEnum.NEVER);

        // Token 조회
        String token = authService.getAuthToken();
        BearerTokenAuthInterceptor authInterceptor = new BearerTokenAuthInterceptor(token);

        // FHIR client 생성
        IGenericClient client = ctx.newRestfulGenericClient(fhirServerUrl);
        client.registerInterceptor(authInterceptor);
        client.registerInterceptor(new LoggingInterceptor());

        // 삭제 대상 Resource ID 조회
        List<FhrRscUpdtDto.Resource> resources = fhrRscUpdtDao.getResourceInfo(paramMap, fhirDbSchema);

        for (FhrRscUpdtDto.Resource resource : resources) {
            String resourceType = resource.getResourceType();
            String resourceId = String.valueOf(resource.getResourceId());

            try {
                // FHIR delete API 실행
                MethodOutcome outcome = client
                        .delete()
                        .resourceById(resourceType, resourceId)
                        .execute();

                if (outcome.getResponseHeaders().equals(200)) {
                    String result = outcome.getResource().toString();
                    log.debug(result);
                }

            } catch (ResourceNotFoundException e) {
                String massage = resourceType + ", " + resourceId + "was not found :" + e.getMessage();
                log.error(massage, e);
                throw new CustomException(ExceptionEnum.NO_DATA_EXCEPTION);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new CustomException(ExceptionEnum.RESOURCE_DELETE_EXCEPTION);
            }
        }
    }

    // 하위 리소스 expunge 처리
    @Transactional(propagation = Propagation.REQUIRED)
    public void expungeResource(FhrRscUpdtDto.RequestDelete requestDelete, HttpServletRequest httpServletRequest) throws Exception {

        log.debug("fhirDbSchema : " + fhirDbSchema);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("tokenHashSpSystem", requestDelete.getTokenHashSpSystem());
        paramMap.put("tokenHashSpValue", requestDelete.getTokenHashSpValue());

        // SEED 키 조회
        String cisn = httpServletRequest.getHeader("cisn");
        String key = encryptService.getSeedKey(cisn).getSeedCtr().getKey();

        String fhirServerBaseUrl = fhirServerUrl;
        // 삭제 대상 Resource ID 조회
        List<FhrRscUpdtDto.Resource> resources = fhrRscUpdtDao.getResourceInfo(paramMap, fhirDbSchema);

        // Token 조회
        String token = authService.getAuthToken();

        // Body 생성
        String parametersJson =
                " { " +
                " 	\"resourceType\": \"Parameters\", " +
                " 	\"parameter\": [ " +
                " 		{ " +
                " 			\"name\": \"limit\", " +
                " 			\"valueInteger\": 1000 " +
                " 		}, " +
                " 		{ " +
                " 			\"name\": \"expungePreviousVersions\", " +
                " 			\"valueBoolean\": true " +
                " 		}, " +
                " 		{ " +
                " 			\"name\": \"expungeDeletedResources\", " +
                " 			\"valueBoolean\": true " +
                " 		}, " +
                " 		{ " +
                " 			\"name\": \"expungeEverything\", " +
                " 			\"valueBoolean\": true " +
                " 		} " +
                " 	] " +
                " } ";

        // 압축
        Lz4Utils lz4Utils = new Lz4Utils();
        byte compressTemp[] = lz4Utils.compress(parametersJson.getBytes("utf-8"));
        // 암호화
        String parameter = SeedCtrUtils.SEED_CTR_Encrypt(key, compressTemp);

        try {
            // Http client 생성
            CloseableHttpClient client = HttpClientBuilder.create().build();

            for (FhrRscUpdtDto.Resource resource : resources) {
                String resourceType = resource.getResourceType();
                String resourceId = String.valueOf(resource.getResourceId());

                String expungeUrl = fhirServerBaseUrl + "/" + resourceType + "/" + resourceId + "/$expunge";

                // FHIR expunge API 실행 (httpClient 이용)
                HttpPost httpPost = new HttpPost(expungeUrl);
                httpPost.addHeader("Authorization", "Bearer " + token);
                httpPost.addHeader("Content-Type", "application/json");
                httpPost.addHeader("CISN", cisn);
                httpPost.setEntity(new StringEntity(parameter));
                org.apache.http.HttpResponse response = client.execute(httpPost);

                log.info(httpPost.getURI().toString());
                log.info(response.getStatusLine().toString());

                if (response.getStatusLine().getStatusCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
                }

                response.getEntity().getContent().close();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CustomException(ExceptionEnum.EXPUNGE_EXCEPTION);
        }

    }


    // Encounter delete
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteEncounter(FhrRscUpdtDto.RequestDelete requestDelete) throws Exception {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("tokenHashSpSystem", requestDelete.getTokenHashSpSystem());
        paramMap.put("tokenHashSpValue", requestDelete.getTokenHashSpValue());

        FhirContext ctx = FhirContext.forR4();
        ctx.getRestfulClientFactory().setServerValidationMode(ServerValidationModeEnum.NEVER);

        // Token 조회
        String token = authService.getAuthToken();
        BearerTokenAuthInterceptor authInterceptor = new BearerTokenAuthInterceptor(token);

        // FHIR client 생성
        IGenericClient client = ctx.newRestfulGenericClient(fhirServerUrl);
        client.registerInterceptor(authInterceptor);
        client.registerInterceptor(new LoggingInterceptor());

        // 삭제 대상 Resource ID 조회
        FhrRscUpdtDto.Resource resource = fhrRscUpdtDao.getEncounterInfo(paramMap, fhirDbSchema);

        String resourceType = resource.getResourceType();
        String resourceId = String.valueOf(resource.getResourceId());

        try {
            // FHIR delete API 실행
            MethodOutcome outcome = client
                    .delete()
                    .resourceById(resourceType, resourceId)
                    .execute();

            if (outcome.getResponseHeaders().equals(200)) {
                String result = outcome.getResource().toString();
                log.debug(result);
            }

        } catch (ResourceNotFoundException e) {
            String massage = resourceType + ", " + resourceId + "was not found :" + e.getMessage();
            log.error(massage, e);
            throw new CustomException(ExceptionEnum.NO_DATA_EXCEPTION);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CustomException(ExceptionEnum.RESOURCE_DELETE_EXCEPTION);
        }
    }


    // Encounter expunge
    @Transactional(propagation = Propagation.REQUIRED)
    public void expungeEncounter(FhrRscUpdtDto.RequestDelete requestDelete, HttpServletRequest httpServletRequest) throws Exception {

        log.debug("fhirDbSchema : " + fhirDbSchema);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("tokenHashSpSystem", requestDelete.getTokenHashSpSystem());
        paramMap.put("tokenHashSpValue", requestDelete.getTokenHashSpValue());

        // SEED 키 조회
        String cisn = httpServletRequest.getHeader("cisn");
        String key = encryptService.getSeedKey(cisn).getSeedCtr().getKey();

        String fhirServerBaseUrl = fhirServerUrl;
        // 삭제 대상 Resource ID 조회
        FhrRscUpdtDto.Resource resource = fhrRscUpdtDao.getEncounterInfo(paramMap, fhirDbSchema);

        // Token 조회
        String token = authService.getAuthToken();

        // Body 생성
        String parametersJson =
                " { " +
                " 	\"resourceType\": \"Parameters\", " +
                " 	\"parameter\": [ " +
                " 		{ " +
                " 			\"name\": \"limit\", " +
                " 			\"valueInteger\": 1000 " +
                " 		}, " +
                " 		{ " +
                " 			\"name\": \"expungePreviousVersions\", " +
                " 			\"valueBoolean\": true " +
                " 		}, " +
                " 		{ " +
                " 			\"name\": \"expungeDeletedResources\", " +
                " 			\"valueBoolean\": true " +
                " 		}, " +
                " 		{ " +
                " 			\"name\": \"expungeEverything\", " +
                " 			\"valueBoolean\": true " +
                " 		} " +
                " 	] " +
                " } ";
        // 압축
        Lz4Utils lz4Utils = new Lz4Utils();
        byte compressTemp[] = lz4Utils.compress(parametersJson.getBytes("utf-8"));
        // 암호화
        String parameter = SeedCtrUtils.SEED_CTR_Encrypt(key, compressTemp);

        try {
            // Http client 생성
            CloseableHttpClient client = HttpClientBuilder.create().build();

            String resourceType = resource.getResourceType();
            String resourceId = String.valueOf(resource.getResourceId());

            String expungeUrl = fhirServerBaseUrl + "/" + resourceType + "/" + resourceId + "/$expunge";

            // FHIR expunge API 실행 (httpClient 이용)
            HttpPost httpPost = new HttpPost(expungeUrl);
            httpPost.addHeader("Authorization", "Bearer " + token);
            httpPost.addHeader("Content-Type", "application/json");
            httpPost.addHeader("CISN", cisn);
            httpPost.setEntity(new StringEntity(parameter));
            org.apache.http.HttpResponse response = client.execute(httpPost);

            log.info(httpPost.getURI().toString());
            log.info(response.getStatusLine().toString());

            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
            }
            response.getEntity().getContent().close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CustomException(ExceptionEnum.EXPUNGE_EXCEPTION);
        }
    }
}