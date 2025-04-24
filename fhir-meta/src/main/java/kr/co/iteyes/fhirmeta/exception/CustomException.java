package kr.co.iteyes.fhirmeta.exception;

import lombok.Data;

@Data
public class CustomException extends RuntimeException {
    private ExceptionEnum error;
    private Object[] args = null;

    public CustomException(ExceptionEnum e) {
        super(e.getMessage());
        this.error = e;
    }

    public CustomException(ExceptionEnum e, Object[] args) {
        super(args != null && args.length > 0 ? String.format(e.getMessage(), args) : e.getMessage());
        this.error = e;
        this.args = args;
    }
}
