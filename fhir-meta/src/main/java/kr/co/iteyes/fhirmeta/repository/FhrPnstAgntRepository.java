package kr.co.iteyes.fhirmeta.repository;

import javax.transaction.Transactional;
import kr.co.iteyes.fhirmeta.entity.FhrPnstAgnt;
import kr.co.iteyes.fhirmeta.entity.FhrPnstAgntId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FhrPnstAgntRepository extends JpaRepository<FhrPnstAgnt, FhrPnstAgntId> {

    @Transactional
    @Modifying
    @Query(value = "UPDATE FhrPnstAgnt a SET a.lastYn = 'N', a.mdfcnDt = :mdfcnDt WHERE a.id.cisn = :cisn AND a.id.agntKdcd = :agntKdcd ")
    void updateAllLastYn(@Param("mdfcnDt") String mdfcnDt, @Param("cisn") String cisn, @Param("agntKdcd") String agntKdcd);
}
