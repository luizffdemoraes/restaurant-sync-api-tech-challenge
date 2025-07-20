package br.com.fiap.postech.restaurantsync.infrastructure.config.dependency;

import br.com.fiap.postech.restaurantsync.domain.gateways.RoleGateway;
import br.com.fiap.postech.restaurantsync.domain.gateways.UserGateway;
import br.com.fiap.postech.restaurantsync.domain.usecases.user.CreateUserUseCase;
import br.com.fiap.postech.restaurantsync.domain.usecases.user.CreateUserUseCaseImp;
import br.com.fiap.postech.restaurantsync.infrastructure.application.gateways.RoleGatewayImpl;
import br.com.fiap.postech.restaurantsync.infrastructure.application.gateways.UserGatewayImpl;
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
                                               RoleGateway roleGateway,
                                               PasswordEncoder passwordEncoder) {
        return new CreateUserUseCaseImp(userGateway, roleGateway, passwordEncoder);
    }

    @Bean
    public UserGateway userGateway(UserRepository repository) {
        return new UserGatewayImpl(repository);
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

