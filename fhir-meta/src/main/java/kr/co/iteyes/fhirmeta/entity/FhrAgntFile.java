package kr.co.iteyes.fhirmeta.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.*;

@Entity
@Table(name = "TFHR_AGNTFILE")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class FhrAgntFile {
    @Id
    private int instlFileNo;
    private String agntKdcd;
    private int agntVerNo;
    private String dbmsKdcd;
    private String fileChksm;
    private String regStcd;
    @Column(length = 14)
    private String regDt;
}
