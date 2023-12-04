package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Clients;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Clients entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ClientsRepository extends JpaRepository<Clients, Long> {
    @Query("SELECT c FROM Clients c WHERE c.user.id = :clientId")
    List<Clients> findClientByUserId(@Param("clientId") Long id);
}
