package kr.co.iteyes.fhirmeta.service;

import kr.co.iteyes.fhirmeta.dto.FhrValidErrsDto;
import kr.co.iteyes.fhirmeta.dto.FhrValidStatsDto;
import kr.co.iteyes.fhirmeta.entity.FhrRscldg;
import kr.co.iteyes.fhirmeta.entity.FhrDtaerr;
import kr.co.iteyes.fhirmeta.repository.FhrDtaerrRepository;
import kr.co.iteyes.fhirmeta.repository.FhrRscldgRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

@RequiredArgsConstructor
@Service
public class FhirValidateService {

    private final FhrDtaerrRepository fhrDtaerrRepository;
    private final FhrRscldgRepository fhrRscldgRepository;

    public List<FhrDtaerr> getFhrValidErrs(FhrValidErrsDto.Request request) {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String date = dateFormat.format(request.getBaseDate());

        return fhrDtaerrRepository.findAllByRegDt(date);
    }

    public List<FhrRscldg> getFhrValidStats(FhrValidStatsDto.Request request) {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String date = dateFormat.format(request.getBaseDate());

        return fhrRscldgRepository.findAllByRegDt(date);
    }
}
