package auth.steps.matchers;

import java.text.MessageFormat;
import java.util.List;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import uk.co.dajohnston.auth.model.User;

public class UserMatcher extends BaseMatcher<User> {

    private User expectedUser;

    private UserMatcher(User expectedUser) {
        this.expectedUser = expectedUser;
    }

    @Override
    public boolean matches(Object item) {
        User user = (User) item;
        return user.getFirstName().equals(expectedUser.getFirstName()) && user.getLastName().equals(expectedUser.getLastName()) && user
                .getEmailAddress().equals(expectedUser.getEmailAddress()) && user.getRole().equals(expectedUser.getRole());
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(MessageFormat
                .format("User [firstName: [{0}], lastName: [{1}], emailAddress: [{2}], role: [{3}]]", expectedUser.getFirstName(),
                        expectedUser.getLastName(), expectedUser.getEmailAddress(), expectedUser.getRole()));
    }

    public static Matcher<User>[] all(List<User> users) {
        return users.stream().map(UserMatcher::new).toArray(UserMatcher[]::new);
    }
}
