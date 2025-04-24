package kr.co.iteyes.fhirmeta.repository;

import kr.co.iteyes.fhirmeta.entity.Statistics;
import kr.co.iteyes.fhirmeta.entity.StatisticsId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StatisticsRepository extends JpaRepository<Statistics, StatisticsId> {

    List<Statistics> findAllByStatisticsIdIn(List<StatisticsId> statisticsIds);

}
