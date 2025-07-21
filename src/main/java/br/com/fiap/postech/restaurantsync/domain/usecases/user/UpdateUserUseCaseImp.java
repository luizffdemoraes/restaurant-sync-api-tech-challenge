package br.com.fiap.postech.restaurantsync.domain.usecases.user;

import br.com.fiap.postech.restaurantsync.domain.entities.User;
import br.com.fiap.postech.restaurantsync.domain.gateways.UserGateway;
import br.com.fiap.postech.restaurantsync.infrastructure.application.dtos.requests.UserRequest;
import br.com.fiap.postech.restaurantsync.infrastructure.application.dtos.responses.UserResponse;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UpdateUserUseCaseImp implements UpdateUserUseCase{

    private final UserGateway userGateway;

    public UpdateUserUseCaseImp(UserGateway userGateway) {
        this.userGateway = userGateway;
    }

    @Override
    public UserResponse execute(Integer id, UserRequest request) {
        User user = new User(request);
        this.userGateway.validateSelfOrAdmin(id);
        this.userGateway.updateUser(id, user);
        return new UserResponse(user);
    }
}
