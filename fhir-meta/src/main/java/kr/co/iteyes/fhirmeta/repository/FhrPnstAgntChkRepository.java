package kr.co.iteyes.fhirmeta.repository;

import kr.co.iteyes.fhirmeta.entity.FhrPnstAgnt;
import kr.co.iteyes.fhirmeta.entity.FhrPnstAgntChk;
import kr.co.iteyes.fhirmeta.entity.FhrPnstAgntChkId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FhrPnstAgntChkRepository extends JpaRepository<FhrPnstAgntChk, FhrPnstAgntChkId> {
}
