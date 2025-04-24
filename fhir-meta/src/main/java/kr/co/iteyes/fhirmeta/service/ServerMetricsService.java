package kr.co.iteyes.fhirmeta.service;

import kr.co.iteyes.fhirmeta.dto.ServerMetricsDto;
import kr.co.iteyes.fhirmeta.entity.ComSrvroprchkhh;
import kr.co.iteyes.fhirmeta.entity.ComSrvroprchkhhId;
import kr.co.iteyes.fhirmeta.repository.ServerMetricsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ServerMetricsService {
    private final ServerMetricsRepository serverMetricsRepository;

    public List<ComSrvroprchkhh> getServerMetrics(ServerMetricsDto.Request request) {
        String date = request.getBaseDateTime().replaceAll("[^0-9]", "").substring(0, 10);

        String startDate = date + "0000";
        String endDate = date + "5959";

        return serverMetricsRepository.findAllByRegDt(startDate, endDate);
    }

    public void addServerMetrics(ServerMetricsDto.CreateResource createResource) throws Exception {

        ComSrvroprchkhhId comSrvroprchkhhId = ComSrvroprchkhhId.builder()
                .stptNo(createResource.getStptNo())
                .chckDt(createResource.getChckDt())
                .srvrId(createResource.getSrvrId())
                .build();

        ComSrvroprchkhh comSrvroprchkhh = ComSrvroprchkhh.builder()
                .comSrvroprchkhhId(comSrvroprchkhhId)
                .cpuUsgrt(createResource.getCpuUsgrt())
                .mmryUsgrt(createResource.getMmryUsgrt())
                .diskUsgrt(createResource.getDiskUsgrt())
                .build();

        serverMetricsRepository.save(comSrvroprchkhh);
    }
}