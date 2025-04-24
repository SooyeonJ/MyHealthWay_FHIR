package kr.co.iteyes.fhirmeta.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "TFHR_AGNTINSTLDMND_HIS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class AgntInstlDmndHis {
    @EmbeddedId
    private AgntInstlDmndHisId id;
    String agntInstlStcd;
    @Column(length = 14)
    String stsChgDt;

}
