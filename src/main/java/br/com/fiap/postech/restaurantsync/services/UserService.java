package br.com.fiap.postech.restaurantsync.services;

import br.com.fiap.postech.restaurantsync.dtos.requests.UserRequest;
import br.com.fiap.postech.restaurantsync.dtos.responses.UserResponse;
import br.com.fiap.postech.restaurantsync.entities.User;
import br.com.fiap.postech.restaurantsync.repositories.UserRepository;
import br.com.fiap.postech.restaurantsync.resources.exceptions.BusinessException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserResponse createUser(UserRequest userRequest) {
        User user = this.userRepository.save(new User(userRequest));
        return new UserResponse(user);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> findAllPagedUsers(PageRequest pageRequest) {
        Page<User> list = this.userRepository.findAll(pageRequest);
        return list.map(UserResponse::new);
    }

    @Transactional(readOnly = true)
    public UserResponse findUserById(Long id) {
        Optional<User> obj = this.userRepository.findById(id);
        User entity = obj.orElseThrow(() -> new BusinessException("Entity not Found"));
        return new UserResponse(entity);
    }

    @Transactional
    public void deleteUser(Long id) {
        try {
            this.userRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new BusinessException("Id not found.");
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("Integrity violaton.");
        }
    }

    @Transactional
    public UserResponse updateUser(Long id, UserRequest userRequest) {
        try {
            User entity = this.userRepository.getReferenceById(id);
            entity = this.userRepository.save(entity);
            return new UserResponse(entity);

        } catch (EntityNotFoundException e) {
            throw new BusinessException("Id not found:" + id);
        }
    }

    @Transactional
    public void updatePassword(Long id, String newPassword) {
        User user = userRepository.findById(id).orElseThrow(() -> new BusinessException("Id not found: " + id));
        user.setPassword(newPassword);
        userRepository.save(user);
    }
}
