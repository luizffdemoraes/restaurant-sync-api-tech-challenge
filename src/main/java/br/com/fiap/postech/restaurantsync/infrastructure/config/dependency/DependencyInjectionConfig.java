package br.com.fiap.postech.restaurantsync.infrastructure.config.dependency;

import br.com.fiap.postech.restaurantsync.domain.gateways.RoleGateway;
import br.com.fiap.postech.restaurantsync.domain.gateways.UserGateway;
import br.com.fiap.postech.restaurantsync.domain.usecases.user.*;
import br.com.fiap.postech.restaurantsync.application.gateways.RoleGatewayImpl;
import br.com.fiap.postech.restaurantsync.application.gateways.UserGatewayImpl;
import br.com.fiap.postech.restaurantsync.infrastructure.persistence.repository.RoleRepository;
import br.com.fiap.postech.restaurantsync.infrastructure.persistence.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DependencyInjectionConfig {

    @Bean
    public CreateUserUseCase createUserUseCase(UserGateway userGateway,
                                               RoleGateway roleGateway) {
        return new CreateUserUseCaseImp(userGateway, roleGateway);
    }

    @Bean
    public DeleteUserUseCase deleteUserUseCase(UserGateway userGateway) {
        return new DeleteUserUseCaseImp(userGateway);
    }

    @Bean
    public FindUserByIdUseCase findUserByIdUseCase(UserGateway userGateway) {
        return new FindUserByIdUseCaseImp(userGateway);
    }

    @Bean
    public FindAllPagedUsersUseCase findAllPagedUsersUseCase(UserGateway userGateway) {
        return new FindAllPagedUsersUseCaseImp(userGateway);
    }

    @Bean
    public UpdateUserUseCase updateUserUseCase(UserGateway userGateway) {
        return new UpdateUserUseCaseImp(userGateway);
    }

    @Bean
    public UpdatePasswordUseCase updatePasswordUseCase(UserGateway userGateway) {
        return new UpdatePasswordUseCaseImp(userGateway);
    }

    @Bean
    public UserGateway userGateway(UserRepository repository,
                                   PasswordEncoder passwordEncoder) {
        return new UserGatewayImpl(repository, passwordEncoder);
    }

    @Bean
    public RoleGateway roleGateway(RoleRepository repository) {
        return new RoleGatewayImpl(repository);
    }

    @Bean
    @Primary
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
