package kr.co.iteyes.fhirmeta.service;

import kr.co.iteyes.fhirmeta.dto.IndexDto;
import kr.co.iteyes.fhirmeta.entity.Index;
import kr.co.iteyes.fhirmeta.repository.IndexRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class IndexService {

    private final IndexRepository indexRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public void createIndex(IndexDto.RequestForInsert request) {
        if(StringUtils.isNotBlank(request.getPatientId())) {

            Date date = Calendar.getInstance().getTime();
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            String strDate = dateFormat.format(date);

            Index index = Index.builder()
                    .mhId(request.getUserId())
                    .cisn(request.getProvideInstitutionCode())
                    .patientId(request.getPatientId())
                    .createYmd(strDate)
                    .build();

            indexRepository.save(index);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateIndex(IndexDto.RequestForUpdate request, String cisn) {
        Optional<Index> indexOptional = indexRepository.findByPatientIdAndCisnAndMhId(request.getPatientId(), cisn, request.getUserId());
        indexOptional.ifPresent(index -> {
            if(StringUtils.isBlank(index.getFhirPatientResourceId()) && StringUtils.isBlank(index.getFhirOrganizationResourceId())) index.updateAll(request);
            else index.update(request);
        });
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateIndex(IndexDto.RequestForUpdatePost request, String cisn) {
        Optional<Index> indexOptional = indexRepository.findByPatientIdAndCisnAndMhId(request.getPatientId(), cisn, request.getUserId());
        indexOptional.ifPresent(index -> {
            if(StringUtils.isBlank(index.getFhirPatientResourceId()) && StringUtils.isBlank(index.getFhirOrganizationResourceId())) index.updateAll(request);
            else index.update(request);
        });
    }

    public List<Index> getIndexListByKey(String mhId, String cisn) {
        return indexRepository.findByMhIdAndCisn(mhId, cisn);
    }
}
