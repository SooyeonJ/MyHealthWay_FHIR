package kr.co.iteyes.fhirmeta.repository;

import kr.co.iteyes.fhirmeta.entity.ComAgnt;
import kr.co.iteyes.fhirmeta.entity.ComAgntId;
import kr.co.iteyes.fhirmeta.entity.ComEmr;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComAgntRepository extends JpaRepository<ComAgnt, ComAgntId> {
}
