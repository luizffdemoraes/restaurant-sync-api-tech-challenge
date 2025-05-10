package br.com.fiap.postech.restaurantsync.services;

import br.com.fiap.postech.restaurantsync.dtos.requests.UserRequest;
import br.com.fiap.postech.restaurantsync.dtos.responses.UserResponse;
import br.com.fiap.postech.restaurantsync.entities.User;
import br.com.fiap.postech.restaurantsync.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse createUser(UserRequest userRequest) {
        User user = this.userRepository.save(new User(userRequest));
        return new UserResponse(user);
    }
}
