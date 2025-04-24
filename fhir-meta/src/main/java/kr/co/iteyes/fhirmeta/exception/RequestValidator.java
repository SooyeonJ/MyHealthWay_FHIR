package kr.co.iteyes.fhirmeta.exception;

import javax.validation.Validation;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import java.util.*;

@Component
public class RequestValidator implements Validator {

    private final SpringValidatorAdapter validator;

    public RequestValidator(SpringValidatorAdapter validator) {
        this.validator = new SpringValidatorAdapter(Validation.buildDefaultValidatorFactory().getValidator());
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (target instanceof Collection) {
            Collection collection = (Collection) target;
            for (final Object object : collection) {
                validator.validate(object, errors);
            }
        }
        else {
            validator.validate(target, errors);
        }
    }

    /**
     * 유효성 검사를 하고 예외처리를 합니다.
     * @param target
     * @param errors
     */
    public void validateParameters(Object target, Errors errors)  {
        Exception result = null;
        this.validate(target, errors);
        if (errors.hasErrors() && errors.getFieldErrorCount() > 0) {
            FieldError fieldError = errors.getFieldErrors().get(0);
            String code = fieldError.getCode();
            String message = fieldError.getDefaultMessage();

            if (message.length() > 1 && message.startsWith(":")) {
                message = message.substring(1);
            }
            else {
                if (code != null && !code.isEmpty()) {
                    switch(code) {
                        case "NotNull":
                            message = "필수 요청 항목입니다.";
                            break;
                        case "NotBlank":
                            message = "필수 요청 항목입니다.";
                            break;
                        case "NotEmpty":
                            message = "필수 요청 항목입니다.";
                            break;
                    }
                }
            }
            throw new CustomException(ExceptionEnum.INVALID_PARAMETER_EXCEPTION, new String[]{fieldError.getField(), message});
        }
    }

}
