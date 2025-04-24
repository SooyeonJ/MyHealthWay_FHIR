package kr.co.iteyes.fhirmeta.service;

import kr.co.iteyes.fhirmeta.dto.StatisticsDto;
import kr.co.iteyes.fhirmeta.entity.Statistics;
import kr.co.iteyes.fhirmeta.entity.StatisticsId;
import kr.co.iteyes.fhirmeta.repository.StatisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class StatisticsService {

    private final StatisticsRepository statisticsRepository;

    public List<Statistics> getStatisticsList(List<String> cisnList, StatisticsDto.Request request) {
        List<StatisticsId> statisticsIds = new ArrayList<>();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String date = dateFormat.format(request.getBaseDate());
        for (String cisn : cisnList) {
            StatisticsId statisticsId = StatisticsId.builder()
                    .statisticsYmd(date)
                    .cisn(cisn)
                    .build();
            statisticsIds.add(statisticsId);
        }

        List<Statistics> statistics = statisticsRepository.findAllByStatisticsIdIn(statisticsIds);

        List<Statistics> results = new ArrayList<>();
        for (String cisn : cisnList) {
            boolean isStatistics = false;
            for (Statistics statistic : statistics) {
                if (cisn.equals(statistic.getStatisticsId().getCisn())) {
                    results.add(statistic);
                    isStatistics = true;
                    break;
                }
            }
            if(!isStatistics) {
                StatisticsId statisticsId = StatisticsId.builder()
                        .statisticsYmd(date)
                        .cisn(cisn)
                        .build();
                Statistics newStatistics = Statistics.builder()
                        .statisticsId(statisticsId)
                        .patient(0L)
                        .organization(0L)
                        .practitioner(0L)
                        .condition(0L)
                        .medicationRequest(0L)
                        .observationLaboratory(0L)
                        .observationExam(0L)
                        .imagingDiagnosticReport(0L)
                        .pathologyDiagnosticReport(0L)
                        .procedure(0L)
                        .allergyIntolerance(0L)
                        .documentReference(0L)
                        .practitionerRole(0L)
                        .encounter(0L)
                        .imagingstudy(0L)
                        .media(0L)
                        .endpoint(0L)
                        .patientTotalCount(0L)
                        .organizationTotalCount(0L)
                        .practitionerTotalCount(0L)
                        .conditionTotalCount(0L)
                        .medicationRequestTotalCount(0L)
                        .observationLaboratoryTotalCount(0L)
                        .observationExamTotalCount(0L)
                        .imagingDiagnosticReportTotalCount(0L)
                        .pathologyDiagnosticReportTotalCount(0L)
                        .procedureTotalCount(0L)
                        .allergyIntoleranceTotalCount(0L)
                        .documentReferenceTotalCount(0L)
                        .practitionerRoleTotalCount(0L)
                        .encounterTotalCount(0L)
                        .imagingstudyTotalCount(0L)
                        .mediaTotalCount(0L)
                        .endpointTotalCount(0L)
                        .build();
                results.add(newStatistics);
            }
        }
        return results;
    }
}
