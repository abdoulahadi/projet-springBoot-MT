package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Categories;
import com.mycompany.myapp.domain.Produits;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Produits entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProduitsRepository extends JpaRepository<Produits, Long> {
    @Query("SELECT p FROM Produits p WHERE p.categories.id = :categoryId")
    List<Produits> findByCategoriesId(@Param("categoryId") Long id);

    @Query("SELECT p FROM Produits p ORDER BY p.id DESC LIMIT 5")
    List<Produits> findLimited();
}
