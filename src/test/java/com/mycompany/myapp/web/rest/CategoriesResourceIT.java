package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Categories;
import com.mycompany.myapp.repository.CategoriesRepository;
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
 * Integration tests for the {@link CategoriesResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class CategoriesResourceIT {

    private static final String DEFAULT_NOM_CATEGORIE = "AAAAAAAAAA";
    private static final String UPDATED_NOM_CATEGORIE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/categories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CategoriesRepository categoriesRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Categories categories;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Categories createEntity(EntityManager em) {
        Categories categories = new Categories().nomCategorie(DEFAULT_NOM_CATEGORIE);
        return categories;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Categories createUpdatedEntity(EntityManager em) {
        Categories categories = new Categories().nomCategorie(UPDATED_NOM_CATEGORIE);
        return categories;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Categories.class).block();
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
        categories = createEntity(em);
    }

    @Test
    void createCategories() throws Exception {
        int databaseSizeBeforeCreate = categoriesRepository.findAll().collectList().block().size();
        // Create the Categories
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(categories))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Categories in the database
        List<Categories> categoriesList = categoriesRepository.findAll().collectList().block();
        assertThat(categoriesList).hasSize(databaseSizeBeforeCreate + 1);
        Categories testCategories = categoriesList.get(categoriesList.size() - 1);
        assertThat(testCategories.getNomCategorie()).isEqualTo(DEFAULT_NOM_CATEGORIE);
    }

    @Test
    void createCategoriesWithExistingId() throws Exception {
        // Create the Categories with an existing ID
        categories.setId(1L);

        int databaseSizeBeforeCreate = categoriesRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(categories))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Categories in the database
        List<Categories> categoriesList = categoriesRepository.findAll().collectList().block();
        assertThat(categoriesList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllCategoriesAsStream() {
        // Initialize the database
        categoriesRepository.save(categories).block();

        List<Categories> categoriesList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Categories.class)
            .getResponseBody()
            .filter(categories::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(categoriesList).isNotNull();
        assertThat(categoriesList).hasSize(1);
        Categories testCategories = categoriesList.get(0);
        assertThat(testCategories.getNomCategorie()).isEqualTo(DEFAULT_NOM_CATEGORIE);
    }

    @Test
    void getAllCategories() {
        // Initialize the database
        categoriesRepository.save(categories).block();

        // Get all the categoriesList
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
            .value(hasItem(categories.getId().intValue()))
            .jsonPath("$.[*].nomCategorie")
            .value(hasItem(DEFAULT_NOM_CATEGORIE));
    }

    @Test
    void getCategories() {
        // Initialize the database
        categoriesRepository.save(categories).block();

        // Get the categories
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, categories.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(categories.getId().intValue()))
            .jsonPath("$.nomCategorie")
            .value(is(DEFAULT_NOM_CATEGORIE));
    }

    @Test
    void getNonExistingCategories() {
        // Get the categories
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingCategories() throws Exception {
        // Initialize the database
        categoriesRepository.save(categories).block();

        int databaseSizeBeforeUpdate = categoriesRepository.findAll().collectList().block().size();

        // Update the categories
        Categories updatedCategories = categoriesRepository.findById(categories.getId()).block();
        updatedCategories.nomCategorie(UPDATED_NOM_CATEGORIE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedCategories.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedCategories))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Categories in the database
        List<Categories> categoriesList = categoriesRepository.findAll().collectList().block();
        assertThat(categoriesList).hasSize(databaseSizeBeforeUpdate);
        Categories testCategories = categoriesList.get(categoriesList.size() - 1);
        assertThat(testCategories.getNomCategorie()).isEqualTo(UPDATED_NOM_CATEGORIE);
    }

    @Test
    void putNonExistingCategories() throws Exception {
        int databaseSizeBeforeUpdate = categoriesRepository.findAll().collectList().block().size();
        categories.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, categories.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(categories))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Categories in the database
        List<Categories> categoriesList = categoriesRepository.findAll().collectList().block();
        assertThat(categoriesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCategories() throws Exception {
        int databaseSizeBeforeUpdate = categoriesRepository.findAll().collectList().block().size();
        categories.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(categories))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Categories in the database
        List<Categories> categoriesList = categoriesRepository.findAll().collectList().block();
        assertThat(categoriesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCategories() throws Exception {
        int databaseSizeBeforeUpdate = categoriesRepository.findAll().collectList().block().size();
        categories.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(categories))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Categories in the database
        List<Categories> categoriesList = categoriesRepository.findAll().collectList().block();
        assertThat(categoriesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCategoriesWithPatch() throws Exception {
        // Initialize the database
        categoriesRepository.save(categories).block();

        int databaseSizeBeforeUpdate = categoriesRepository.findAll().collectList().block().size();

        // Update the categories using partial update
        Categories partialUpdatedCategories = new Categories();
        partialUpdatedCategories.setId(categories.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCategories.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCategories))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Categories in the database
        List<Categories> categoriesList = categoriesRepository.findAll().collectList().block();
        assertThat(categoriesList).hasSize(databaseSizeBeforeUpdate);
        Categories testCategories = categoriesList.get(categoriesList.size() - 1);
        assertThat(testCategories.getNomCategorie()).isEqualTo(DEFAULT_NOM_CATEGORIE);
    }

    @Test
    void fullUpdateCategoriesWithPatch() throws Exception {
        // Initialize the database
        categoriesRepository.save(categories).block();

        int databaseSizeBeforeUpdate = categoriesRepository.findAll().collectList().block().size();

        // Update the categories using partial update
        Categories partialUpdatedCategories = new Categories();
        partialUpdatedCategories.setId(categories.getId());

        partialUpdatedCategories.nomCategorie(UPDATED_NOM_CATEGORIE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCategories.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCategories))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Categories in the database
        List<Categories> categoriesList = categoriesRepository.findAll().collectList().block();
        assertThat(categoriesList).hasSize(databaseSizeBeforeUpdate);
        Categories testCategories = categoriesList.get(categoriesList.size() - 1);
        assertThat(testCategories.getNomCategorie()).isEqualTo(UPDATED_NOM_CATEGORIE);
    }

    @Test
    void patchNonExistingCategories() throws Exception {
        int databaseSizeBeforeUpdate = categoriesRepository.findAll().collectList().block().size();
        categories.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, categories.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(categories))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Categories in the database
        List<Categories> categoriesList = categoriesRepository.findAll().collectList().block();
        assertThat(categoriesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCategories() throws Exception {
        int databaseSizeBeforeUpdate = categoriesRepository.findAll().collectList().block().size();
        categories.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(categories))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Categories in the database
        List<Categories> categoriesList = categoriesRepository.findAll().collectList().block();
        assertThat(categoriesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCategories() throws Exception {
        int databaseSizeBeforeUpdate = categoriesRepository.findAll().collectList().block().size();
        categories.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(categories))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Categories in the database
        List<Categories> categoriesList = categoriesRepository.findAll().collectList().block();
        assertThat(categoriesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCategories() {
        // Initialize the database
        categoriesRepository.save(categories).block();

        int databaseSizeBeforeDelete = categoriesRepository.findAll().collectList().block().size();

        // Delete the categories
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, categories.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Categories> categoriesList = categoriesRepository.findAll().collectList().block();
        assertThat(categoriesList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
