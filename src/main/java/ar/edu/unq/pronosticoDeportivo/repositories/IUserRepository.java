package ar.edu.unq.pronosticoDeportivo.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ar.edu.unq.pronosticoDeportivo.model.User;

public interface IUserRepository extends JpaRepository<User, Long>{
    @Query(
        "SELECT u FROM User u WHERE u.name = ?1"
    )
    User getByName(String name);
}
