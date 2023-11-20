package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Commandes;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Commandes entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CommandesRepository extends JpaRepository<Commandes, Long> {}
