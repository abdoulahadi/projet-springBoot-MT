package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class CommandesTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Commandes getCommandesSample1() {
        return new Commandes().id(1L).idCommande(1L);
    }

    public static Commandes getCommandesSample2() {
        return new Commandes().id(2L).idCommande(2L);
    }

    public static Commandes getCommandesRandomSampleGenerator() {
        return new Commandes().id(longCount.incrementAndGet()).idCommande(longCount.incrementAndGet());
    }
}
