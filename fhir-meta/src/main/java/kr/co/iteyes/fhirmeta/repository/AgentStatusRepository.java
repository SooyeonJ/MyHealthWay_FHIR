package kr.co.iteyes.fhirmeta.repository;

import kr.co.iteyes.fhirmeta.entity.AgentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AgentStatusRepository extends JpaRepository<AgentStatus, String> {
    Optional<AgentStatus> findByCisn(String cisn);
}
