package ar.edu.unq.pronostico.deportivo.repositories;
import ar.edu.unq.pronostico.deportivo.model.User;
import org.hibernate.query.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IUserRepository extends JpaRepository<User, Long>{
    @Query(
        "SELECT u FROM User u WHERE u.name = ?1"
    )
    User getByName(String name);
}
