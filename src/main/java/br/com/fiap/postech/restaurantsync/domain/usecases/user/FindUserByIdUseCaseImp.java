package br.com.fiap.postech.restaurantsync.domain.usecases.user;

import br.com.fiap.postech.restaurantsync.domain.entities.User;
import br.com.fiap.postech.restaurantsync.domain.gateways.UserGateway;

public class FindUserByIdUseCaseImp implements FindUserByIdUseCase{

    private final UserGateway userGateway;

    public FindUserByIdUseCaseImp(UserGateway userGateway) {
        this.userGateway = userGateway;
    }

    @Override
    public User execute(Integer id) {
        User user = this.userGateway.findUserById(id);
        this.userGateway.validateSelfOrAdmin(user.getId());
        return user;
    }
}
