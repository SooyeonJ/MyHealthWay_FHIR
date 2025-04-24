package kr.co.iteyes.fhirmeta.repository;

import kr.co.iteyes.fhirmeta.entity.SeedCtr;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeedCtrRepository extends JpaRepository<SeedCtr, Long> {
    List<SeedCtr> findByCisnOrderByCreateDtAsc(String cisn);
}
