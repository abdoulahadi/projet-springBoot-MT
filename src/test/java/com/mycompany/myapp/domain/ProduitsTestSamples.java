package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ProduitsTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Produits getProduitsSample1() {
        return new Produits().id(1L).nomProduit("nomProduit1").descriptionProduit("descriptionProduit1").prixProduit(1L);
    }

    public static Produits getProduitsSample2() {
        return new Produits().id(2L).nomProduit("nomProduit2").descriptionProduit("descriptionProduit2").prixProduit(2L);
    }

    public static Produits getProduitsRandomSampleGenerator() {
        return new Produits()
            .id(longCount.incrementAndGet())
            .nomProduit(UUID.randomUUID().toString())
            .descriptionProduit(UUID.randomUUID().toString())
            .prixProduit(longCount.incrementAndGet());
    }
}
