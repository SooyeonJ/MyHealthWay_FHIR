package kr.co.iteyes.fhirmeta.repository;

import kr.co.iteyes.fhirmeta.entity.AgntInstlDmndHis;
import kr.co.iteyes.fhirmeta.entity.AgntInstlDmndHisId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AgntInstlDmndHisRepository extends JpaRepository<AgntInstlDmndHis, AgntInstlDmndHisId> {
    @Query("SELECT MAX(a.id.histSeq) FROM AgntInstlDmndHis a WHERE a.id.instlDmndNo = :instlDmndNo AND a.id.cisn = :cisn ")
    Integer getMaxHistSeq(int instlDmndNo, String cisn);

}