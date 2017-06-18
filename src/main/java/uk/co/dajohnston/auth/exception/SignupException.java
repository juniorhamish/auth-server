package uk.co.dajohnston.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SignupException extends RuntimeException {

    public SignupException(String message) {
        super(message);
    }
}
