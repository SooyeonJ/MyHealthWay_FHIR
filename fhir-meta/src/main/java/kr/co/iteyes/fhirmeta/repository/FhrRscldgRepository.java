package kr.co.iteyes.fhirmeta.repository;

import kr.co.iteyes.fhirmeta.entity.FhrRscldg;
import kr.co.iteyes.fhirmeta.entity.FhrRscldgId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FhrRscldgRepository extends JpaRepository<FhrRscldg, FhrRscldgId> {
    @Query("SELECT r FROM FhrRscldg r WHERE r.regDt like :date%")
    List<FhrRscldg> findAllByRegDt(String date);
}
