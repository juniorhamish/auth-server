package uk.co.dajohnston.auth.model;

public class FieldValidation {

    private String field;
    private String message;

    protected FieldValidation() {
        // Required for JSON deserialisation
    }

    public FieldValidation(String field, String defaultMessage) {
        this.field = field;
        message = defaultMessage;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
