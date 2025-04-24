package kr.co.iteyes.fhirmeta.dto;

import kr.co.iteyes.fhirmeta.entity.ComSrvroprchkhh;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class ServerMetricsDto {
    @Getter
    public static class CreateResource {
        private Long stptNo;
        private String chckDt;
        private String srvrId;
        private Double cpuUsgrt;
        private Double mmryUsgrt;
        private Double diskUsgrt;
        private String regDt;
    }

    @Getter
    public static class Request {
        private String baseDateTime;
    }

    @Getter
    @Builder
    public static class Response {
        private String stptNo;
        private String chckDt;
        private String srvrId;
        private Double cpuUsgrt;
        private Double mmryUsgrt;
        private Double diskUsgrt;
        private String regDt;

        public static List<Response> fromList(List<ComSrvroprchkhh> comSrvroprchkhhs) {
            List<Response> result = new ArrayList<>();

            comSrvroprchkhhs.forEach(comSrvroprchkhh -> result.add(
                    Response.from(comSrvroprchkhh)
            ));
            return result;
        }

        private static Response from(ComSrvroprchkhh comSrvroprchkhh) {
            return Response.builder()
                    .stptNo(String.valueOf(comSrvroprchkhh.getComSrvroprchkhhId().getStptNo()))
                    .chckDt(comSrvroprchkhh.getComSrvroprchkhhId().getChckDt())
                    .srvrId(comSrvroprchkhh.getComSrvroprchkhhId().getSrvrId())
                    .cpuUsgrt(comSrvroprchkhh.getCpuUsgrt())
                    .mmryUsgrt(comSrvroprchkhh.getMmryUsgrt())
                    .diskUsgrt(comSrvroprchkhh.getDiskUsgrt())
                    .regDt(comSrvroprchkhh.getRegDt())
                    .build();
        }
    }
}
