package kr.co.iteyes.oauth.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ResponseStatus(HttpStatus.FORBIDDEN)
public class AuthFailedException extends RuntimeException {


    public AuthFailedException(String message) {
        super(message);
        log.error(message);
    }
}
