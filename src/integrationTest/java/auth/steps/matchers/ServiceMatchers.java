package auth.steps.matchers;

import java.util.List;
import org.hamcrest.Matcher;
import uk.co.dajohnston.auth.model.FieldValidation;
import uk.co.dajohnston.auth.model.User;

public class ServiceMatchers {

    public static Matcher<FieldValidation> fieldError(String field, String message) {
        return new FieldValidationMatcher(field, message);
    }

    public static Matcher<User>[] all(List<User> users) {
        return users.stream().map(UserMatcher::new).toArray(UserMatcher[]::new);
    }
}
