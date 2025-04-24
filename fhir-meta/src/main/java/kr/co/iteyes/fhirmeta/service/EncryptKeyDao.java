package kr.co.iteyes.fhirmeta.service;

import kr.co.iteyes.fhirmeta.dto.OrganizationSeedStatusDto;

import kr.co.iteyes.fhirmeta.dto.SeedStatusDto;
import kr.co.iteyes.fhirmeta.utils.DaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class EncryptKeyDao {

    @Autowired
    DaoHelper daoHelper;

    /**
     * 의료기관 암호화 키 발급 상태 조회
     * @param paramMap
     * @return
     * @throws Exception
     */
    public List<OrganizationSeedStatusDto.Response> getOrganizationSeedStatusList(Map<String, Object> paramMap) throws Exception {
        String sql =
                " SELECT TO_CHAR(B.ISSU_DMND_NO) AS \"issueDemandNo\", " +
                " 	A.CISN AS \"careInstitutionSign\", " +
                " 	NVL(B.ISSU_RSN_DVCD, '10') AS \"issueDivisionCode\", " +
                " 	'10' AS \"status\", " +
                " 	TO_CHAR(B.CREATE_DT,'YYYY-MM-DD HH24:MI:SS') AS \"issueDateTime\",  " +
                " 	TO_CHAR(B.CREATE_DT,'YYYY-MM-DD HH24:MI:SS') AS \"validBeginningDateTime\",  " +
                " 	TO_CHAR(B.VALID_DT,'YYYY-MM-DD HH24:MI:SS') AS \"validEndDateTime\", " +
                " 	(CASE WHEN B.VALID_DT < SYSDATE THEN TO_CHAR(B.VALID_DT,'YYYY-MM-DD HH24:MI:SS') END) \"discardDateTime\" " +
                " FROM ENCRYPT_KEY A " +
                " JOIN SEED_CTR B " +
                " 	ON A.CISN=B.CISN AND A.SEED_CTR_ID=B.ID AND A.TYPE='SEED'  " +
                " WHERE A.CISN !='00000000' " +
                "   AND B.ISSU_DMND_NO IS NOT NULL " +
                "   AND A.UPDATE_DT BETWEEN TO_DATE(:baseDate, 'YYYYMMDD') AND TO_DATE(:baseDate,'YYYYMMDD') + 0.99999 ";

        return daoHelper.getResultList(sql, OrganizationSeedStatusDto.Response.class, paramMap);
    }

    /**
     * 전체 제공기관 폐기상태 조회
     * @return
     * @throws Exception
     */
    public SeedStatusDto.Response getSeedStatusInfo(Map<String, Object> paramMap) throws Exception {
        String sql =
                " SELECT TO_CHAR(BB.ISSU_DMND_NO) AS \"issueDemandNo\", " +
                " 	(SELECT (CASE WHEN COUNT(*) = 0 THEN 'Y' ELSE 'N' END) " +
                " 	FROM ENCRYPT_KEY A " +
                " 	JOIN SEED_CTR B " +
                " 		ON A.CISN=B.CISN AND A.SEED_CTR_ID=B.ID AND A.TYPE='SEED' " +
                " 	WHERE A.TYPE='SEED' AND A.CISN != :platform_cisn AND A.VALID_DT > SYSDATE" +
                "       AND A.UPDATE_DT BETWEEN TO_DATE(:baseDate, 'YYYYMMDD') AND TO_DATE(:baseDate, 'YYYYMMDD') + 0.99999" +
                "   ) AS \"allEncryptionKeyDiscardYN\" " +
                " FROM ENCRYPT_KEY AA " +
                " JOIN SEED_CTR BB " +
                " 	ON AA.CISN=BB.CISN AND AA.SEED_CTR_ID=BB.ID " +
                " WHERE AA.CISN = :platform_cisn AND AA.TYPE='SEED' ";

        return daoHelper.getSingleResult(sql, SeedStatusDto.Response.class, paramMap);
    }

    /**
     * 제공기관 암호화 키 폐기 상태 조회
     * @param paramMap
     * @return
     * @throws Exception
     */
    public List<SeedStatusDto.CareInstitutionSignResult> getOrganizationDiscardList(Map<String, Object> paramMap) throws Exception {
        String sql =
                " SELECT DISTINCT B.CISN AS \"careInstitutionSign\", " +
                "   C.PNST_NO AS \"provideInstitutionNo\", " +
                " 	(CASE WHEN A.VALID_DT < SYSDATE THEN TO_CHAR(A.VALID_DT,'YYYY-MM-DD') END) AS \"discardDate\" " +
                " FROM ENCRYPT_KEY A  " +
                " JOIN SEED_CTR B " +
                " 	ON A.CISN=B.CISN AND A.SEED_CTR_ID=B.ID AND A.TYPE='SEED' " +
                " JOIN TMST_MDST C " +
                "   ON A.CISN=C.CISN " +
                " WHERE A.CISN != :platform_cisn " +
                " 	AND B.ISSU_DMND_NO IS NOT NULL " +
                " 	AND A.VALID_DT < SYSDATE " +
                "   AND B.ISSU_DMND_NO = :issuDmndNo " +
                "   AND C.PNST_NO IS NOT NULL " +
                " 	AND A.UPDATE_DT BETWEEN TO_DATE(:baseDate, 'YYYYMMDD') AND TO_DATE(:baseDate, 'YYYYMMDD') + 0.99999 ";

        return daoHelper.getResultList(sql, SeedStatusDto.CareInstitutionSignResult.class, paramMap);
    }

    /**
     * SEED 키 만료
     * @param paramMap
     * @return
     * @throws Exception
     */
    public int expireSeedKey(Map<String, Object> paramMap) throws Exception {
        String sql =
                " UPDATE SEED_CTR A " +
                " SET VALID_DT = TRUNC(SYSDATE), " +
                "   ISSU_DMND_NO = :issueDemandNo, " +
                "   APRV_YMD = :approvalDate, " +
                "   ISSU_RSN_DVCD = '30' " +
                " WHERE CISN = :cisn AND VALID_DT > SYSDATE " ;

        return daoHelper.executeUpdate(sql, paramMap);
    }

    /**
     * 암호화(SEED) 키 비활성화
     * @param paramMap
     * @return
     * @throws Exception
     */
    public int deActivateSeedEncryptKey(Map<String, Object> paramMap) throws Exception {
        String sql =
                " UPDATE ENCRYPT_KEY " +
                " SET VALID_DT = TRUNC(SYSDATE), " +
                "   UPDATE_DT = SYSDATE " +
                " WHERE CISN = :cisn AND TYPE='SEED' AND VALID_DT > SYSDATE ";

        return daoHelper.executeUpdate(sql, paramMap);
    }

    /**
     * 전체 제공기관 SEED 키 만료 처리
     * @param paramMap
     * @return
     * @throws Exception
     */
    public int expireAllOrganizationSeedKey(Map<String, Object> paramMap) throws Exception {
        String sql =
                " UPDATE SEED_CTR A " +
                " SET VALID_DT = TRUNC(SYSDATE), " +
                "   ISSU_DMND_NO = :issueDemandNo, " +
                "   APRV_YMD = :approvalDate, " +
                "   ISSU_RSN_DVCD = '30' " +
                " WHERE CISN != :platform_cisn AND VALID_DT > SYSDATE ";

        return daoHelper.executeUpdate(sql, paramMap);
    }

    /**
     * 전체 제공기관 암호화(SEED) 키 비활성화
     * @param paramMap
     * @return
     * @throws Exception
     */
    public int deActivateAllOrganizationSeedEncryptKey(Map<String, Object> paramMap) throws Exception {
        String sql =
                " UPDATE ENCRYPT_KEY " +
                " SET VALID_DT = TRUNC(SYSDATE), " +
                "   UPDATE_DT = SYSDATE " +
                " WHERE CISN != :platform_cisn AND TYPE='SEED' AND VALID_DT > SYSDATE ";

        return daoHelper.executeUpdate(sql, paramMap);
    }

}
