package stepdefinitions;

import br.com.fiap.postech.restaurantsync.RestaurantSyncApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = RestaurantSyncApplication.class)
public class StepDefsDefault {
}
