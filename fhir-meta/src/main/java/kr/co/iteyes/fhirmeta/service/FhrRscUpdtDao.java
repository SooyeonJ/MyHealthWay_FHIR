package kr.co.iteyes.fhirmeta.service;

import kr.co.iteyes.fhirmeta.dto.FhrRscUpdtDto;
import kr.co.iteyes.fhirmeta.utils.DaoHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class FhrRscUpdtDao {
    DaoHelper daoHelper;

    // Encounter RES_ID 조회
    public FhrRscUpdtDto.Resource getEncounterInfo(Map<String, Object> paramMap, String fhirDbSchema) throws Exception {
        String sql =
                "SELECT RES_ID AS \"resourceId\", RES_TYPE AS \"resourceType\" FROM " + fhirDbSchema +".HFJ_SPIDX_TOKEN " +
                "	WHERE SP_NAME = 'identifier' " +
                "	  AND RES_TYPE = 'Encounter' " +
                "      AND SP_SYSTEM = :tokenHashSpSystem " +
                "	  AND SP_VALUE = PETRA.PLS_ENCRYPT_B64_ID(:tokenHashSpValue, 101)";
        return daoHelper.getSingleResult(sql, FhrRscUpdtDto.Resource.class, paramMap);
    }

    // 하위 리소스 RES_ID 조회
    public List<FhrRscUpdtDto.Resource> getResourceInfo(Map<String, Object> paramMap, String fhirDbSchema) throws Exception {
        String sql =
                " SELECT RES_ID AS \"resourceId\", RES_TYPE AS \"resourceType\" FROM " + fhirDbSchema + ".HFJ_RESOURCE " +
                        " WHERE RES_ID in (SELECT A.SRC_RESOURCE_ID as RESOURCE_ID " +
                        " 	FROM " + fhirDbSchema + ".HFJ_RES_LINK A " +
                        " 			 LEFT JOIN " + fhirDbSchema + ".HFJ_RESOURCE C " +
                        " 					   ON A.TARGET_RESOURCE_ID = C.RES_ID " +
                        " 	  AND C.RES_DELETED_AT IS NOT NULL " +
                        " 			 LEFT JOIN " + fhirDbSchema + ".HFJ_SPIDX_TOKEN B " +
                        " 					   ON A.TARGET_RESOURCE_ID = B.RES_ID " +
                        " 	WHERE B.SP_NAME = 'identifier' " +
                        " 	  AND B.RES_TYPE = 'Encounter' " +
                        " 	  AND B.SP_SYSTEM = :tokenHashSpSystem" +
                        " 	  AND B.SP_VALUE = PETRA.PLS_ENCRYPT_B64_ID(:tokenHashSpValue, 101)" +
                        " ) ";
        return daoHelper.getResultList(sql, FhrRscUpdtDto.Resource.class, paramMap);
    }
}
