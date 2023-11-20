package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ClientsTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Clients getClientsSample1() {
        return new Clients().id(1L).nom("nom1").prenom("prenom1").adresse("adresse1").telephone("telephone1").email("email1");
    }

    public static Clients getClientsSample2() {
        return new Clients().id(2L).nom("nom2").prenom("prenom2").adresse("adresse2").telephone("telephone2").email("email2");
    }

    public static Clients getClientsRandomSampleGenerator() {
        return new Clients()
            .id(longCount.incrementAndGet())
            .nom(UUID.randomUUID().toString())
            .prenom(UUID.randomUUID().toString())
            .adresse(UUID.randomUUID().toString())
            .telephone(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString());
    }
}
