package br.com.fiap.postech.restaurantsync.infrastructure.application.gateways;

import br.com.fiap.postech.restaurantsync.domain.entities.User;
import br.com.fiap.postech.restaurantsync.domain.gateways.UserGateway;
import br.com.fiap.postech.restaurantsync.infrastructure.exceptions.BusinessException;
import br.com.fiap.postech.restaurantsync.infrastructure.persistence.entity.RoleEntity;
import br.com.fiap.postech.restaurantsync.infrastructure.persistence.entity.UserDetailsProjection;
import br.com.fiap.postech.restaurantsync.infrastructure.persistence.entity.UserEntity;
import br.com.fiap.postech.restaurantsync.infrastructure.persistence.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserGatewayImpl implements UserGateway {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserGatewayImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User saveUser(User user) {
        UserEntity userEntity = UserEntity.fromDomain(user);
        userEntity.setPassword(passwordEncoder.encode(user.getPassword()));
        UserEntity saved = this.userRepository.save(userEntity);
        return saved.toDomain();
    }

    @Override
    public boolean existsUserByEmail(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public Page<User> findAllPagedUsers(PageRequest pageRequest) {
        validateAdmin();
        Page<UserEntity> pagedUsers = this.userRepository.findAll(pageRequest);
        return pagedUsers.map(UserEntity::toDomain);
    }

    public User findUserById(Integer id) {
        User user = findUserOrThrow(id);
        validateSelfOrAdmin(user.getId());
        return user;
    }

    public void deleteUserById(Integer id) {
        validateAdmin();
        findUserOrThrow(id);
        try {
            this.userRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("Integrity violaton.");
        }
    }

    public User updateUser(Integer id, User userRequest) {
        User user = findUserOrThrow(id);
        validateSelfOrAdmin(user.getId());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        UserEntity saved = this.userRepository.save(UserEntity.fromDomain(user));
        return saved.toDomain();
    }

    public void updateUserPassword(Integer id, String newPassword) {
        User user = findUserOrThrow(id);
        validateSelfOrAdmin(user.getId());
        user.setPassword(passwordEncoder.encode(newPassword));
        this.userRepository.save(UserEntity.fromDomain(user));
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<UserDetailsProjection> result = this.userRepository.searchUserAndRolesByEmail(username);
        if (result == null || result.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(username);
        userEntity.setPassword(result.get(0).getPassword());

        // Cria uma cópia dos roles para evitar lazy loading
        Set<RoleEntity> roles = new HashSet<>();
        for (UserDetailsProjection projection : result) {
            roles.add(new RoleEntity(projection.getRoleId(), projection.getAuthority()));
        }

        // Atribui a coleção completa de uma vez
        userEntity.getRoleEntities().addAll(roles);

        return userEntity;
    }

    public User authenticated() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Jwt jwtPrincipal = (Jwt) authentication.getPrincipal();
            String username = jwtPrincipal.getClaim("username");
            return this.userRepository.findByEmail(username).get().toDomain();
        } catch (Exception e) {
            throw new UsernameNotFoundException("Invalid user");
        }
    }

    public void validateAdmin() {
        User me = authenticated();
        if (!me.hasRole("ROLE_ADMIN")) {
            throw new BusinessException("Access denied");
        }
    }

    public void validateSelfOrAdmin(Integer userId) {
        User me = authenticated();
        if (!me.hasRole("ROLE_ADMIN") && !me.getId().equals(userId)) {
            throw new BusinessException("Access denied");
        }
    }

    public User findUserOrThrow(Integer id) {
        return this.userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Id not found: " + id)).toDomain();
    }
}
