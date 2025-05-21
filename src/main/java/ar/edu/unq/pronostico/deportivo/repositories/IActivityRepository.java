package ar.edu.unq.pronostico.deportivo.repositories;

import ar.edu.unq.pronostico.deportivo.model.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IActivityRepository extends JpaRepository<Activity, Long> {
    @Query("SELECT a FROM Activity a WHERE a.user.name = ?1")
    Page<Activity> getActivityByUser(String userName, Pageable pageable);
}
