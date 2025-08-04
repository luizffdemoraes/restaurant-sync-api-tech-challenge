package stepdefinitions;


import org.junit.platform.suite.api.*;
import static io.cucumber.junit.platform.engine.Constants.*;


@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(
        key = FEATURES_PROPERTY_NAME,
        value = "classpath:features/user.feature,classpath:features/restaurant.feature,classpath:features/menu.feature"
)
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "stepdefinitions")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME,
        value = "pretty, html:target/cucumber-reports/cucumber.html, json:target/cucumber-reports/cucumber.json")
public class CucumberRunnerTest {
}
