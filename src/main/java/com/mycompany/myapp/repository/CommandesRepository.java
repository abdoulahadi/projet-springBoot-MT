package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Commandes;
import com.mycompany.myapp.domain.Produits;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Commandes entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CommandesRepository extends JpaRepository<Commandes, Long> {
    @Query("SELECT c FROM Commandes c WHERE c.clients.id = :clientId")
    List<Commandes> findByClientId(@Param("clientId") Long id);
}
