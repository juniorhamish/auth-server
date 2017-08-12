package auth.steps;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import cucumber.api.java.After;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.restassured.response.Response;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.dajohnston.auth.model.Role;
import uk.co.dajohnston.auth.model.User;

public class UserSteps {

    private static final String SECRET_KEY = "Tn5UViRLPEgpI0hvbmZRbjpmTlY5RzJpJys+STxfImp7X203KGMsdCY5ZSpfYmNUTi0nQVUoVj9eZXw3SDNv";

    @Autowired
    private RestSteps restSteps;
    private Response response;
    private Set<Long> registeredUsers = new HashSet<>();

    @After
    public void cleanup() {
        String adminAuthToken = Jwts.builder().claim("name", "admin").claim("role", Role.ADMIN)
                .setExpiration(new Date(System.currentTimeMillis() + 10000)).signWith(SignatureAlgorithm.HS512, SECRET_KEY).compact();

        registeredUsers.forEach(id -> restSteps.executeDelete("/users/" + id, adminAuthToken));
    }

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
        Claims body = getAuthTokenFromResponse();
        assertThat(body.get("role"), is(expectedRole));
    }

    @Then("^the response should contain authorization token for \"([^\"]*)\"$")
    public void theResponseShouldContainAuthorizationTokenFor(String emailAddress) {
        Claims body = getAuthTokenFromResponse();
        assertThat(body.get("name"), is(emailAddress));
    }

    private Claims getAuthTokenFromResponse() {
        String authorizationHeader = response.getHeader("Authorization");
        String token = authorizationHeader.replace("Bearer ", "");
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
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

        User userResponse = response.as(User.class);
        if (userResponse.getId() != null) {
            registeredUsers.add(userResponse.getId());
        }
    }

    @When("^I log in as \"([^\"]*)\" with password \"([^\"]*)\"$")
    public void logInAsUser(String emailAddress, String password) {
        Map<String, String> data = new HashMap<>();
        data.put("emailAddress", emailAddress);
        data.put("password", password);
        response = restSteps.executePost("/login", data);
    }
}
