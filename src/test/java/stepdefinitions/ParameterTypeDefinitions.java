package stepdefinitions;

import io.cucumber.java.ParameterType;

public class ParameterTypeDefinitions {

    @ParameterType("true|false")
    public Boolean booleanValue(String value) {
        return Boolean.parseBoolean(value);
    }
}
