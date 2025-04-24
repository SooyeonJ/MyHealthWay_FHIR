package kr.co.iteyes.fhirmeta.repository;

import kr.co.iteyes.fhirmeta.entity.Index;
import kr.co.iteyes.fhirmeta.entity.IndexId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IndexRepository extends JpaRepository<Index, IndexId> {
    List<Index> findByMhIdAndCisnIn(String mhId, List<String> cisnList);
    List<Index> findByMhId(String mhId);
    Optional<Index> findByPatientIdAndCisnAndMhId(String patientId, String cisn, String mhId);
    List<Index> findByMhIdAndCisn(String mhId, String cisn);
}
