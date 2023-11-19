package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.CategoriesTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CategoriesTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Categories.class);
        Categories categories1 = getCategoriesSample1();
        Categories categories2 = new Categories();
        assertThat(categories1).isNotEqualTo(categories2);

        categories2.setId(categories1.getId());
        assertThat(categories1).isEqualTo(categories2);

        categories2 = getCategoriesSample2();
        assertThat(categories1).isNotEqualTo(categories2);
    }
}
