package br.com.fiap.postech.restaurantsync.repositories;

import br.com.fiap.postech.restaurantsync.entities.User;
import br.com.fiap.postech.restaurantsync.entities.UserDetailsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    @Query(nativeQuery = true, value = """
			SELECT u.email AS username, u.password, r.id AS roleId, r.authority
			FROM users u
			INNER JOIN user_role ur ON u.id = ur.user_id
			INNER JOIN roles r ON r.id = ur.role_id
			WHERE u.email = :email
		""")
    List<UserDetailsProjection> searchUserAndRolesByEmail(String email);
}
