package kr.co.iteyes.fhirmeta.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "TCOM_AGNT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class ComAgnt {
    @EmbeddedId
    private ComAgntId id;
    private String agntNm;
    private String agntVerNm;
    private String mainChgCn;
    private String regStcd;
    @Column(length = 14, updatable = false)
    private String regDt;
    @Column(length = 14)
    private String mdfcnDt;
}
