package kr.co.iteyes.fhirmeta.utils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class DaoHelper {
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * 쿼리 리스트 조회
     * @param sql
     * @param target
     * @param queryParamMap
     * @return
     * @param <T>
     * @param <R>
     * @throws Exception
     */
    public <T, R> List<R> getResultList(String sql, Class<T> target, Map<String, Object> queryParamMap) throws Exception {
        Query query = entityManager.createNativeQuery(sql)
                .unwrap(NativeQuery.class)
                .setResultTransformer(Transformers.aliasToBean(target));

        if (queryParamMap != null) {
            for (final Map.Entry<String, Object> entry : queryParamMap.entrySet()) {
                if (sql.contains(":" + entry.getKey())) {
                    query.setParameter(entry.getKey(), entry.getValue());
                }
            }
        }
        return query.getResultList();
    }

    /**
     * 쿼리 리스트 조회
     * @param sql
     * @param queryParamMap
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getResultMapList(String sql, Map<String, Object> queryParamMap) throws Exception {
        Query query = entityManager.createNativeQuery(sql)
                .unwrap(NativeQuery.class)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);

        if (queryParamMap != null) {
            for (final Map.Entry<String, Object> entry : queryParamMap.entrySet()) {
                if (sql.contains(":" + entry.getKey())) {
                    query.setParameter(entry.getKey(), entry.getValue());
                }
            }
        }
        return query.getResultList();
    }

    /**
     * 쿼리 단건 조회
     * @param sql
     * @param target
     * @param queryParamMap
     * @return
     * @param <T>
     * @throws Exception
     */
    public <T> T getSingleResult(String sql, Class<T> target, Map<String, Object> queryParamMap) throws Exception {
        Query query = entityManager.createNativeQuery(sql)
                .unwrap(NativeQuery.class)
                .setResultTransformer(Transformers.aliasToBean(target));

        if (queryParamMap != null) {
            for (final Map.Entry<String, Object> entry : queryParamMap.entrySet()) {
                if (sql.contains(":" + entry.getKey())) {
                    query.setParameter(entry.getKey(), entry.getValue());
                }
            }
        }
        return (T) query.getSingleResult();
    }

    /**
     * 쿼리 업데이트 실행
     * @param sql
     * @param queryParamMap
     * @return
     * @throws Exception
     */
    public int executeUpdate(String sql, Map<String, Object> queryParamMap) throws Exception {
        Query query = entityManager.createNativeQuery(sql);

        if (queryParamMap != null) {
            for (final Map.Entry<String, Object> entry : queryParamMap.entrySet()) {
                if (sql.contains(":" + entry.getKey())) {
                    query.setParameter(entry.getKey(), entry.getValue());
                }
            }
        }
        return query.executeUpdate();
    }
}
