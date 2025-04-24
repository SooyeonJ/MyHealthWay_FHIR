package kr.co.iteyes.fhirmeta.code;

public enum FhrUpdtStatusCode {
    INITIAL("20"),
    TRANSFERRED("30"),
    SUCCESS("41"),
    FAILURE("42"),
    FAIL_FHIR_UPDATE("FHIR 업데이트 실패"),
    NULL_FHIR_UPDATE("FHIR 업데이트 결과값 없음"),
    FAIL_PHR_UPDATE("PHR 업데이트 실패"),
    NULL_PHR_UPDATE("PHR 업데이트 결과값 없음");


    private String fhrUpdtDmndStcd;

    FhrUpdtStatusCode(String fhrUpdtDmndStcd) {
        this.fhrUpdtDmndStcd = fhrUpdtDmndStcd;
    }

    public String getFhrUpdtDmndStcd() {
        return fhrUpdtDmndStcd;
    }

}
