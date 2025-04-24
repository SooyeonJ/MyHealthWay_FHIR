package kr.co.iteyes.fhirmeta.service;

import kr.co.iteyes.fhirmeta.dto.ConsentDto;
import kr.co.iteyes.fhirmeta.utils.DaoHelper;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Component
public class ConsentDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    DaoHelper daoHelper;

    /**
     * 사용자 동의상태 대사처리 결과 조회
     * @param paramMap
     * @return
     * @throws Exception
     */
    public List<ConsentDto.CompareResultResponse> getCompareResultList(Map<String, Object> paramMap) throws Exception {
        String sql =
                " SELECT " +
                " 	TO_CHAR(A.CMPS_JOB_NO) AS \"comparisionJobNo\", " +
                " 	A.CISN AS \"careInstitutionSign\", " +
                " 	TO_CHAR(A.STPT_NO) AS \"stptPnstNo\", " +
                " 	SUM(AGRE_CRCT_CNT) AS \"agreementCorrectionCount\", " +
                " 	SUM(DIS_ASRE_CRCT_CNT) AS \"withdrawalCorrectionCount\", " +
                " 	(SELECT COUNT(*) FROM EXTRACT WHERE CISN=A.CISN AND USE_YN='Y') AS \"lastAgreementCount\" " +
                " FROM ( " +
                " 	SELECT " +
                " 		NVL(A.STPT_NO, B.STPT_NO) STPT_NO, " +
                " 		NVL(A.CMPS_JOB_NO, B.CMPS_JOB_NO) CMPS_JOB_NO, " +
                " 		NVL(A.CISN, B.CISN) CISN, " +
                " 		NVL(B.AGRE_CRCT_CNT,0) AGRE_CRCT_CNT, " +
                " 		NVL(B.DIS_ASRE_CRCT_CNT,0) DIS_ASRE_CRCT_CNT " +
                " 	FROM ( " +
                " 		SELECT DISTINCT B.STPT_NO, B.CMPS_JOB_NO, B.CISN " +
                " 		FROM TFHR_AGRECMPS A " +
                " 		JOIN TFHR_AGRECMPSRCPTN B " +
                " 			ON A.STPT_NO = B.STPT_NO AND A.CMPS_JOB_NO = B.CMPS_JOB_NO " +
                " 		WHERE A.STPT_NO = :serverDomainNo AND A.CRTR_DT LIKE :baseDate || '%' " +
                " 	) A " +
                " 	FULL JOIN ( " +
                " 		SELECT DISTINCT B.STPT_NO, B.CMPS_JOB_NO, B.CISN, " +
                " 			SUM(CASE WHEN B.CRCT_SCS_YN='Y' AND B.CRCT_AFTR_AGRE_STCD='10' AND B.CRCT_BFR_AGRE_STCD='00' THEN 1 ELSE 0 END) AS AGRE_CRCT_CNT, " +
                " 			SUM(CASE WHEN B.CRCT_SCS_YN='Y' AND B.CRCT_AFTR_AGRE_STCD='00' AND B.CRCT_BFR_AGRE_STCD='10' THEN 1 ELSE 0 END) AS DIS_ASRE_CRCT_CNT " +
                " 		FROM TFHR_AGRECMPS A " +
                " 		JOIN TFHR_AGRECMPSCRCT B " +
                " 			ON A.STPT_NO = B.STPT_NO AND A.CMPS_JOB_NO = B.CMPS_JOB_NO " +
                " 		WHERE A.STPT_NO = :serverDomainNo AND A.CRTR_DT LIKE :baseDate || '%' " +
                " 		GROUP BY B.STPT_NO, B.CMPS_JOB_NO, B.CISN " +
                " 	) B " +
                " 	ON A.STPT_NO=B.CMPS_JOB_NO AND A.CISN=B.CISN " +
                " ) A " +
                " GROUP BY A.STPT_NO, A.CMPS_JOB_NO, A.CISN ";

        return daoHelper.getResultList(sql, ConsentDto.CompareResultResponse.class, paramMap);
    }

    /**
     * 동의정보 대사 수신 등록
     * @param stptNo
     * @param cmpsJobNo
     * @param list
     * @throws Exception
     */
    public void insertFhrAgreCmpsRcptnBulk(int stptNo, int cmpsJobNo, List<ConsentDto.AgreementStatusProces> list)  throws Exception {
        //String sql = "INSERT INTO TFHR_AGRECMPSRCPTN S (STPT_NO, CMPS_JOB_NO, APP_ID, MH_ID, CISN) SELECT ?, ?, ?, ?, ? FROM DUAL " +
        //        " WHERE NOT EXISTS (SELECT 1 FROM TFHR_AGRECMPSRCPTN WHERE STPT_NO=? AND CMPS_JOB_NO=? AND APP_ID=? AND MH_ID=? AND CISN=?) ";
        String sql = "INSERT INTO TFHR_AGRECMPSRCPTN (STPT_NO, CMPS_JOB_NO, APP_ID, MH_ID, CISN) VALUES (?, ?, ?, ?, ?) ";

        jdbcTemplate.batchUpdate(sql,
            new BatchPreparedStatementSetter() {
            @Override
            public void setValues(@NotNull PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, stptNo);
                ps.setInt(2, cmpsJobNo);
                ps.setString(3, list.get(i).getUtilizationServiceNo());
                ps.setString(4, list.get(i).getUtilizationUserNo());
                ps.setString(5, list.get(i).getCareInstitutionSign());
                //ps.setInt(6, stptNo);
                //ps.setInt(7, cmpsJobNo);
                //ps.setString(8, list.get(i).getUtilizationServiceNo());
                //ps.setString(9, list.get(i).getUtilizationUserNo());
                //ps.setString(10, list.get(i).getCareInstitutionSign());
            }

            @Override
            public int getBatchSize() {
                return list.size();
            }
        });
    };

    /**
     * 동의정보 보정 대상 등록
     * @param paramMap
     * @return
     * @throws Exception
     */
    public int insertFhrAgreCmpsCrct(Map<String, Object> paramMap) throws Exception {
        String sql =
                " INSERT INTO TFHR_AGRECMPSCRCT ( " +
                " 	STPT_NO, CMPS_JOB_NO, APP_ID, MH_ID, CISN, CRCT_SCS_YN, CRCT_BFR_AGRE_STCD, CRCT_AFTR_AGRE_STCD, CRCT_DT " +
                " ) " +
                " SELECT STPT_NO, CMPS_JOB_NO, APP_ID, MH_ID, CISN, CRCT_SCS_YN, CRCT_BFR_AGRE_STCD, CRCT_AFTR_AGRE_STCD, CRCT_DT " +
                " FROM ( " +
                " 	SELECT :stptNo AS STPT_NO, " +
                " 		:cmpsJobNo AS CMPS_JOB_NO, " +
                " 		NVL(A.APP_ID, B.APP_ID) AS APP_ID, " +
                " 		NVL(A.MH_ID, B.MH_ID) AS MH_ID, " +
                " 		NVL(A.CISN, B.CISN) AS CISN, " +
                " 		'N' AS CRCT_SCS_YN, " +
                " 		(CASE WHEN A.MH_ID IS NULL THEN '00' ELSE '10' END) CRCT_BFR_AGRE_STCD, " +
                " 		(CASE WHEN B.MH_ID IS NOT NULL THEN '10' ELSE '00' END) CRCT_AFTR_AGRE_STCD, " +
                " 		TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS') CRCT_DT " +
                " 	FROM ( " +
                " 		SELECT APP_ID, MH_ID, CISN " +
                " 		FROM EXTRACT S " +
                " 		WHERE USE_YN='Y' " +
                " 			AND EXISTS (SELECT 1 FROM CONSENT WHERE APP_ID=S.APP_ID AND MH_ID=S.MH_ID AND DESTRUCT_YN='N') " +
                " 	) A " +
                " 	FULL JOIN ( " +
                "		SELECT APP_ID, MH_ID, CISN " +
                "		FROM TFHR_AGRECMPSRCPTN S " +
                "		WHERE STPT_NO=:stptNo AND CMPS_JOB_NO=:cmpsJobNo " +
                "		    AND  EXISTS (SELECT 1 FROM CONSENT WHERE APP_ID=S.APP_ID AND MH_ID=S.MH_ID) " +
                "	) B " +
                " 		ON A.APP_ID=B.APP_ID AND A.MH_ID=B.MH_ID AND A.CISN=B.CISN " +
                " 	WHERE (A.MH_ID IS NULL OR B.MH_ID IS NULL) " +
                " ) A " +
                " WHERE NOT EXISTS(SELECT 1 FROM TFHR_AGRECMPSCRCT WHERE STPT_NO=A.STPT_NO AND CMPS_JOB_NO=A.CMPS_JOB_NO " +
                " 					AND APP_ID=A.APP_ID AND MH_ID=A.MH_ID AND CISN=A.CISN) ";
        return daoHelper.executeUpdate(sql, paramMap);
    }

    /**
     * 동의정보 보정 결과 업데이트
     * @param paramMap
     * @return
     * @throws Exception
     */
    public int updateFhrAgreCmpsCrctAll(Map<String, Object> paramMap) throws Exception {
        String sql =
                " UPDATE TFHR_AGRECMPSCRCT A " +
                " SET CRCT_SCS_YN = 'Y', " +
                " 	CRCT_DT = TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS') " +
                " WHERE STPT_NO=:stptNo AND CMPS_JOB_NO=:cmpsJobNo AND CRCT_SCS_YN = 'N' " +
                "   AND EXISTS(SELECT 1 FROM CONSENT WHERE APP_ID=A.APP_ID AND MH_ID=A.MH_ID) ";
        return daoHelper.executeUpdate(sql, paramMap);
    }

    /**
     * 수신 중 페이지번호 기록
     * @param paramMap
     * @return
     * @throws Exception
     */
    public int updateFhrAgrePageNo(Map<String, Object> paramMap) throws Exception {
        String sql =
                " UPDATE TFHR_AGRECMPS A " +
                " SET AGRE_RCPTN_NOCS = :pageNo " +
                " WHERE STPT_NO=:stptNo AND CMPS_JOB_NO=:cmpsJobNo ";
        return daoHelper.executeUpdate(sql, paramMap);
    }

    /**
     * 동의정보대사기본 업데이트
     * @param paramMap
     * @return
     * @throws Exception
     */
    public int updateFhrAgreCmps(Map<String, Object> paramMap) throws Exception {
        String sql =
                " UPDATE TFHR_AGRECMPS A " +
                " SET " +
                " 	CMPS_JOB_BGNG_DT = :jobStDt, " +
                " 	CMPS_JOB_END_DT = :jobEdDt, " +
                " 	AGRE_RCPTN_NOCS = (SELECT COUNT(*) FROM TFHR_AGRECMPSRCPTN WHERE STPT_NO=A.STPT_NO AND CMPS_JOB_NO=A.CMPS_JOB_NO), " +
                " 	AGRE_CRCT_NOCS = (SELECT COUNT(*) FROM TFHR_AGRECMPSCRCT WHERE STPT_NO=A.STPT_NO AND CMPS_JOB_NO=A.CMPS_JOB_NO AND CRCT_AFTR_AGRE_STCD='10' AND CRCT_SCS_YN='Y'), " +
                " 	RCNTT_CRCT_NOCS = (SELECT COUNT(*) FROM TFHR_AGRECMPSCRCT WHERE STPT_NO=A.STPT_NO AND CMPS_JOB_NO=A.CMPS_JOB_NO AND CRCT_AFTR_AGRE_STCD='00' AND CRCT_SCS_YN='Y') " +
                " WHERE STPT_NO=:stptNo AND CMPS_JOB_NO=:cmpsJobNo ";
        return daoHelper.executeUpdate(sql, paramMap);
    }

    /**
     * 동의정보 추가 [미사용: 주민번호가 없으면 에이전트에서 처리 불가]
     * @param paramMap
     * @return
     * @throws Exception
     */
    public int insertConsentCmpsAll(Map<String, Object> paramMap) throws Exception {
        String sql =
                " INSERT INTO CONSENT ( " +
                " 	APP_ID, MH_ID, AGREE_DT, CISN_LIST, CREATE_DT, DESTRUCT_YN,  " +
                " 	DISAGREE_DT, MH_SEND_DT, RRNO, UPDATE_DT, USER_NM, DESTRUCT_DT " +
                " ) " +
                " SELECT APP_ID, MH_ID, NULL AGREE_DT, NULL CISN_LIST, SYSTIMESTAMP CREATE_DT, 'N' DESTRUCT_YN, " +
                " 	NULL DISAGREE_DT, NULL MH_SEND_DT, NULL RRNO, SYSTIMESTAMP UPDATE_DT, NULL USER_NM, NULL DESTRUCT_DT " +
                " FROM ( " +
                " 	SELECT DISTINCT A.APP_ID, A.MH_ID " +
                " 	FROM TFHR_AGRECMPSCRCT A " +
                " 	WHERE A.STPT_NO = :stptNo AND A.CMPS_JOB_NO = :cmpsJobNo " +
                " 	  AND A.CRCT_SCS_YN = 'N' " +
                " 		AND NOT EXISTS (SELECT 1 FROM CONSENT WHERE APP_ID = A.APP_ID AND MH_ID = A.MH_ID) " +
                " ) T ";
        return daoHelper.executeUpdate(sql, paramMap);
    }

    /**
     * 동의정보 업데이트
     * @param paramMap
     * @return
     * @throws Exception
     */
    public int updateConsentCmpsAll(Map<String, Object> paramMap) throws Exception {
        String sql =
                " MERGE INTO CONSENT A  " +
                " USING (  " +
                " 	SELECT APP_ID, MH_ID, LISTAGG(DECODE(USE_YN,'Y',CISN,''), ',') WITHIN GROUP(ORDER BY CISN) CISN_LIST " +
                " 	FROM EXTRACT S " +
                " 	WHERE EXISTS (SELECT 1 FROM TFHR_AGRECMPSCRCT WHERE STPT_NO = :stptNo AND CMPS_JOB_NO = :cmpsJobNo " +
                " 					AND APP_ID = S.APP_ID AND MH_ID = S.MH_ID AND CRCT_SCS_YN = 'N') " +
                " 	GROUP BY APP_ID, MH_ID " +
                " ) B " +
                " 	ON (A.APP_ID=B.APP_ID AND A.MH_ID=B.MH_ID) " +
                " WHEN MATCHED THEN " +
                " 	UPDATE " +
                " 	SET " +
                " 		A.CISN_LIST = B.CISN_LIST, " +
                " 		A.UPDATE_DT = SYSTIMESTAMP  " ;
        return daoHelper.executeUpdate(sql, paramMap);
    }

    /**
     * 동의 추출대상 업데이트
     * @param paramMap
     * @return
     * @throws Exception
     */
    public int updateExtractCmpsAll(Map<String, Object> paramMap) throws Exception {
        String sql =
                " UPDATE EXTRACT A " +
                " SET " +
                " 	USE_YN = (SELECT DECODE(CRCT_AFTR_AGRE_STCD, '00', 'N', 'Y') FROM TFHR_AGRECMPSCRCT " +
                " 				WHERE STPT_NO = :stptNo AND CMPS_JOB_NO = :cmpsJobNo AND APP_ID = A.APP_ID AND MH_ID = A.MH_ID AND CISN = A.CISN), " +
                " 	UPDATE_DT = SYSTIMESTAMP " +
                " WHERE EXISTS (SELECT 1 FROM TFHR_AGRECMPSCRCT WHERE STPT_NO = :stptNo AND CMPS_JOB_NO = :cmpsJobNo " +
                " 				AND CRCT_SCS_YN = 'N' AND APP_ID = A.APP_ID AND MH_ID = A.MH_ID AND CISN = A.CISN) ";
        return daoHelper.executeUpdate(sql, paramMap);
    }

    /**
     * 동의 추출대상 추가
     * @param paramMap
     * @return
     * @throws Exception
     */
    public int insertExtractCmpsAll(Map<String, Object> paramMap) throws Exception {
        String sql =
                " INSERT INTO EXTRACT ( " +
                " 	CISN, MH_ID, APP_ID, CREATE_DT, RRNO, UPDATE_DT, USE_YN, USER_NM " +
                " ) " +
                " SELECT A.CISN, A.MH_ID, A.APP_ID, SYSTIMESTAMP CREATE_DT, B.RRNO, SYSTIMESTAMP UPDATE_DT, 'Y' USE_YN, B.USER_NM " +
                " FROM TFHR_AGRECMPSCRCT A " +
                " JOIN CONSENT B " +
                " 	ON A.APP_ID = B.APP_ID AND A.MH_ID = B.MH_ID " +
                " WHERE A.STPT_NO = :stptNo AND A.CMPS_JOB_NO = :cmpsJobNo " +
                "   AND A.CRCT_AFTR_AGRE_STCD = '10' " +
                "   AND A.CRCT_SCS_YN = 'N' " +
                " 	AND NOT EXISTS (SELECT 1 FROM EXTRACT WHERE APP_ID = A.APP_ID AND MH_ID = A.MH_ID AND CISN = A.CISN) ";
        return daoHelper.executeUpdate(sql, paramMap);
    }

    /**
     * 추출정보 없는 동의정보 업데이트
     * @param paramMap
     * @return
     * @throws Exception
     */
    public int clearConsent(Map<String, Object> paramMap) throws Exception {
        String sql =
                " UPDATE CONSENT A " +
                " SET CISN_LIST=NULL " +
                " WHERE NOT EXISTS (SELECT 1 FROM EXTRACT WHERE APP_ID=A.APP_ID AND MH_ID=A.MH_ID AND USE_YN='Y') " +
                " 	AND CISN_LIST IS NOT NULL AND DESTRUCT_YN='N' ";
        return daoHelper.executeUpdate(sql, paramMap);
    }

}
