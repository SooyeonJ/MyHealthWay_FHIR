package kr.co.iteyes.fhirmeta.repository;

import kr.co.iteyes.fhirmeta.entity.EncryptKey;
import kr.co.iteyes.fhirmeta.entity.EncryptKeyId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface EncryptKeyRepository extends JpaRepository<EncryptKey, EncryptKeyId> {

    Optional<EncryptKey> findByEncryptKeyIdAndValidDtAfter(EncryptKeyId encryptKeyId, Timestamp timestamp);

    @Query("SELECT e FROM EncryptKey e where e.encryptKeyId.type = :type")
    List<EncryptKey> findAllByType(String type);
}
