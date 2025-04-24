package kr.co.iteyes.fhirmeta.service;

import kr.co.iteyes.fhirmeta.dto.AgentDto;
import kr.co.iteyes.fhirmeta.utils.DaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ComAgntDao {

    @Autowired
    DaoHelper daoHelper;

    /**
     * 에이전트 업데이트 대상 조회
     * @param paramMap
     * @return
     * @throws Exception
     */
    public List<AgentDto.AgentUpdateTargetResponse> getAgentUpdateTargetList(Map<String, Object> paramMap) throws Exception {
        String sql =
                " SELECT A.AGNT_KDCD \"agentKindCode\", " +
                " 	A.AGNT_VER_NO \"agentVersionNumber\", " +
                " 	B.INSTL_FILE_NO \"installFileNumber\", " +
                " 	C.INSTL_DMND_NO \"installDemandNumber\", " +
                " 	A.AGNT_VER_NM \"versionName\", " +
                " 	D.DWNLD_URL_ADDR \"downloadUrl\" " +
                " FROM TCOM_AGNT A " +
                " JOIN TFHR_AGNTFILE B " +
                " 	ON A.AGNT_KDCD=B.AGNT_KDCD AND A.AGNT_VER_NO=B.AGNT_VER_NO " +
                " JOIN TFHR_AGNTINSTLDMND C " +
                " 	ON B.INSTL_FILE_NO=C.INSTL_FILE_NO " +
                " JOIN TFHR_AGNTINSTLDMND_DTL D " +
                " 	ON C.INSTL_DMND_NO=D.INSTL_DMND_NO " +
                " WHERE D.CISN=:cisn " +
                " 	AND D.INSTL_YN='N' " +
                " 	AND D.AGNT_INSTL_STCD='30' " +
                " 	AND C.INSTL_BGNG_PRNMNT_DT < TO_CHAR(TRUNC(SYSDATE+1), 'YYYYMMDDHH24MISS') ";

        return daoHelper.getResultList(sql, AgentDto.AgentUpdateTargetResponse.class, paramMap);
    }

    /**
     * 에이전트 업데이트 처리결과 조회
     * @param paramMap
     * @return
     * @throws Exception
     */
    public List<AgentDto.AgentUpdateRequestResultResponse> getAgentUpdateResultList(Map<String, Object> paramMap) throws Exception {
        //에이전트 설치상태코드: [30: 접수, 41:성공, 42:실패, 43:취소, 44:무효, 45:생략]
        String sql =
                " SELECT " +
                " 	INSTL_FILE_NO AS \"installationFileNo\", " +
                " 	INSTL_DMND_NO AS \"installationDemandNo\", " +
                " 	CISN AS \"careInstitutionSign\", " +
                " 	PNST_NO AS \"provideInstitutionNo\", " +
                " 	TO_CHAR(TO_DATE(INSTL_YMD,'YYYYMMDD'),'YYYY-MM-DD') AS \"installationDate\", " +
                " 	(CASE WHEN AGNT_INSTL_STCD='41' AND INSTL_DMND_NO != MAX_INSTL_DMND_NO THEN '44' " +
                " 		ELSE AGNT_INSTL_STCD END) AS \"installationStatusCode\" " +
                " FROM ( " +
                " 	SELECT A.INSTL_FILE_NO, A.INSTL_DMND_NO, B.CISN, D.PNST_NO, B.INSTL_YMD, B.AGNT_INSTL_STCD, " +
                " 		MAX(DECODE(B.AGNT_INSTL_STCD,'41',B.INSTL_DMND_NO,0))  " +
                " 			OVER(PARTITION BY B.INSTL_YMD, B.CISN, C.AGNT_KDCD) MAX_INSTL_DMND_NO " +
                " 	FROM TFHR_AGNTINSTLDMND A " +
                " 	JOIN TFHR_AGNTINSTLDMND_DTL B " +
                " 		ON A.INSTL_DMND_NO=B.INSTL_DMND_NO " +
                " 	JOIN TFHR_AGNTFILE C " +
                " 		ON A.INSTL_FILE_NO=C.INSTL_FILE_NO " +
                " 	LEFT JOIN TMST_MDST D " +
                " 		ON B.CISN=D.CISN " +
                " 	WHERE B.INSTL_YMD = :baseDate AND B.AGNT_INSTL_STCD IN ('41','42','43','44') " +
                " ) T ";

        return daoHelper.getResultList(sql, AgentDto.AgentUpdateRequestResultResponse.class, paramMap);
    }

    /**
     * 에이전트 설치요청 상세 생성
     * @param paramMap
     * @return
     * @throws Exception
     */
    public int insertAgntInstlDmndDtl(Map<String, Object> paramMap) throws Exception {
        String sql =
                " INSERT INTO TFHR_AGNTINSTLDMND_DTL ( " +
                " 	INSTL_DMND_NO, " +
                " 	CISN, " +
                " 	DWNLD_URL_ADDR, " +
                " 	INSTL_YN, " +
                " 	AGNT_INSTL_STCD, " +
                " 	INSTL_YMD " +
                " ) " +
                " SELECT " +
                " 	:instlDmndNo AS INSTL_DMND_NO, " +
                " 	:cisn AS CISN, " +
                " 	:downloadUrl AS DWNLD_URL_ADDR, " +
                " 	:instlYn AS INSTL_YN, " +
                " 	:agntInstlStcd AS AGNT_INSTL_STCD, " +
                " 	:instlYmd AS INSTL_YMD " +
                " FROM DUAL " +
                " WHERE NOT EXISTS (SELECT 1 FROM TFHR_AGNTINSTLDMND_DTL WHERE INSTL_DMND_NO = :instlDmndNo AND CISN = :cisn) ";

        return daoHelper.executeUpdate(sql, paramMap);
    }

    /**
     * 에이전트 설치요청 상세 취소
     * @param paramMap
     * @return
     * @throws Exception
     */
    public int cancelAgntInstlDmndDtl(Map<String, Object> paramMap) throws Exception {
        String sql =
                " UPDATE TFHR_AGNTINSTLDMND_DTL T " +
                " SET AGNT_INSTL_STCD='43', " +
                "     INSTL_YMD = :planYmd  " +
                " WHERE CISN = :cisn AND INSTL_YN='N' AND AGNT_INSTL_STCD='30' " +
                " 	AND INSTL_DMND_NO IN ( " +
                " 		SELECT DISTINCT A.INSTL_DMND_NO " +
                " 		FROM TFHR_AGNTINSTLDMND A " +
                " 		JOIN TFHR_AGNTFILE B " +
                " 			ON A.INSTL_FILE_NO=B.INSTL_FILE_NO " +
                " 		WHERE A.INSTL_BGNG_PRNMNT_DT LIKE :planYmd || '%' " +
                " 			AND B.AGNT_KDCD = :agntKdcd " +
                " 	) ";

        return daoHelper.executeUpdate(sql, paramMap);
    }

    /**
     * 중복설치 무효화
     * @param paramMap
     * @return
     * @throws Exception
     */
    public int invalidAgntInstlDmndDtl(Map<String, Object> paramMap) throws Exception {
        String sql =
                " UPDATE TFHR_AGNTINSTLDMND_DTL " +
                " SET AGNT_INSTL_STCD='44' " +
                " WHERE CISN = :cisn AND INSTL_DMND_NO IN ( " +
                " 	SELECT DISTINCT A.INSTL_DMND_NO " +
                " 	FROM TFHR_AGNTINSTLDMND A " +
                " 	JOIN TFHR_AGNTINSTLDMND_DTL B " +
                " 		ON A.INSTL_DMND_NO=B.INSTL_DMND_NO " +
                " 	JOIN TFHR_AGNTFILE C " +
                " 		ON A.INSTL_FILE_NO=C.INSTL_FILE_NO " +
                " 	WHERE B.INSTL_YMD = :instlYmd AND B.CISN = :cisn AND B.INSTL_YN='Y' " +
                " 		AND B.AGNT_INSTL_STCD='41' AND C.AGNT_KDCD = :agntKdcd " +
                " ) ";

        return daoHelper.executeUpdate(sql, paramMap);
    }

    /**
     * 현재 에이전트 번호 조회
     * @param paramMap
     * @return
     * @throws Exception
     */
    public AgentDto.AgentVersionResult getCurrentAgntVerNo(Map<String, Object> paramMap) throws Exception {
        String sql =
                " SELECT NVL(MAX(AGNT_VER_NO),0) AS \"agentVersionNumber\" " +
                " FROM TFHR_PNSTAGNT " +
                " WHERE AGNT_KDCD = :agntKdcd AND CISN = :cisn AND LAST_YN='Y' ";

        return daoHelper.getSingleResult(sql, AgentDto.AgentVersionResult.class, paramMap);
    }

}
