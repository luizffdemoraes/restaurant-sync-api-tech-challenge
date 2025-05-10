package br.com.fiap.postech.restaurantsync.repositories;

import br.com.fiap.postech.restaurantsync.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
