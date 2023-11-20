package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CategoriesTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Categories getCategoriesSample1() {
        return new Categories().id(1L).nomCategorie("nomCategorie1");
    }

    public static Categories getCategoriesSample2() {
        return new Categories().id(2L).nomCategorie("nomCategorie2");
    }

    public static Categories getCategoriesRandomSampleGenerator() {
        return new Categories().id(longCount.incrementAndGet()).nomCategorie(UUID.randomUUID().toString());
    }
}
