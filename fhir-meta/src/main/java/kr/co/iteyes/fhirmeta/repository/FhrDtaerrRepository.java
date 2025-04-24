package kr.co.iteyes.fhirmeta.repository;

import kr.co.iteyes.fhirmeta.entity.FhrDtaerr;
import kr.co.iteyes.fhirmeta.entity.FhrDtaerrId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FhrDtaerrRepository extends JpaRepository<FhrDtaerr, FhrDtaerrId> {
    @Query("SELECT e FROM FhrDtaerr e WHERE e.regDt like :date%")
    List<FhrDtaerr> findAllByRegDt(String date);
}
