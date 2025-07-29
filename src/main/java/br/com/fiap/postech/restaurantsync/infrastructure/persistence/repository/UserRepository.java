package br.com.fiap.postech.restaurantsync.infrastructure.persistence.repository;

import br.com.fiap.postech.restaurantsync.infrastructure.persistence.entity.UserDetailsProjection;
import br.com.fiap.postech.restaurantsync.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> findByEmail(String email);

	boolean existsByEmail(String email);

    @Query(nativeQuery = true, value = """
			SELECT u.email AS username, u.password, r.id AS roleId, r.authority
			FROM users u
			INNER JOIN user_role ur ON u.id = ur.user_id
			INNER JOIN roles r ON r.id = ur.role_id
			WHERE u.email = :email
		""")
    List<UserDetailsProjection> searchUserAndRolesByEmail(String email);
}
