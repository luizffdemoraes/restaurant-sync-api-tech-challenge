package br.com.fiap.postech.restaurantsync.domain.usecases.restaurant;

import br.com.fiap.postech.restaurantsync.domain.gateways.RestaurantGateway;
import br.com.fiap.postech.restaurantsync.domain.gateways.UserGateway;
import br.com.fiap.postech.restaurantsync.infrastructure.exceptions.BusinessException;
import org.springframework.dao.DataIntegrityViolationException;

public class DeleteRestaurantUseCaseImp  implements DeleteRestaurantUseCase{

    private final RestaurantGateway restaurantGateway;
    private final UserGateway userGateway;

    public DeleteRestaurantUseCaseImp(RestaurantGateway restaurantGateway, UserGateway userGateway) {
        this.restaurantGateway = restaurantGateway;
        this.userGateway = userGateway;
    }

    @Override
    public void execute(Integer id) {
        this.userGateway.validateAdmin();
        this.restaurantGateway.findRestaurantOrThrow(id);
        try {
            this.restaurantGateway.deleterRestaurantById(id);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("Integrity violaton.");
        }
    }
}
