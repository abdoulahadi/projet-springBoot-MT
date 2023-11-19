package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.ClientsTestSamples.*;
import static com.mycompany.myapp.domain.CommandesTestSamples.*;
import static com.mycompany.myapp.domain.ProduitsTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CommandesTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Commandes.class);
        Commandes commandes1 = getCommandesSample1();
        Commandes commandes2 = new Commandes();
        assertThat(commandes1).isNotEqualTo(commandes2);

        commandes2.setId(commandes1.getId());
        assertThat(commandes1).isEqualTo(commandes2);

        commandes2 = getCommandesSample2();
        assertThat(commandes1).isNotEqualTo(commandes2);
    }

    @Test
    void clientsTest() throws Exception {
        Commandes commandes = getCommandesRandomSampleGenerator();
        Clients clientsBack = getClientsRandomSampleGenerator();

        commandes.setClients(clientsBack);
        assertThat(commandes.getClients()).isEqualTo(clientsBack);

        commandes.clients(null);
        assertThat(commandes.getClients()).isNull();
    }

    @Test
    void produitsTest() throws Exception {
        Commandes commandes = getCommandesRandomSampleGenerator();
        Produits produitsBack = getProduitsRandomSampleGenerator();

        commandes.setProduits(produitsBack);
        assertThat(commandes.getProduits()).isEqualTo(produitsBack);

        commandes.produits(null);
        assertThat(commandes.getProduits()).isNull();
    }
}
