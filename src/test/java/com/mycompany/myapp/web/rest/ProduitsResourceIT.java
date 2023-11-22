package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Produits;
import com.mycompany.myapp.repository.ProduitsRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ProduitsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
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
    private MockMvc restProduitsMockMvc;

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

    @BeforeEach
    public void initTest() {
        produits = createEntity(em);
    }

    @Test
    @Transactional
    void createProduits() throws Exception {
        int databaseSizeBeforeCreate = produitsRepository.findAll().size();
        // Create the Produits
        restProduitsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(produits)))
            .andExpect(status().isCreated());

        // Validate the Produits in the database
        List<Produits> produitsList = produitsRepository.findAll();
        assertThat(produitsList).hasSize(databaseSizeBeforeCreate + 1);
        Produits testProduits = produitsList.get(produitsList.size() - 1);
        assertThat(testProduits.getNomProduit()).isEqualTo(DEFAULT_NOM_PRODUIT);
        assertThat(testProduits.getDescriptionProduit()).isEqualTo(DEFAULT_DESCRIPTION_PRODUIT);
        assertThat(testProduits.getPrixProduit()).isEqualTo(DEFAULT_PRIX_PRODUIT);
        assertThat(testProduits.getImageProduit()).isEqualTo(DEFAULT_IMAGE_PRODUIT);
    }

    @Test
    @Transactional
    void createProduitsWithExistingId() throws Exception {
        // Create the Produits with an existing ID
        produits.setId(1L);

        int databaseSizeBeforeCreate = produitsRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProduitsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(produits)))
            .andExpect(status().isBadRequest());

        // Validate the Produits in the database
        List<Produits> produitsList = produitsRepository.findAll();
        assertThat(produitsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllProduits() throws Exception {
        // Initialize the database
        produitsRepository.saveAndFlush(produits);

        // Get all the produitsList
        restProduitsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(produits.getId().intValue())))
            .andExpect(jsonPath("$.[*].nomProduit").value(hasItem(DEFAULT_NOM_PRODUIT)))
            .andExpect(jsonPath("$.[*].descriptionProduit").value(hasItem(DEFAULT_DESCRIPTION_PRODUIT)))
            .andExpect(jsonPath("$.[*].prixProduit").value(hasItem(DEFAULT_PRIX_PRODUIT.intValue())))
            .andExpect(jsonPath("$.[*].imageProduit").value(hasItem(DEFAULT_IMAGE_PRODUIT.toString())));
    }

    @Test
    @Transactional
    void getProduits() throws Exception {
        // Initialize the database
        produitsRepository.saveAndFlush(produits);

        // Get the produits
        restProduitsMockMvc
            .perform(get(ENTITY_API_URL_ID, produits.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(produits.getId().intValue()))
            .andExpect(jsonPath("$.nomProduit").value(DEFAULT_NOM_PRODUIT))
            .andExpect(jsonPath("$.descriptionProduit").value(DEFAULT_DESCRIPTION_PRODUIT))
            .andExpect(jsonPath("$.prixProduit").value(DEFAULT_PRIX_PRODUIT.intValue()))
            .andExpect(jsonPath("$.imageProduit").value(DEFAULT_IMAGE_PRODUIT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingProduits() throws Exception {
        // Get the produits
        restProduitsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProduits() throws Exception {
        // Initialize the database
        produitsRepository.saveAndFlush(produits);

        int databaseSizeBeforeUpdate = produitsRepository.findAll().size();

        // Update the produits
        Produits updatedProduits = produitsRepository.findById(produits.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedProduits are not directly saved in db
        em.detach(updatedProduits);
        updatedProduits
            .nomProduit(UPDATED_NOM_PRODUIT)
            .descriptionProduit(UPDATED_DESCRIPTION_PRODUIT)
            .prixProduit(UPDATED_PRIX_PRODUIT)
            .imageProduit(UPDATED_IMAGE_PRODUIT);

        restProduitsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedProduits.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedProduits))
            )
            .andExpect(status().isOk());

        // Validate the Produits in the database
        List<Produits> produitsList = produitsRepository.findAll();
        assertThat(produitsList).hasSize(databaseSizeBeforeUpdate);
        Produits testProduits = produitsList.get(produitsList.size() - 1);
        assertThat(testProduits.getNomProduit()).isEqualTo(UPDATED_NOM_PRODUIT);
        assertThat(testProduits.getDescriptionProduit()).isEqualTo(UPDATED_DESCRIPTION_PRODUIT);
        assertThat(testProduits.getPrixProduit()).isEqualTo(UPDATED_PRIX_PRODUIT);
        assertThat(testProduits.getImageProduit()).isEqualTo(UPDATED_IMAGE_PRODUIT);
    }

    @Test
    @Transactional
    void putNonExistingProduits() throws Exception {
        int databaseSizeBeforeUpdate = produitsRepository.findAll().size();
        produits.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProduitsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, produits.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(produits))
            )
            .andExpect(status().isBadRequest());

        // Validate the Produits in the database
        List<Produits> produitsList = produitsRepository.findAll();
        assertThat(produitsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProduits() throws Exception {
        int databaseSizeBeforeUpdate = produitsRepository.findAll().size();
        produits.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProduitsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(produits))
            )
            .andExpect(status().isBadRequest());

        // Validate the Produits in the database
        List<Produits> produitsList = produitsRepository.findAll();
        assertThat(produitsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProduits() throws Exception {
        int databaseSizeBeforeUpdate = produitsRepository.findAll().size();
        produits.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProduitsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(produits)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Produits in the database
        List<Produits> produitsList = produitsRepository.findAll();
        assertThat(produitsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProduitsWithPatch() throws Exception {
        // Initialize the database
        produitsRepository.saveAndFlush(produits);

        int databaseSizeBeforeUpdate = produitsRepository.findAll().size();

        // Update the produits using partial update
        Produits partialUpdatedProduits = new Produits();
        partialUpdatedProduits.setId(produits.getId());

        partialUpdatedProduits.prixProduit(UPDATED_PRIX_PRODUIT).imageProduit(UPDATED_IMAGE_PRODUIT);

        restProduitsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProduits.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProduits))
            )
            .andExpect(status().isOk());

        // Validate the Produits in the database
        List<Produits> produitsList = produitsRepository.findAll();
        assertThat(produitsList).hasSize(databaseSizeBeforeUpdate);
        Produits testProduits = produitsList.get(produitsList.size() - 1);
        assertThat(testProduits.getNomProduit()).isEqualTo(DEFAULT_NOM_PRODUIT);
        assertThat(testProduits.getDescriptionProduit()).isEqualTo(DEFAULT_DESCRIPTION_PRODUIT);
        assertThat(testProduits.getPrixProduit()).isEqualTo(UPDATED_PRIX_PRODUIT);
        assertThat(testProduits.getImageProduit()).isEqualTo(UPDATED_IMAGE_PRODUIT);
    }

    @Test
    @Transactional
    void fullUpdateProduitsWithPatch() throws Exception {
        // Initialize the database
        produitsRepository.saveAndFlush(produits);

        int databaseSizeBeforeUpdate = produitsRepository.findAll().size();

        // Update the produits using partial update
        Produits partialUpdatedProduits = new Produits();
        partialUpdatedProduits.setId(produits.getId());

        partialUpdatedProduits
            .nomProduit(UPDATED_NOM_PRODUIT)
            .descriptionProduit(UPDATED_DESCRIPTION_PRODUIT)
            .prixProduit(UPDATED_PRIX_PRODUIT)
            .imageProduit(UPDATED_IMAGE_PRODUIT);

        restProduitsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProduits.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProduits))
            )
            .andExpect(status().isOk());

        // Validate the Produits in the database
        List<Produits> produitsList = produitsRepository.findAll();
        assertThat(produitsList).hasSize(databaseSizeBeforeUpdate);
        Produits testProduits = produitsList.get(produitsList.size() - 1);
        assertThat(testProduits.getNomProduit()).isEqualTo(UPDATED_NOM_PRODUIT);
        assertThat(testProduits.getDescriptionProduit()).isEqualTo(UPDATED_DESCRIPTION_PRODUIT);
        assertThat(testProduits.getPrixProduit()).isEqualTo(UPDATED_PRIX_PRODUIT);
        assertThat(testProduits.getImageProduit()).isEqualTo(UPDATED_IMAGE_PRODUIT);
    }

    @Test
    @Transactional
    void patchNonExistingProduits() throws Exception {
        int databaseSizeBeforeUpdate = produitsRepository.findAll().size();
        produits.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProduitsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, produits.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(produits))
            )
            .andExpect(status().isBadRequest());

        // Validate the Produits in the database
        List<Produits> produitsList = produitsRepository.findAll();
        assertThat(produitsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProduits() throws Exception {
        int databaseSizeBeforeUpdate = produitsRepository.findAll().size();
        produits.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProduitsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(produits))
            )
            .andExpect(status().isBadRequest());

        // Validate the Produits in the database
        List<Produits> produitsList = produitsRepository.findAll();
        assertThat(produitsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProduits() throws Exception {
        int databaseSizeBeforeUpdate = produitsRepository.findAll().size();
        produits.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProduitsMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(produits)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Produits in the database
        List<Produits> produitsList = produitsRepository.findAll();
        assertThat(produitsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProduits() throws Exception {
        // Initialize the database
        produitsRepository.saveAndFlush(produits);

        int databaseSizeBeforeDelete = produitsRepository.findAll().size();

        // Delete the produits
        restProduitsMockMvc
            .perform(delete(ENTITY_API_URL_ID, produits.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Produits> produitsList = produitsRepository.findAll();
        assertThat(produitsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
