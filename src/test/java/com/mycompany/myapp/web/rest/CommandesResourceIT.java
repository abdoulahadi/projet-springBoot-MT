package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Commandes;
import com.mycompany.myapp.repository.CommandesRepository;
import com.mycompany.myapp.repository.EntityManager;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link CommandesResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class CommandesResourceIT {

    private static final Instant DEFAULT_DATE_COMMANDE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_COMMANDE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/commandes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CommandesRepository commandesRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Commandes commandes;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Commandes createEntity(EntityManager em) {
        Commandes commandes = new Commandes().dateCommande(DEFAULT_DATE_COMMANDE);
        return commandes;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Commandes createUpdatedEntity(EntityManager em) {
        Commandes commandes = new Commandes().dateCommande(UPDATED_DATE_COMMANDE);
        return commandes;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Commandes.class).block();
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
        commandes = createEntity(em);
    }

    @Test
    void createCommandes() throws Exception {
        int databaseSizeBeforeCreate = commandesRepository.findAll().collectList().block().size();
        // Create the Commandes
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(commandes))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Commandes in the database
        List<Commandes> commandesList = commandesRepository.findAll().collectList().block();
        assertThat(commandesList).hasSize(databaseSizeBeforeCreate + 1);
        Commandes testCommandes = commandesList.get(commandesList.size() - 1);
        assertThat(testCommandes.getDateCommande()).isEqualTo(DEFAULT_DATE_COMMANDE);
    }

    @Test
    void createCommandesWithExistingId() throws Exception {
        // Create the Commandes with an existing ID
        commandes.setId(1L);

        int databaseSizeBeforeCreate = commandesRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(commandes))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Commandes in the database
        List<Commandes> commandesList = commandesRepository.findAll().collectList().block();
        assertThat(commandesList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllCommandesAsStream() {
        // Initialize the database
        commandesRepository.save(commandes).block();

        List<Commandes> commandesList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Commandes.class)
            .getResponseBody()
            .filter(commandes::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(commandesList).isNotNull();
        assertThat(commandesList).hasSize(1);
        Commandes testCommandes = commandesList.get(0);
        assertThat(testCommandes.getDateCommande()).isEqualTo(DEFAULT_DATE_COMMANDE);
    }

    @Test
    void getAllCommandes() {
        // Initialize the database
        commandesRepository.save(commandes).block();

        // Get all the commandesList
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
            .value(hasItem(commandes.getId().intValue()))
            .jsonPath("$.[*].dateCommande")
            .value(hasItem(DEFAULT_DATE_COMMANDE.toString()));
    }

    @Test
    void getCommandes() {
        // Initialize the database
        commandesRepository.save(commandes).block();

        // Get the commandes
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, commandes.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(commandes.getId().intValue()))
            .jsonPath("$.dateCommande")
            .value(is(DEFAULT_DATE_COMMANDE.toString()));
    }

    @Test
    void getNonExistingCommandes() {
        // Get the commandes
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingCommandes() throws Exception {
        // Initialize the database
        commandesRepository.save(commandes).block();

        int databaseSizeBeforeUpdate = commandesRepository.findAll().collectList().block().size();

        // Update the commandes
        Commandes updatedCommandes = commandesRepository.findById(commandes.getId()).block();
        updatedCommandes.dateCommande(UPDATED_DATE_COMMANDE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedCommandes.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedCommandes))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Commandes in the database
        List<Commandes> commandesList = commandesRepository.findAll().collectList().block();
        assertThat(commandesList).hasSize(databaseSizeBeforeUpdate);
        Commandes testCommandes = commandesList.get(commandesList.size() - 1);
        assertThat(testCommandes.getDateCommande()).isEqualTo(UPDATED_DATE_COMMANDE);
    }

    @Test
    void putNonExistingCommandes() throws Exception {
        int databaseSizeBeforeUpdate = commandesRepository.findAll().collectList().block().size();
        commandes.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, commandes.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(commandes))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Commandes in the database
        List<Commandes> commandesList = commandesRepository.findAll().collectList().block();
        assertThat(commandesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCommandes() throws Exception {
        int databaseSizeBeforeUpdate = commandesRepository.findAll().collectList().block().size();
        commandes.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(commandes))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Commandes in the database
        List<Commandes> commandesList = commandesRepository.findAll().collectList().block();
        assertThat(commandesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCommandes() throws Exception {
        int databaseSizeBeforeUpdate = commandesRepository.findAll().collectList().block().size();
        commandes.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(commandes))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Commandes in the database
        List<Commandes> commandesList = commandesRepository.findAll().collectList().block();
        assertThat(commandesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCommandesWithPatch() throws Exception {
        // Initialize the database
        commandesRepository.save(commandes).block();

        int databaseSizeBeforeUpdate = commandesRepository.findAll().collectList().block().size();

        // Update the commandes using partial update
        Commandes partialUpdatedCommandes = new Commandes();
        partialUpdatedCommandes.setId(commandes.getId());

        partialUpdatedCommandes.dateCommande(UPDATED_DATE_COMMANDE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCommandes.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCommandes))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Commandes in the database
        List<Commandes> commandesList = commandesRepository.findAll().collectList().block();
        assertThat(commandesList).hasSize(databaseSizeBeforeUpdate);
        Commandes testCommandes = commandesList.get(commandesList.size() - 1);
        assertThat(testCommandes.getDateCommande()).isEqualTo(UPDATED_DATE_COMMANDE);
    }

    @Test
    void fullUpdateCommandesWithPatch() throws Exception {
        // Initialize the database
        commandesRepository.save(commandes).block();

        int databaseSizeBeforeUpdate = commandesRepository.findAll().collectList().block().size();

        // Update the commandes using partial update
        Commandes partialUpdatedCommandes = new Commandes();
        partialUpdatedCommandes.setId(commandes.getId());

        partialUpdatedCommandes.dateCommande(UPDATED_DATE_COMMANDE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCommandes.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCommandes))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Commandes in the database
        List<Commandes> commandesList = commandesRepository.findAll().collectList().block();
        assertThat(commandesList).hasSize(databaseSizeBeforeUpdate);
        Commandes testCommandes = commandesList.get(commandesList.size() - 1);
        assertThat(testCommandes.getDateCommande()).isEqualTo(UPDATED_DATE_COMMANDE);
    }

    @Test
    void patchNonExistingCommandes() throws Exception {
        int databaseSizeBeforeUpdate = commandesRepository.findAll().collectList().block().size();
        commandes.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, commandes.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(commandes))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Commandes in the database
        List<Commandes> commandesList = commandesRepository.findAll().collectList().block();
        assertThat(commandesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCommandes() throws Exception {
        int databaseSizeBeforeUpdate = commandesRepository.findAll().collectList().block().size();
        commandes.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(commandes))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Commandes in the database
        List<Commandes> commandesList = commandesRepository.findAll().collectList().block();
        assertThat(commandesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCommandes() throws Exception {
        int databaseSizeBeforeUpdate = commandesRepository.findAll().collectList().block().size();
        commandes.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(commandes))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Commandes in the database
        List<Commandes> commandesList = commandesRepository.findAll().collectList().block();
        assertThat(commandesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCommandes() {
        // Initialize the database
        commandesRepository.save(commandes).block();

        int databaseSizeBeforeDelete = commandesRepository.findAll().collectList().block().size();

        // Delete the commandes
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, commandes.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Commandes> commandesList = commandesRepository.findAll().collectList().block();
        assertThat(commandesList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
