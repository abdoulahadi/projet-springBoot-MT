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

    @Query("SELECT c, cl, pd FROM Commandes c JOIN Clients cl ON cl.id = c.clients.id JOIN Produits pd ON pd.id = c.produits.id")
    List<Commandes> findAllDetail();

    @Query(
        "SELECT p, COUNT(*) AS nombreCommandes FROM Commandes c JOIN Produits p ON p.id = c.produits.id GROUP BY p ORDER BY nombreCommandes DESC "
    )
    List<Produits> findProduitPlusPresent();
}
