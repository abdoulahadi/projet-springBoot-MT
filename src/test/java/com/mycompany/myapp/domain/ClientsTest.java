package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.ClientsTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ClientsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Clients.class);
        Clients clients1 = getClientsSample1();
        Clients clients2 = new Clients();
        assertThat(clients1).isNotEqualTo(clients2);

        clients2.setId(clients1.getId());
        assertThat(clients1).isEqualTo(clients2);

        clients2 = getClientsSample2();
        assertThat(clients1).isNotEqualTo(clients2);
    }
}
