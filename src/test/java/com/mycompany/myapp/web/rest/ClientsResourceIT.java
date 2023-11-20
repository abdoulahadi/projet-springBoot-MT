package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Clients;
import com.mycompany.myapp.repository.ClientsRepository;
import com.mycompany.myapp.repository.EntityManager;
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
 * Integration tests for the {@link ClientsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ClientsResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final String DEFAULT_PRENOM = "AAAAAAAAAA";
    private static final String UPDATED_PRENOM = "BBBBBBBBBB";

    private static final String DEFAULT_ADRESSE = "AAAAAAAAAA";
    private static final String UPDATED_ADRESSE = "BBBBBBBBBB";

    private static final String DEFAULT_TELEPHONE = "AAAAAAAAAA";
    private static final String UPDATED_TELEPHONE = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final Long DEFAULT_ID_USER = 1L;
    private static final Long UPDATED_ID_USER = 2L;

    private static final String ENTITY_API_URL = "/api/clients";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ClientsRepository clientsRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Clients clients;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Clients createEntity(EntityManager em) {
        Clients clients = new Clients()
            .nom(DEFAULT_NOM)
            .prenom(DEFAULT_PRENOM)
            .adresse(DEFAULT_ADRESSE)
            .telephone(DEFAULT_TELEPHONE)
            .email(DEFAULT_EMAIL)
            .idUser(DEFAULT_ID_USER);
        return clients;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Clients createUpdatedEntity(EntityManager em) {
        Clients clients = new Clients()
            .nom(UPDATED_NOM)
            .prenom(UPDATED_PRENOM)
            .adresse(UPDATED_ADRESSE)
            .telephone(UPDATED_TELEPHONE)
            .email(UPDATED_EMAIL)
            .idUser(UPDATED_ID_USER);
        return clients;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Clients.class).block();
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
        clients = createEntity(em);
    }

    @Test
    void createClients() throws Exception {
        int databaseSizeBeforeCreate = clientsRepository.findAll().collectList().block().size();
        // Create the Clients
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(clients))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Clients in the database
        List<Clients> clientsList = clientsRepository.findAll().collectList().block();
        assertThat(clientsList).hasSize(databaseSizeBeforeCreate + 1);
        Clients testClients = clientsList.get(clientsList.size() - 1);
        assertThat(testClients.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testClients.getPrenom()).isEqualTo(DEFAULT_PRENOM);
        assertThat(testClients.getAdresse()).isEqualTo(DEFAULT_ADRESSE);
        assertThat(testClients.getTelephone()).isEqualTo(DEFAULT_TELEPHONE);
        assertThat(testClients.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testClients.getIdUser()).isEqualTo(DEFAULT_ID_USER);
    }

    @Test
    void createClientsWithExistingId() throws Exception {
        // Create the Clients with an existing ID
        clients.setId(1L);

        int databaseSizeBeforeCreate = clientsRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(clients))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Clients in the database
        List<Clients> clientsList = clientsRepository.findAll().collectList().block();
        assertThat(clientsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllClientsAsStream() {
        // Initialize the database
        clientsRepository.save(clients).block();

        List<Clients> clientsList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Clients.class)
            .getResponseBody()
            .filter(clients::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(clientsList).isNotNull();
        assertThat(clientsList).hasSize(1);
        Clients testClients = clientsList.get(0);
        assertThat(testClients.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testClients.getPrenom()).isEqualTo(DEFAULT_PRENOM);
        assertThat(testClients.getAdresse()).isEqualTo(DEFAULT_ADRESSE);
        assertThat(testClients.getTelephone()).isEqualTo(DEFAULT_TELEPHONE);
        assertThat(testClients.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testClients.getIdUser()).isEqualTo(DEFAULT_ID_USER);
    }

    @Test
    void getAllClients() {
        // Initialize the database
        clientsRepository.save(clients).block();

        // Get all the clientsList
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
            .value(hasItem(clients.getId().intValue()))
            .jsonPath("$.[*].nom")
            .value(hasItem(DEFAULT_NOM))
            .jsonPath("$.[*].prenom")
            .value(hasItem(DEFAULT_PRENOM))
            .jsonPath("$.[*].adresse")
            .value(hasItem(DEFAULT_ADRESSE))
            .jsonPath("$.[*].telephone")
            .value(hasItem(DEFAULT_TELEPHONE))
            .jsonPath("$.[*].email")
            .value(hasItem(DEFAULT_EMAIL))
            .jsonPath("$.[*].idUser")
            .value(hasItem(DEFAULT_ID_USER.intValue()));
    }

    @Test
    void getClients() {
        // Initialize the database
        clientsRepository.save(clients).block();

        // Get the clients
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, clients.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(clients.getId().intValue()))
            .jsonPath("$.nom")
            .value(is(DEFAULT_NOM))
            .jsonPath("$.prenom")
            .value(is(DEFAULT_PRENOM))
            .jsonPath("$.adresse")
            .value(is(DEFAULT_ADRESSE))
            .jsonPath("$.telephone")
            .value(is(DEFAULT_TELEPHONE))
            .jsonPath("$.email")
            .value(is(DEFAULT_EMAIL))
            .jsonPath("$.idUser")
            .value(is(DEFAULT_ID_USER.intValue()));
    }

    @Test
    void getNonExistingClients() {
        // Get the clients
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingClients() throws Exception {
        // Initialize the database
        clientsRepository.save(clients).block();

        int databaseSizeBeforeUpdate = clientsRepository.findAll().collectList().block().size();

        // Update the clients
        Clients updatedClients = clientsRepository.findById(clients.getId()).block();
        updatedClients
            .nom(UPDATED_NOM)
            .prenom(UPDATED_PRENOM)
            .adresse(UPDATED_ADRESSE)
            .telephone(UPDATED_TELEPHONE)
            .email(UPDATED_EMAIL)
            .idUser(UPDATED_ID_USER);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedClients.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedClients))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Clients in the database
        List<Clients> clientsList = clientsRepository.findAll().collectList().block();
        assertThat(clientsList).hasSize(databaseSizeBeforeUpdate);
        Clients testClients = clientsList.get(clientsList.size() - 1);
        assertThat(testClients.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testClients.getPrenom()).isEqualTo(UPDATED_PRENOM);
        assertThat(testClients.getAdresse()).isEqualTo(UPDATED_ADRESSE);
        assertThat(testClients.getTelephone()).isEqualTo(UPDATED_TELEPHONE);
        assertThat(testClients.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testClients.getIdUser()).isEqualTo(UPDATED_ID_USER);
    }

    @Test
    void putNonExistingClients() throws Exception {
        int databaseSizeBeforeUpdate = clientsRepository.findAll().collectList().block().size();
        clients.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, clients.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(clients))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Clients in the database
        List<Clients> clientsList = clientsRepository.findAll().collectList().block();
        assertThat(clientsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchClients() throws Exception {
        int databaseSizeBeforeUpdate = clientsRepository.findAll().collectList().block().size();
        clients.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(clients))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Clients in the database
        List<Clients> clientsList = clientsRepository.findAll().collectList().block();
        assertThat(clientsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamClients() throws Exception {
        int databaseSizeBeforeUpdate = clientsRepository.findAll().collectList().block().size();
        clients.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(clients))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Clients in the database
        List<Clients> clientsList = clientsRepository.findAll().collectList().block();
        assertThat(clientsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateClientsWithPatch() throws Exception {
        // Initialize the database
        clientsRepository.save(clients).block();

        int databaseSizeBeforeUpdate = clientsRepository.findAll().collectList().block().size();

        // Update the clients using partial update
        Clients partialUpdatedClients = new Clients();
        partialUpdatedClients.setId(clients.getId());

        partialUpdatedClients.prenom(UPDATED_PRENOM).telephone(UPDATED_TELEPHONE).email(UPDATED_EMAIL).idUser(UPDATED_ID_USER);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedClients.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedClients))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Clients in the database
        List<Clients> clientsList = clientsRepository.findAll().collectList().block();
        assertThat(clientsList).hasSize(databaseSizeBeforeUpdate);
        Clients testClients = clientsList.get(clientsList.size() - 1);
        assertThat(testClients.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testClients.getPrenom()).isEqualTo(UPDATED_PRENOM);
        assertThat(testClients.getAdresse()).isEqualTo(DEFAULT_ADRESSE);
        assertThat(testClients.getTelephone()).isEqualTo(UPDATED_TELEPHONE);
        assertThat(testClients.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testClients.getIdUser()).isEqualTo(UPDATED_ID_USER);
    }

    @Test
    void fullUpdateClientsWithPatch() throws Exception {
        // Initialize the database
        clientsRepository.save(clients).block();

        int databaseSizeBeforeUpdate = clientsRepository.findAll().collectList().block().size();

        // Update the clients using partial update
        Clients partialUpdatedClients = new Clients();
        partialUpdatedClients.setId(clients.getId());

        partialUpdatedClients
            .nom(UPDATED_NOM)
            .prenom(UPDATED_PRENOM)
            .adresse(UPDATED_ADRESSE)
            .telephone(UPDATED_TELEPHONE)
            .email(UPDATED_EMAIL)
            .idUser(UPDATED_ID_USER);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedClients.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedClients))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Clients in the database
        List<Clients> clientsList = clientsRepository.findAll().collectList().block();
        assertThat(clientsList).hasSize(databaseSizeBeforeUpdate);
        Clients testClients = clientsList.get(clientsList.size() - 1);
        assertThat(testClients.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testClients.getPrenom()).isEqualTo(UPDATED_PRENOM);
        assertThat(testClients.getAdresse()).isEqualTo(UPDATED_ADRESSE);
        assertThat(testClients.getTelephone()).isEqualTo(UPDATED_TELEPHONE);
        assertThat(testClients.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testClients.getIdUser()).isEqualTo(UPDATED_ID_USER);
    }

    @Test
    void patchNonExistingClients() throws Exception {
        int databaseSizeBeforeUpdate = clientsRepository.findAll().collectList().block().size();
        clients.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, clients.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(clients))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Clients in the database
        List<Clients> clientsList = clientsRepository.findAll().collectList().block();
        assertThat(clientsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchClients() throws Exception {
        int databaseSizeBeforeUpdate = clientsRepository.findAll().collectList().block().size();
        clients.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(clients))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Clients in the database
        List<Clients> clientsList = clientsRepository.findAll().collectList().block();
        assertThat(clientsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamClients() throws Exception {
        int databaseSizeBeforeUpdate = clientsRepository.findAll().collectList().block().size();
        clients.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(clients))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Clients in the database
        List<Clients> clientsList = clientsRepository.findAll().collectList().block();
        assertThat(clientsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteClients() {
        // Initialize the database
        clientsRepository.save(clients).block();

        int databaseSizeBeforeDelete = clientsRepository.findAll().collectList().block().size();

        // Delete the clients
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, clients.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Clients> clientsList = clientsRepository.findAll().collectList().block();
        assertThat(clientsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
