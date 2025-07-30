package br.com.fiap.postech.restaurantsync.application.gateways;

import br.com.fiap.postech.restaurantsync.domain.entities.User;
import br.com.fiap.postech.restaurantsync.domain.gateways.UserGateway;
import br.com.fiap.postech.restaurantsync.infrastructure.config.mapper.UserMapper;
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

import java.util.Date;
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
        UserEntity userEntity = UserMapper.fromDomain(user);
        userEntity.setPassword(passwordEncoder.encode(user.getPassword()));
        UserEntity saved = this.userRepository.save(userEntity);
        return UserMapper.toDomain(saved);
    }

    @Override
    public boolean existsUserByEmail(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public Page<User> findAllPagedUsers(PageRequest pageRequest) {
        Page<UserEntity> pagedUsers = this.userRepository.findAll(pageRequest);
        return pagedUsers.map(UserMapper::toDomain);
    }

    public User findUserById(Integer id) {
        return findUserOrThrow(id);
    }

    public void deleteUserById(Integer id) {
        try {
            this.userRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("Integrity violaton.");
        }
    }

    @Override
    public User updateUser(Integer id, User userRequest) {
        User user = findUserOrThrow(id);
        validateSelfOrAdmin(user.getId());

        user.setPassword(
                (userRequest.getPassword() != null && !userRequest.getPassword().isEmpty())
                        ? passwordEncoder.encode(userRequest.getPassword())
                        : user.getPassword()
        );
        user.setName(userRequest.getName() != null ? userRequest.getName() : user.getName());
        user.setEmail(userRequest.getEmail() != null ? userRequest.getEmail() : user.getEmail());
        user.setLogin(userRequest.getLogin() != null ? userRequest.getLogin() : user.getLogin());
        user.setAddress(userRequest.getAddress() != null ? userRequest.getAddress() : user.getAddress());
        user.setLastUpdateDate(new Date());

        UserEntity saved = this.userRepository.save(UserMapper.fromDomain(user));
        return UserMapper.toDomain(saved);
    }

    public void updateUserPassword(Integer id, String newPassword) {
        User user = findUserOrThrow(id);
        user.setPassword(passwordEncoder.encode(newPassword));
        this.userRepository.save(UserMapper.fromDomain(user));
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
            UserEntity userEntity = this.userRepository.findByEmail(username).get();
            return UserMapper.toDomain(userEntity);
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
        UserEntity userEntity = this.userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Id not found: " + id));
        return UserMapper.toDomain(userEntity);
    }

    public void validateUserByOwnerId(Integer ownerId) {
        validateAdmin();
        User owner = findUserOrThrow(ownerId);
        if (!owner.hasRole("ROLE_ADMIN")) {
            throw new BusinessException(
                    "Restaurant owner must be an ADMIN user"
            );
        }
    }
}
