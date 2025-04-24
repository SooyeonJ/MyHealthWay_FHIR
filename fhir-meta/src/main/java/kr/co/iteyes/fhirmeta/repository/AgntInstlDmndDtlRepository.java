package kr.co.iteyes.fhirmeta.repository;

import javax.transaction.Transactional;
import kr.co.iteyes.fhirmeta.entity.AgntInstlDmndDtl;
import kr.co.iteyes.fhirmeta.entity.AgntInstlDmndDtlId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AgntInstlDmndDtlRepository extends JpaRepository<AgntInstlDmndDtl, AgntInstlDmndDtlId> {
    @Transactional
    @Modifying
    @Query(value = "UPDATE AgntInstlDmndDtl a SET a.instlYn = :instlYn, a.agntInstlStcd = :agntInstlStcd, a.instlYmd = :instlYmd WHERE a.id = :id ")
    void updateInstlResult(@Param("instlYn") String instlYn, @Param("agntInstlStcd") String agntInstlStcd, @Param("instlYmd") String instlYmd, @Param("id") AgntInstlDmndDtlId id);
}
