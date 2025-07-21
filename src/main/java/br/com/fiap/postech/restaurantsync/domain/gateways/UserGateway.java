package br.com.fiap.postech.restaurantsync.domain.gateways;

import br.com.fiap.postech.restaurantsync.domain.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserGateway {
    User saveUser(User user);
    boolean existsUserByEmail(String email);
    Page<User> findAllPagedUsers(PageRequest pageRequest);
    User findUserById(Integer id);
    void deleteUserById(Integer id);
    User updateUser(Integer id, User user);
    void updateUserPassword(Integer id, String newPassword);
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
    User authenticated();
    void validateAdmin();
    void validateSelfOrAdmin(Integer userId);
    User findUserOrThrow(Integer id);
}