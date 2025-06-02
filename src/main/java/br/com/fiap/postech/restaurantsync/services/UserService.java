package br.com.fiap.postech.restaurantsync.services;

import br.com.fiap.postech.restaurantsync.dtos.requests.UserRequest;
import br.com.fiap.postech.restaurantsync.dtos.responses.UserResponse;
import br.com.fiap.postech.restaurantsync.entities.Role;
import br.com.fiap.postech.restaurantsync.entities.User;
import br.com.fiap.postech.restaurantsync.entities.UserDetailsProjection;
import br.com.fiap.postech.restaurantsync.repositories.RoleRepository;
import br.com.fiap.postech.restaurantsync.repositories.UserRepository;
import br.com.fiap.postech.restaurantsync.resources.exceptions.BusinessException;
import org.springframework.dao.DataIntegrityViolationException;
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
        validateAdmin();
        Page<User> list = this.userRepository.findAll(pageRequest);
        return list.map(UserResponse::new);
    }

    @Transactional(readOnly = true)
    public UserResponse findUserById(Integer id) {
        User user = findUserOrThrow(id);
        validateSelfOrAdmin(user.getId());
        return new UserResponse(user);
    }

    @Transactional
    public void deleteUser(Integer id) {
        validateAdmin();
        findUserOrThrow(id);
        try {
            this.userRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("Integrity violaton.");
        }
    }

    @Transactional
    public UserResponse updateUser(Integer id, UserRequest userRequest) {
        User user = findUserOrThrow(id);
        validateSelfOrAdmin(user.getId());
        user.setPassword(passwordEncoder.encode(userRequest.password()));
        user = this.userRepository.save(user);
        return new UserResponse(user);
    }

    @Transactional
    public void updatePassword(Integer id, String newPassword) {
        User user = findUserOrThrow(id);
        validateSelfOrAdmin(user.getId());
        user.setPassword(passwordEncoder.encode(newPassword));
        this.userRepository.save(user);
    }

    private User findUserOrThrow(Integer id) {
        return this.userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Id não encontrado: " + id));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<UserDetailsProjection> result = this.userRepository.searchUserAndRolesByEmail(username);
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

    public User authenticated() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Jwt jwtPrincipal = (Jwt) authentication.getPrincipal();
            String username = jwtPrincipal.getClaim("username");
            return this.userRepository.findByEmail(username).get();
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

    public Role getRoleForEmail(String email) {
        if (email != null && email.toLowerCase().endsWith("@restaurantsync.com")) {
            return roleRepository.findByAuthority("ROLE_ADMIN")
                    .orElseThrow(() -> new BusinessException("Role ADMIN não encontrada."));
        } else {
            return roleRepository.findByAuthority("ROLE_CLIENT")
                    .orElseThrow(() -> new BusinessException("Role CLIENT não encontrada."));
        }
    }
}
