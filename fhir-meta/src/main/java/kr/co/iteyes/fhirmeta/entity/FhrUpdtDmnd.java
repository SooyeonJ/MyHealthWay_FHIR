package kr.co.iteyes.fhirmeta.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import kr.co.iteyes.fhirmeta.dto.FhrUpdtDmndDto;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Table(name = "TFHR_UPDTDMND")
public class FhrUpdtDmnd {

    @EmbeddedId
    private FhrUpdtDmndId fhrUpdtDmndId;
    private String cisn;
    private String mhId;
    private String updateDmndYmd;
    private String updateDmndBgngYmd;
    private String updateDmndEndYmd;
    private String appId;
    private String appName;
    private String phrUpdateDt;
    private String phrUpdateYn;
    private String fhirUpdateDt;
    private String fhirUpdateYn;
    private String updateStcd;
    private String updateDt;
    private String regDt;

    public void update(FhrUpdtDmndDto.UpdateRequest updateRequest, String updateDt) {
        this.phrUpdateDt = updateRequest.getPhrUpdateDateTime().replaceAll("[^0-9]", "");
        this.phrUpdateYn = updateRequest.getPhrUpdateFlag();
        this.fhirUpdateDt = updateRequest.getFhirUpdateDateTime().replaceAll("[^0-9]", "");
        this.fhirUpdateYn = updateRequest.getFhirUpdateFlag();
        this.updateStcd = updateRequest.getUpdateStcd();
        this.updateDt = updateDt;
    }
}
