package ar.edu.unq.pronosticodeportivo.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ar.edu.unq.pronosticodeportivo.model.User;

public interface IUserRepository extends JpaRepository<User, Long>{
    @Query(
        "SELECT u FROM User u WHERE u.name = ?1"
    )
    User getByName(String name);
}
