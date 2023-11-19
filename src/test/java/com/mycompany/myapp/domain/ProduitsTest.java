package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.CategoriesTestSamples.*;
import static com.mycompany.myapp.domain.ProduitsTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProduitsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Produits.class);
        Produits produits1 = getProduitsSample1();
        Produits produits2 = new Produits();
        assertThat(produits1).isNotEqualTo(produits2);

        produits2.setId(produits1.getId());
        assertThat(produits1).isEqualTo(produits2);

        produits2 = getProduitsSample2();
        assertThat(produits1).isNotEqualTo(produits2);
    }

    @Test
    void categoriesTest() throws Exception {
        Produits produits = getProduitsRandomSampleGenerator();
        Categories categoriesBack = getCategoriesRandomSampleGenerator();

        produits.setCategories(categoriesBack);
        assertThat(produits.getCategories()).isEqualTo(categoriesBack);

        produits.categories(null);
        assertThat(produits.getCategories()).isNull();
    }
}
