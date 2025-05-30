package br.com.fiap.postech.restaurantsync.services;

import br.com.fiap.postech.restaurantsync.dtos.requests.UserRequest;
import br.com.fiap.postech.restaurantsync.dtos.responses.UserResponse;
import br.com.fiap.postech.restaurantsync.entities.Role;
import br.com.fiap.postech.restaurantsync.entities.User;
import br.com.fiap.postech.restaurantsync.entities.UserDetailsProjection;
import br.com.fiap.postech.restaurantsync.repositories.RoleRepository;
import br.com.fiap.postech.restaurantsync.repositories.UserRepository;
import br.com.fiap.postech.restaurantsync.resources.exceptions.BusinessException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;


    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<UserDetailsProjection> result = userRepository.searchUserAndRolesByEmail(username);
        if (result == null || result.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        User user = new User();
        user.setEmail(username);
        user.setPassword(result.get(0).getPassword());
        for (UserDetailsProjection projection : result) {
            user.addRole(new Role(projection.getRoleId(), projection.getAuthority()));
        }
        return user;
    }

    @Transactional
    public UserResponse createUser(UserRequest userRequest) {
        User user = new User(userRequest);
        Role role = getRoleForEmail(userRequest.email());
        user.setPassword(passwordEncoder.encode(userRequest.password()));

        user.addRole(role);

        user = this.userRepository.save(user);
        return new UserResponse(user);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> findAllPagedUsers(PageRequest pageRequest) {
        Page<User> list = this.userRepository.findAll(pageRequest);
        return list.map(UserResponse::new);
    }

    @Transactional(readOnly = true)
    public UserResponse findUserById(Integer id) {
        Optional<User> obj = this.userRepository.findById(id);
        User entity = obj.orElseThrow(() -> new BusinessException("Entity not Found"));
        validateSelfOrAdmin(obj.get().getId());
        return new UserResponse(entity);
    }

    @Transactional
    public void deleteUser(Integer id) {
        try {
            this.userRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new BusinessException("Id not found.");
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("Integrity violaton.");
        }
    }

    @Transactional
    public UserResponse updateUser(Integer id, UserRequest userRequest) {
        try {
            User user = this.userRepository.getReferenceById(id);
            validateSelfOrAdmin(user.getId());
            user.setPassword(passwordEncoder.encode(userRequest.password()));
            user = this.userRepository.save(user);
            return new UserResponse(user);

        } catch (EntityNotFoundException e) {
            throw new BusinessException("Id not found:" + id);
        }
    }

    @Transactional
    public void updatePassword(Integer id, String newPassword) {
        User user = userRepository.findById(id).orElseThrow(() -> new BusinessException("Id not found: " + id));
        validateSelfOrAdmin(user.getId());
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    User authenticated() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Jwt jwtPrincipal = (Jwt) authentication.getPrincipal();
            String username = jwtPrincipal.getClaim("username");
            return userRepository.findByEmail(username).get();
        }
        catch (Exception e) {
            throw new UsernameNotFoundException("Invalid user");
        }
    }

    void validateSelfOrAdmin(Integer userId) {
        User me = authenticated();
        if (!me.hasRole("ROLE_ADMIN") && !me.getId().equals(userId)) {
            throw new BusinessException("Access denied");
        }
    }

    private Role getRoleForEmail(String email) {
        if (email != null && email.toLowerCase().endsWith("@restaurantsync.com")) {
            return roleRepository.findByAuthority("ROLE_ADMIN")
                    .orElseThrow(() -> new BusinessException("Role ADMIN não encontrada."));
        } else {
            return roleRepository.findByAuthority("ROLE_CLIENT")
                    .orElseThrow(() -> new BusinessException("Role CLIENT não encontrada."));
        }
    }
}
