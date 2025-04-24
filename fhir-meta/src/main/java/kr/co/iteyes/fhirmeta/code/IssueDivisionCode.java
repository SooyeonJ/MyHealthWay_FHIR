package kr.co.iteyes.fhirmeta.code;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public enum IssueDivisionCode {

	ISSU("10", "발급"),
	UPDT("20", "갱신"),
	DSCD("30", "폐기"),
  ;
	
	private String code;
	private String name;
	
	IssueDivisionCode(String code, String name) {
		this.code = code;
		this.name = name;
	}
}
