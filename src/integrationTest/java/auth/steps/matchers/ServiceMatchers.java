package auth.steps.matchers;

import org.hamcrest.Matcher;
import uk.co.dajohnston.auth.model.FieldValidation;

public class ServiceMatchers {

    public static Matcher<FieldValidation> fieldError(String field, String message) {
        return new FieldValidationMatcher(field, message);
    }
}
