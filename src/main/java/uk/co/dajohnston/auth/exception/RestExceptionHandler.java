package uk.co.dajohnston.auth.exception;

import java.util.stream.Collectors;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import uk.co.dajohnston.auth.model.ErrorResponse;
import uk.co.dajohnston.auth.model.FieldValidation;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(SignUpException.class)
    protected ResponseEntity<Object> handleSignUpException(SignUpException ex) {
        ErrorResponse response = new ErrorResponse();
        response.setFieldErrors(
                ex.getFieldErrors().stream().map(fieldError -> new FieldValidation(fieldError.getField(), fieldError.getDefaultMessage()))
                        .collect(Collectors.toList()));
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(response, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

}
