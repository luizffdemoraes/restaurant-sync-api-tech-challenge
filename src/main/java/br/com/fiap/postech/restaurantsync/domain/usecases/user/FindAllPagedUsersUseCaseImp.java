package br.com.fiap.postech.restaurantsync.domain.usecases.user;

import br.com.fiap.postech.restaurantsync.domain.entities.User;
import br.com.fiap.postech.restaurantsync.domain.gateways.UserGateway;
import br.com.fiap.postech.restaurantsync.infrastructure.application.dtos.responses.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public class FindAllPagedUsersUseCaseImp implements FindAllPagedUsersUseCase {

    private final UserGateway userGateway;

    public FindAllPagedUsersUseCaseImp(UserGateway userGateway) {
        this.userGateway = userGateway;
    }

    @Override
    public Page<UserResponse> execute(PageRequest pageRequest) {
        this.userGateway.validateAdmin();
        Page<User> pagedUsers = this.userGateway.findAllPagedUsers(pageRequest);
        return pagedUsers.map(UserResponse::new);
    }
}
