package kr.co.iteyes.fhirmeta.exception;

import javax.servlet.http.HttpServletRequest;
import kr.co.iteyes.fhirmeta.dto.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.security.InvalidKeyException;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({CustomException.class})
    public ResponseEntity<ApiException> exceptionHandler(HttpServletRequest request, final CustomException e) {
        String message = e.getArgs() != null && e.getArgs().length > 0 ? String.format(e.getMessage(), e.getArgs()) : e.getError().getMessage();
        writeCustomExceptionLog(e);
        return ResponseEntity
                .status(e.getError().getStatus())
                .body(ApiException.builder()
                        .result("FAIL")
                        .message(message)
                        .build());
    }

    /**
     * CustomException 로깅
     * @param e the exception
     */
    private void writeCustomExceptionLog(CustomException e) {
        String message = e.getArgs() != null && e.getArgs().length > 0 ? String.format(e.getMessage(), e.getArgs()) : e.getError().getMessage();
        if (e.getStackTrace() != null && e.getStackTrace().length > 0) {
            StringBuilder sb = new StringBuilder();
            StackTraceElement stackTraceElement = null;
            String className = null, methodName = null, fileName = null, lineNumber = null;
            for (int i = 0; i < 2; i++) {
                if (e.getStackTrace().length > i) {
                    stackTraceElement = e.getStackTrace()[i];
                    className = stackTraceElement.getClassName();
                    methodName = stackTraceElement.getMethodName();
                    fileName = stackTraceElement.getFileName() == null ? "" : stackTraceElement.getFileName();
                    lineNumber = String.valueOf(stackTraceElement.getLineNumber());
                    sb.append("at ").append(className).append(".").append(methodName).append("(").append(fileName).append(":").append(lineNumber).append(")");
                }
            }
            log.info("FAIL :: {} {}", message, sb);
        }
    }

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<ApiException> exceptionHandler(HttpServletRequest request, final RuntimeException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(ExceptionEnum.RUNTIME_EXCEPTION.getStatus())
                .body(ApiException.builder()
                        .message(e.getMessage())
                        .build());
    }

    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<ApiException> exceptionHandler(HttpServletRequest request, final AccessDeniedException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(ExceptionEnum.ACCESS_DENIED_EXCEPTION.getStatus())
                .body(ApiException.builder()
                        .message(e.getMessage())
                        .build());
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ApiException> exceptionHandler(HttpServletRequest request, final Exception e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(ExceptionEnum.INTERNAL_SERVER_ERROR.getStatus())
                .body(ApiException.builder()
                        .message(e.getMessage())
                        .build());
    }

    @ExceptionHandler({InvalidKeyException.class})
    public ResponseEntity<ApiException> exceptionHandler(HttpServletRequest request, final InvalidKeyException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(ExceptionEnum.INVALID_KEY_EXCEPTION.getStatus())
                .body(ApiException.builder()
                        .message(e.getMessage())
                        .build());
    }
}
