package auth.steps.matchers;

import java.text.MessageFormat;
import java.util.List;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import uk.co.dajohnston.auth.model.FieldValidation;

public class FieldValidationMatcher extends BaseMatcher<FieldValidation> {

    private static final String PATTERN = "a FieldValidation for field [{0}] with message [{1}]";
    private final String field;
    private final String errorMessage;

    public FieldValidationMatcher(String field, String errorMessage) {
        this.field = field;
        this.errorMessage = errorMessage;
    }

    @Override
    public boolean matches(Object item) {
        FieldValidation fieldValidation = (FieldValidation) item;
        return fieldValidation.getField().equals(field) && fieldValidation.getMessage().equals(errorMessage);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(MessageFormat.format(PATTERN, field, errorMessage));
    }

    @Override
    public void describeMismatch(Object item, Description description) {
        FieldValidation fieldValidation = (FieldValidation) item;
        description.appendText("was ").appendText(MessageFormat.format(PATTERN, fieldValidation.getField(), fieldValidation.getMessage()));
    }

    public static FieldValidationMatcher[] all(List<FieldValidation> fieldErrors) {
        return fieldErrors.stream().map(fieldError -> new FieldValidationMatcher(fieldError.getField(), fieldError.getMessage()))
                .toArray(FieldValidationMatcher[]::new);
    }
}
