package stepdefinitions;

import br.com.fiap.postech.restaurantsync.RestaurantSyncApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = RestaurantSyncApplication.class)
@ActiveProfiles("test")
public class StepDefsDefault {
}
