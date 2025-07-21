package br.com.fiap.postech.restaurantsync.domain.usecases.user;


import br.com.fiap.postech.restaurantsync.domain.entities.Role;
import br.com.fiap.postech.restaurantsync.domain.entities.User;
import br.com.fiap.postech.restaurantsync.domain.gateways.RoleGateway;
import br.com.fiap.postech.restaurantsync.domain.gateways.UserGateway;
import br.com.fiap.postech.restaurantsync.infrastructure.application.dtos.requests.UserRequest;
import br.com.fiap.postech.restaurantsync.infrastructure.application.dtos.responses.UserResponse;
import br.com.fiap.postech.restaurantsync.infrastructure.exceptions.BusinessException;

public class CreateUserUseCaseImp implements CreateUserUseCase {

    private final UserGateway userGateway;
    private final RoleGateway roleGateway;

    public CreateUserUseCaseImp(UserGateway userGateway,
                                RoleGateway roleGateway) {
        this.userGateway = userGateway;
        this.roleGateway = roleGateway;
    }

    public UserResponse execute(UserRequest userRequest) {
        if (userGateway.existsUserByEmail(userRequest.email())) {
            throw new BusinessException("Email already registered");
        }

        User user = new User(userRequest);
        Role role = getRoleForEmail(userRequest.email());
        user.addRole(role);

        User savedUser = userGateway.saveUser(user);
        return new UserResponse(savedUser);
    }

    private Role getRoleForEmail(String email) {
        String authority = email != null && email.toLowerCase().endsWith("@restaurantsync.com")
                ? "ROLE_ADMIN" : "ROLE_CLIENT";

        return roleGateway.findByAuthority(authority)
                .orElseThrow(() -> new BusinessException("Role not found: " + authority));
    }
}
