package kr.co.iteyes.fhirmeta.code;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public enum AgreementCode {

	NOT_AGREEMENT("03", "미동의"),
	AGREEMENT("02", "동의")
  ;
	
	private String code;
	private String name;
	
	AgreementCode(String code, String name) {
		this.code = code;
		this.name = name;
	}

	public static String getCode(String useYn) {
		if(StringUtils.isBlank(useYn) || useYn.equals("N")) return NOT_AGREEMENT.getCode();
		return AGREEMENT.getCode();
	}
}
