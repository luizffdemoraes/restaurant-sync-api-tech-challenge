package br.com.fiap.postech.restaurantsync.domain.usecases.user;

import br.com.fiap.postech.restaurantsync.domain.entities.User;
import br.com.fiap.postech.restaurantsync.domain.gateways.UserGateway;
import br.com.fiap.postech.restaurantsync.application.dtos.requests.UserRequest;
import br.com.fiap.postech.restaurantsync.application.dtos.responses.UserResponse;

public class UpdateUserUseCaseImp implements UpdateUserUseCase{

    private final UserGateway userGateway;

    public UpdateUserUseCaseImp(UserGateway userGateway) {
        this.userGateway = userGateway;
    }

    @Override
    public UserResponse execute(Integer id, UserRequest request) {
        User user = new User(request);
        this.userGateway.validateSelfOrAdmin(id);
        User updateUser = this.userGateway.updateUser(id, user);
        return new UserResponse(updateUser);
    }
}
