package br.com.fiap.postech.restaurantsync.infrastructure.config.dependency;

import br.com.fiap.postech.restaurantsync.application.gateways.MenuGatewayImp;
import br.com.fiap.postech.restaurantsync.application.gateways.RestaurantGatewayImpl;
import br.com.fiap.postech.restaurantsync.domain.gateways.MenuGateway;
import br.com.fiap.postech.restaurantsync.domain.gateways.RestaurantGateway;
import br.com.fiap.postech.restaurantsync.domain.gateways.RoleGateway;
import br.com.fiap.postech.restaurantsync.domain.gateways.UserGateway;
import br.com.fiap.postech.restaurantsync.domain.usecases.menu.CreateMenuUseCase;
import br.com.fiap.postech.restaurantsync.domain.usecases.menu.CreateMenuUseCaseImp;
import br.com.fiap.postech.restaurantsync.domain.usecases.restaurant.*;
import br.com.fiap.postech.restaurantsync.domain.usecases.user.*;
import br.com.fiap.postech.restaurantsync.application.gateways.RoleGatewayImpl;
import br.com.fiap.postech.restaurantsync.application.gateways.UserGatewayImpl;
import br.com.fiap.postech.restaurantsync.infrastructure.persistence.repository.MenuRepository;
import br.com.fiap.postech.restaurantsync.infrastructure.persistence.repository.RestaurantRepository;
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

    // Menu Use Cases

    @Bean
    public CreateMenuUseCase createMenuUseCase(MenuGateway menuGateway,
                                               RestaurantGateway restaurantGateway,
                                               UserGateway userGateway) {
        return new CreateMenuUseCaseImp(menuGateway, restaurantGateway, userGateway);
    }

    // Restaurant Use Cases

    @Bean
    public CreateRestaurantUseCase createRestaurantUseCase(RestaurantGateway restaurantGateway,
                                                           UserGateway userGateway) {
        return new CreateRestaurantUseCaseImp(restaurantGateway, userGateway);
    }

    @Bean
    public DeleteRestaurantUseCase deleteRestaurantUseCase(RestaurantGateway restaurantGateway,
                                                           UserGateway userGateway) {
        return new DeleteRestaurantUseCaseImp(restaurantGateway, userGateway);
    }

    @Bean
    public FindAllPagedRestaurantUseCase findAllPagedRestaurantUseCase(RestaurantGateway restaurantGateway,
                                                           UserGateway userGateway) {
        return new FindAllPagedRestaurantUseCaseImp(restaurantGateway, userGateway);
    }

    @Bean
    public FindRestaurantByIdUseCase findRestaurantByIdUseCase(RestaurantGateway restaurantGateway,
                                                                       UserGateway userGateway) {
        return new FindRestaurantByIdUseCaseImp(restaurantGateway, userGateway);
    }

    @Bean
    public UpdateRestaurantUseCase UpdateRestaurantUseCase(RestaurantGateway restaurantGateway,
                                                               UserGateway userGateway) {
        return new UpdateRestaurantUseCaseImp(restaurantGateway, userGateway);
    }

    // User Use Cases

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
    public MenuGateway menuGateway(MenuRepository repository) {
        return new MenuGatewayImp(repository);
    }

    @Bean
    public RestaurantGateway restaurantGateway(RestaurantRepository repository) {
        return new RestaurantGatewayImpl(repository);
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
