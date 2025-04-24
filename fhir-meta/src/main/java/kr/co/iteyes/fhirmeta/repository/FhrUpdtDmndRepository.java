package kr.co.iteyes.fhirmeta.repository;

import kr.co.iteyes.fhirmeta.entity.FhrRscldgId;
import kr.co.iteyes.fhirmeta.entity.FhrUpdtDmnd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FhrUpdtDmndRepository extends JpaRepository<FhrUpdtDmnd, FhrRscldgId> {
    @Query("SELECT p FROM FhrUpdtDmnd p WHERE p.cisn = :cisn AND p.updateStcd = '20' ORDER BY p.regDt DESC")
    List<FhrUpdtDmnd> findAllByCisn(String cisn);

    @Query("SELECT p FROM FhrUpdtDmnd p WHERE p.fhrUpdtDmndId.updateDmndNo = :updateDmndNo")
    FhrUpdtDmnd findAllByUpdateDmndNo(Long updateDmndNo);

    @Query("SELECT p FROM FhrUpdtDmnd p WHERE p.updateDt BETWEEN :startDate AND :endDate")
    List<FhrUpdtDmnd> findAllByUpdateDt(String startDate, String endDate);
}
