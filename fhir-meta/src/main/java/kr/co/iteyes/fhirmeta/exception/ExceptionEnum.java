package kr.co.iteyes.fhirmeta.exception;

import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
public enum ExceptionEnum {
    INVALID_KEY_EXCEPTION(HttpStatus.BAD_REQUEST, "유효한 암호화 키가 아닙니다."),
    RUNTIME_EXCEPTION(HttpStatus.BAD_REQUEST),
    ACCESS_TOKEN_NULL_EXCEPTION(HttpStatus.UNAUTHORIZED, "인증토큰은 NULL 이거나 공백이면 안됩니다."),
    ACCESS_DENIED_EXCEPTION(HttpStatus.UNAUTHORIZED, "유효한 토큰이 아닙니다."),
    ACCESS_TOKEN_CREATE_EXCEPTION(HttpStatus.UNAUTHORIZED, "인증토큰 생성에 실패하였습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),
    INTERNAL_AUTH_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "인증서버가 올바르지 않습니다."),
    INVALID_CONSENT_EXCEPTION(HttpStatus.BAD_REQUEST, "유효한 동의정보가 아닙니다."),
    INVALID_ISSUE_DIVISION_CODE_EXCEPTION(HttpStatus.BAD_REQUEST, "유효한 암호화키 발급 구분 코드가 아닙니다."),
    INVALID_SERVER_DOMAIN_NO_EXCEPTION(HttpStatus.BAD_REQUEST, "유효한 서버 도메인 코드가 아닙니다."),
    EMPTY_VALUE_EXCEPTION(HttpStatus.BAD_REQUEST, "필수 요청 항목(%s)의 값이 누락되었습니다."),
    INVALID_PARAMETER_EXCEPTION(HttpStatus.BAD_REQUEST, "%s: %s"),
    NO_DATA_EXCEPTION(HttpStatus.BAD_REQUEST, "결과 데이터가 없습니다."),
    INTERNAL_PARAM_EXCEPTION(HttpStatus.BAD_REQUEST, "요청 파라미터가 없습니다."),
    RESOURCE_DELETE_EXCEPTION(HttpStatus.BAD_REQUEST, "리소스 삭제에 실패하였습니다."),
    EXPUNGE_EXCEPTION(HttpStatus.BAD_REQUEST, "데이터 삭제에 실패하였습니다.");


    private final HttpStatus status;
    private String message;

    ExceptionEnum(HttpStatus status) {
        this.status = status;
    }

    ExceptionEnum(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
