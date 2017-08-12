package uk.co.dajohnston.auth.model;

import java.util.List;

public class ErrorResponse {

    private int status;
    private List<FieldValidation> fieldErrors;
    private String message;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<FieldValidation> getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(List<FieldValidation> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
