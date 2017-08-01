package auth.steps;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.restassured.response.Response;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.dajohnston.auth.model.User;

public class UserSteps {

    private static final String SECRET_KEY = "Tn5UViRLPEgpI0hvbmZRbjpmTlY5RzJpJys+STxfImp7X203KGMsdCY5ZSpfYmNUTi0nQVUoVj9eZXw3SDNv";

    @Autowired
    private RestSteps restSteps;
    private Response response;

    @When("^I register a new user$")
    public void createNewUser() {
        User user = new User();
        user.setFirstName("Dave");
        user.setLastName("Johnston");
        user.setEmailAddress("dave@test.com");
        user.setPasswordConfirm("Password1");
        user.setPassword("Password1");
        registerUser(Collections.singletonList(user));
    }

    @Then("^the user should have the \"([^\"]*)\" role$")
    public void verifyWebTokenContainsRole(String expectedRole) {
        String authorizationHeader = response.getHeader("Authorization");
        String token = authorizationHeader.replace("Bearer ", "");
        Claims body = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
        String role = body.get("role", String.class);

        assertThat(role, is(expectedRole));
    }

    @When("^I register user:$")
    public void registerUser(List<User> users) {
        User user = users.get(0);
        Map<String, String> data = new HashMap<>();
        data.put("firstName", user.getFirstName());
        data.put("lastName", user.getLastName());
        data.put("emailAddress", user.getEmailAddress().isEmpty() ? null : user.getEmailAddress());
        data.put("password", user.getPassword());
        data.put("passwordConfirm", user.getPasswordConfirm());
        response = restSteps.executePost("/signup", data);
    }
}
