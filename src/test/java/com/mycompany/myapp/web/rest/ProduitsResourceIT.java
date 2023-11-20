package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Produits;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.ProduitsRepository;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link ProduitsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ProduitsResourceIT {

    private static final String DEFAULT_NOM_PRODUIT = "AAAAAAAAAA";
    private static final String UPDATED_NOM_PRODUIT = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION_PRODUIT = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION_PRODUIT = "BBBBBBBBBB";

    private static final Long DEFAULT_PRIX_PRODUIT = 1L;
    private static final Long UPDATED_PRIX_PRODUIT = 2L;

    private static final String DEFAULT_IMAGE_PRODUIT = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE_PRODUIT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/produits";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProduitsRepository produitsRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Produits produits;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Produits createEntity(EntityManager em) {
        Produits produits = new Produits()
            .nomProduit(DEFAULT_NOM_PRODUIT)
            .descriptionProduit(DEFAULT_DESCRIPTION_PRODUIT)
            .prixProduit(DEFAULT_PRIX_PRODUIT)
            .imageProduit(DEFAULT_IMAGE_PRODUIT);
        return produits;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Produits createUpdatedEntity(EntityManager em) {
        Produits produits = new Produits()
            .nomProduit(UPDATED_NOM_PRODUIT)
            .descriptionProduit(UPDATED_DESCRIPTION_PRODUIT)
            .prixProduit(UPDATED_PRIX_PRODUIT)
            .imageProduit(UPDATED_IMAGE_PRODUIT);
        return produits;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Produits.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        produits = createEntity(em);
    }

    @Test
    void createProduits() throws Exception {
        int databaseSizeBeforeCreate = produitsRepository.findAll().collectList().block().size();
        // Create the Produits
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(produits))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Produits in the database
        List<Produits> produitsList = produitsRepository.findAll().collectList().block();
        assertThat(produitsList).hasSize(databaseSizeBeforeCreate + 1);
        Produits testProduits = produitsList.get(produitsList.size() - 1);
        assertThat(testProduits.getNomProduit()).isEqualTo(DEFAULT_NOM_PRODUIT);
        assertThat(testProduits.getDescriptionProduit()).isEqualTo(DEFAULT_DESCRIPTION_PRODUIT);
        assertThat(testProduits.getPrixProduit()).isEqualTo(DEFAULT_PRIX_PRODUIT);
        assertThat(testProduits.getImageProduit()).isEqualTo(DEFAULT_IMAGE_PRODUIT);
    }

    @Test
    void createProduitsWithExistingId() throws Exception {
        // Create the Produits with an existing ID
        produits.setId(1L);

        int databaseSizeBeforeCreate = produitsRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(produits))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Produits in the database
        List<Produits> produitsList = produitsRepository.findAll().collectList().block();
        assertThat(produitsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllProduitsAsStream() {
        // Initialize the database
        produitsRepository.save(produits).block();

        List<Produits> produitsList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Produits.class)
            .getResponseBody()
            .filter(produits::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(produitsList).isNotNull();
        assertThat(produitsList).hasSize(1);
        Produits testProduits = produitsList.get(0);
        assertThat(testProduits.getNomProduit()).isEqualTo(DEFAULT_NOM_PRODUIT);
        assertThat(testProduits.getDescriptionProduit()).isEqualTo(DEFAULT_DESCRIPTION_PRODUIT);
        assertThat(testProduits.getPrixProduit()).isEqualTo(DEFAULT_PRIX_PRODUIT);
        assertThat(testProduits.getImageProduit()).isEqualTo(DEFAULT_IMAGE_PRODUIT);
    }

    @Test
    void getAllProduits() {
        // Initialize the database
        produitsRepository.save(produits).block();

        // Get all the produitsList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(produits.getId().intValue()))
            .jsonPath("$.[*].nomProduit")
            .value(hasItem(DEFAULT_NOM_PRODUIT))
            .jsonPath("$.[*].descriptionProduit")
            .value(hasItem(DEFAULT_DESCRIPTION_PRODUIT))
            .jsonPath("$.[*].prixProduit")
            .value(hasItem(DEFAULT_PRIX_PRODUIT.intValue()))
            .jsonPath("$.[*].imageProduit")
            .value(hasItem(DEFAULT_IMAGE_PRODUIT));
    }

    @Test
    void getProduits() {
        // Initialize the database
        produitsRepository.save(produits).block();

        // Get the produits
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, produits.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(produits.getId().intValue()))
            .jsonPath("$.nomProduit")
            .value(is(DEFAULT_NOM_PRODUIT))
            .jsonPath("$.descriptionProduit")
            .value(is(DEFAULT_DESCRIPTION_PRODUIT))
            .jsonPath("$.prixProduit")
            .value(is(DEFAULT_PRIX_PRODUIT.intValue()))
            .jsonPath("$.imageProduit")
            .value(is(DEFAULT_IMAGE_PRODUIT));
    }

    @Test
    void getNonExistingProduits() {
        // Get the produits
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingProduits() throws Exception {
        // Initialize the database
        produitsRepository.save(produits).block();

        int databaseSizeBeforeUpdate = produitsRepository.findAll().collectList().block().size();

        // Update the produits
        Produits updatedProduits = produitsRepository.findById(produits.getId()).block();
        updatedProduits
            .nomProduit(UPDATED_NOM_PRODUIT)
            .descriptionProduit(UPDATED_DESCRIPTION_PRODUIT)
            .prixProduit(UPDATED_PRIX_PRODUIT)
            .imageProduit(UPDATED_IMAGE_PRODUIT);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedProduits.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedProduits))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Produits in the database
        List<Produits> produitsList = produitsRepository.findAll().collectList().block();
        assertThat(produitsList).hasSize(databaseSizeBeforeUpdate);
        Produits testProduits = produitsList.get(produitsList.size() - 1);
        assertThat(testProduits.getNomProduit()).isEqualTo(UPDATED_NOM_PRODUIT);
        assertThat(testProduits.getDescriptionProduit()).isEqualTo(UPDATED_DESCRIPTION_PRODUIT);
        assertThat(testProduits.getPrixProduit()).isEqualTo(UPDATED_PRIX_PRODUIT);
        assertThat(testProduits.getImageProduit()).isEqualTo(UPDATED_IMAGE_PRODUIT);
    }

    @Test
    void putNonExistingProduits() throws Exception {
        int databaseSizeBeforeUpdate = produitsRepository.findAll().collectList().block().size();
        produits.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, produits.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(produits))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Produits in the database
        List<Produits> produitsList = produitsRepository.findAll().collectList().block();
        assertThat(produitsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchProduits() throws Exception {
        int databaseSizeBeforeUpdate = produitsRepository.findAll().collectList().block().size();
        produits.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(produits))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Produits in the database
        List<Produits> produitsList = produitsRepository.findAll().collectList().block();
        assertThat(produitsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamProduits() throws Exception {
        int databaseSizeBeforeUpdate = produitsRepository.findAll().collectList().block().size();
        produits.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(produits))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Produits in the database
        List<Produits> produitsList = produitsRepository.findAll().collectList().block();
        assertThat(produitsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateProduitsWithPatch() throws Exception {
        // Initialize the database
        produitsRepository.save(produits).block();

        int databaseSizeBeforeUpdate = produitsRepository.findAll().collectList().block().size();

        // Update the produits using partial update
        Produits partialUpdatedProduits = new Produits();
        partialUpdatedProduits.setId(produits.getId());

        partialUpdatedProduits.prixProduit(UPDATED_PRIX_PRODUIT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProduits.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedProduits))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Produits in the database
        List<Produits> produitsList = produitsRepository.findAll().collectList().block();
        assertThat(produitsList).hasSize(databaseSizeBeforeUpdate);
        Produits testProduits = produitsList.get(produitsList.size() - 1);
        assertThat(testProduits.getNomProduit()).isEqualTo(DEFAULT_NOM_PRODUIT);
        assertThat(testProduits.getDescriptionProduit()).isEqualTo(DEFAULT_DESCRIPTION_PRODUIT);
        assertThat(testProduits.getPrixProduit()).isEqualTo(UPDATED_PRIX_PRODUIT);
        assertThat(testProduits.getImageProduit()).isEqualTo(DEFAULT_IMAGE_PRODUIT);
    }

    @Test
    void fullUpdateProduitsWithPatch() throws Exception {
        // Initialize the database
        produitsRepository.save(produits).block();

        int databaseSizeBeforeUpdate = produitsRepository.findAll().collectList().block().size();

        // Update the produits using partial update
        Produits partialUpdatedProduits = new Produits();
        partialUpdatedProduits.setId(produits.getId());

        partialUpdatedProduits
            .nomProduit(UPDATED_NOM_PRODUIT)
            .descriptionProduit(UPDATED_DESCRIPTION_PRODUIT)
            .prixProduit(UPDATED_PRIX_PRODUIT)
            .imageProduit(UPDATED_IMAGE_PRODUIT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProduits.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedProduits))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Produits in the database
        List<Produits> produitsList = produitsRepository.findAll().collectList().block();
        assertThat(produitsList).hasSize(databaseSizeBeforeUpdate);
        Produits testProduits = produitsList.get(produitsList.size() - 1);
        assertThat(testProduits.getNomProduit()).isEqualTo(UPDATED_NOM_PRODUIT);
        assertThat(testProduits.getDescriptionProduit()).isEqualTo(UPDATED_DESCRIPTION_PRODUIT);
        assertThat(testProduits.getPrixProduit()).isEqualTo(UPDATED_PRIX_PRODUIT);
        assertThat(testProduits.getImageProduit()).isEqualTo(UPDATED_IMAGE_PRODUIT);
    }

    @Test
    void patchNonExistingProduits() throws Exception {
        int databaseSizeBeforeUpdate = produitsRepository.findAll().collectList().block().size();
        produits.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, produits.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(produits))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Produits in the database
        List<Produits> produitsList = produitsRepository.findAll().collectList().block();
        assertThat(produitsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchProduits() throws Exception {
        int databaseSizeBeforeUpdate = produitsRepository.findAll().collectList().block().size();
        produits.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(produits))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Produits in the database
        List<Produits> produitsList = produitsRepository.findAll().collectList().block();
        assertThat(produitsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamProduits() throws Exception {
        int databaseSizeBeforeUpdate = produitsRepository.findAll().collectList().block().size();
        produits.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(produits))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Produits in the database
        List<Produits> produitsList = produitsRepository.findAll().collectList().block();
        assertThat(produitsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteProduits() {
        // Initialize the database
        produitsRepository.save(produits).block();

        int databaseSizeBeforeDelete = produitsRepository.findAll().collectList().block().size();

        // Delete the produits
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, produits.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Produits> produitsList = produitsRepository.findAll().collectList().block();
        assertThat(produitsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
