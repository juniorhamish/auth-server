package auth.steps;

import static auth.steps.matchers.ServiceMatchers.fieldError;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.post;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

import auth.steps.matchers.FieldValidationMatcher;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import cucumber.api.PendingException;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.mapper.factory.Jackson2ObjectMapperFactory;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.hamcrest.Matcher;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import uk.co.dajohnston.auth.Application;
import uk.co.dajohnston.auth.model.ErrorResponse;
import uk.co.dajohnston.auth.model.FieldValidation;

@ContextConfiguration(classes = Application.class)
@ActiveProfiles(profiles = "test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class RestSteps {

    @LocalServerPort
    int port;

    private Response response;

    @Before
    public void setPort() {
        RestAssured.port = port;

        Jackson2ObjectMapperFactory jackson2ObjectMapperFactory = (aClass, s) -> {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return objectMapper;
        };
        ObjectMapperConfig objectMapperConfig = new ObjectMapperConfig().jackson2ObjectMapperFactory(jackson2ObjectMapperFactory);
        RestAssured.config = RestAssuredConfig.config().objectMapperConfig(objectMapperConfig);
    }

    Response executePost(String url, Map<String, String> params) {
        String body = new Gson().toJson(params);
        response = given().contentType("application/json").body(body).post(url).thenReturn();
        return response;
    }

    Response executeDelete(String url, String authToken) {
        response = given().header("Authorization", "Bearer " + authToken).delete(url).thenReturn();
        return response;
    }

    @Then("^the response status is (\\d+)$")
    public void validateStatusCode(int expectedResponseCode) {
        assertThat(response.statusCode(), is(expectedResponseCode));
    }

    @And("^the response contains error:$")
    public void theResponseContainsError(List<FieldValidation> fieldErrors) {
        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(errorResponse.getFieldErrors(), containsInAnyOrder(fieldErrors(fieldErrors)));
    }

    private FieldValidationMatcher[] fieldErrors(List<FieldValidation> fieldErrors) {
        FieldValidationMatcher[] matchers = new FieldValidationMatcher[fieldErrors.size()];
        Arrays.setAll(matchers, i -> new FieldValidationMatcher(fieldErrors.get(i).getField(), fieldErrors.get(i).getMessage()));
        return matchers;
    }

    @And("^the error message is \"([^\"]*)\"$")
    public void verifyErrorMessageInResponse(String expectedErrorMessage) {
        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(errorResponse.getMessage(), is(expectedErrorMessage));
    }
}
