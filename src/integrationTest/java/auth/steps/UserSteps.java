package auth.steps;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.restassured.response.Response;
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
        Map<String, String> user = new HashMap<>();
        user.put("firstName", "Dave");
        user.put("lastName", "Johnston");
        user.put("emailAddress", "dave@test.com");
        user.put("password", "Password1");
        user.put("passwordConfirm", "Password1");
        response = restSteps.executePost("/signup", user);
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
    public void registerUser(List<User> user) {
        Map<String, String> data = new HashMap<>();
        data.put("firstName", "Dave");
        data.put("lastName", "Johnston");
        data.put("emailAddress", user.get(0).getEmailAddress());
        data.put("password", "Password1");
        data.put("passwordConfirm", "Password1");
        response = restSteps.executePost("/signup", data);
    }
}
