package br.com.fiap.postech.restaurantsync.domain.usecases.user;

import br.com.fiap.postech.restaurantsync.domain.entities.User;
import br.com.fiap.postech.restaurantsync.domain.gateways.UserGateway;

public class UpdateUserUseCaseImp implements UpdateUserUseCase{

    private final UserGateway userGateway;

    public UpdateUserUseCaseImp(UserGateway userGateway) {
        this.userGateway = userGateway;
    }

    @Override
    public User execute(Integer id, User user) {
        this.userGateway.validateSelfOrAdmin(id);
        return this.userGateway.updateUser(id, user);
    }
}
