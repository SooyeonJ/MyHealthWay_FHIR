package kr.co.iteyes.fhirmeta.repository;

import kr.co.iteyes.fhirmeta.entity.PhrDbValLog;
import kr.co.iteyes.fhirmeta.entity.PhrDbValLogId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PhrDbValLogRepository extends JpaRepository<PhrDbValLog, PhrDbValLogId> {

    @Query("SELECT p FROM PhrDbValLog p WHERE p.phrDbValLogId.cisn = :cisn ORDER BY p.regYmd DESC")
    List<PhrDbValLog> findAllByCisn(String cisn);
}
