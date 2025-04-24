package kr.co.iteyes.fhirmeta.repository;

import kr.co.iteyes.fhirmeta.entity.Consent;
import kr.co.iteyes.fhirmeta.entity.ConsentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;

public interface ConsentRepository extends JpaRepository<Consent, ConsentId> {

    @Transactional
    @Modifying
    @Query("UPDATE Consent c set c.destructYn = 'Y', c.updateDt = :updateDt where c.consentId.mhId = :mhId")
    void updateConsentDestruct(@Param("mhId") String mhId, @Param("updateDt") Timestamp updateDt);

    @Transactional
    @Modifying
    @Query("UPDATE Consent c set c.cisnList = :cisnList, c.destructYn = :destructYn, c.updateDt = :updateDt, c.destructDt = :destructDt where c.consentId.appId = :appId AND c.consentId.mhId = :mhId ")
    void updateCisnList(@Param("appId") String appId, @Param("mhId") String mhId, @Param("cisnList") String cisnList,
                        @Param("destructYn") String destructYn,  @Param("updateDt") Timestamp updateDt, @Param("destructDt") LocalDate destructDt);
}
