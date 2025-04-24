package kr.co.iteyes.fhirmeta.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class ConsentRepo {

    @Id
    private String mhId;

    @Column(length = 1)
    private String destructYn;

    @Column(columnDefinition = "TIMESTAMP")
    private Timestamp agreeDt;

    @Column(columnDefinition = "TIMESTAMP")
    private Timestamp disagreeDt;

    @Column(columnDefinition = "TIMESTAMP")
    private Timestamp mhSendDt;

    @Column(columnDefinition = "TIMESTAMP")
    private Timestamp createDt;

    @Column(columnDefinition = "TIMESTAMP")
    private Timestamp updateDt;

    private String serverDomainNo;

    public void setDisagreeDt(Timestamp disagreeDt) {
        this.disagreeDt = disagreeDt;
    }

    public void setMhSendDt(Timestamp mhSendDt) {
        this.mhSendDt = mhSendDt;
    }

    public void setUpdateDt(Timestamp updateDt) {
        this.updateDt = updateDt;
    }

    public void setCreateDt(Timestamp createDt) {
        this.createDt = createDt;
    }

    public void setDestructYn(String destructYn) { this.destructYn = destructYn; }
}
