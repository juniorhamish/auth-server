package auth;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/integrationTest/resources/features", format = {"pretty"})
public class CucumberTestRunner {

}
