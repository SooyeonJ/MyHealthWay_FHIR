package kr.co.iteyes.fhirmeta.repository;

import kr.co.iteyes.fhirmeta.entity.Extract;
import kr.co.iteyes.fhirmeta.entity.ExtractId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ExtractRepository extends JpaRepository<Extract, ExtractId> {

    @Query("SELECT e FROM Extract e where e.extractId.cisn = :cisn")
    List<Extract> findByCisn(String cisn);

    @Transactional
    @Modifying
    @Query("DELETE FROM Extract e where e.extractId in :extractIds")
    void deleteAllByIds(List<ExtractId> extractIds);
}
