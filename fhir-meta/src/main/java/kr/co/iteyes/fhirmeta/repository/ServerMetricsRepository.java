package kr.co.iteyes.fhirmeta.repository;

import kr.co.iteyes.fhirmeta.entity.ComSrvroprchkhh;
import kr.co.iteyes.fhirmeta.entity.ComSrvroprchkhhId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ServerMetricsRepository extends JpaRepository<ComSrvroprchkhh, ComSrvroprchkhhId> {
    @Query("SELECT r FROM ComSrvroprchkhh r WHERE r.regDt BETWEEN :startDate AND :endDate")
    List<ComSrvroprchkhh> findAllByRegDt(String startDate, String endDate);
}
