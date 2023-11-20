package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Commandes;
import com.mycompany.myapp.repository.CommandesRepository;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link CommandesResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
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
    private MockMvc restCommandesMockMvc;

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

    @BeforeEach
    public void initTest() {
        commandes = createEntity(em);
    }

    @Test
    @Transactional
    void createCommandes() throws Exception {
        int databaseSizeBeforeCreate = commandesRepository.findAll().size();
        // Create the Commandes
        restCommandesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(commandes)))
            .andExpect(status().isCreated());

        // Validate the Commandes in the database
        List<Commandes> commandesList = commandesRepository.findAll();
        assertThat(commandesList).hasSize(databaseSizeBeforeCreate + 1);
        Commandes testCommandes = commandesList.get(commandesList.size() - 1);
        assertThat(testCommandes.getDateCommande()).isEqualTo(DEFAULT_DATE_COMMANDE);
    }

    @Test
    @Transactional
    void createCommandesWithExistingId() throws Exception {
        // Create the Commandes with an existing ID
        commandes.setId(1L);

        int databaseSizeBeforeCreate = commandesRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCommandesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(commandes)))
            .andExpect(status().isBadRequest());

        // Validate the Commandes in the database
        List<Commandes> commandesList = commandesRepository.findAll();
        assertThat(commandesList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCommandes() throws Exception {
        // Initialize the database
        commandesRepository.saveAndFlush(commandes);

        // Get all the commandesList
        restCommandesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(commandes.getId().intValue())))
            .andExpect(jsonPath("$.[*].dateCommande").value(hasItem(DEFAULT_DATE_COMMANDE.toString())));
    }

    @Test
    @Transactional
    void getCommandes() throws Exception {
        // Initialize the database
        commandesRepository.saveAndFlush(commandes);

        // Get the commandes
        restCommandesMockMvc
            .perform(get(ENTITY_API_URL_ID, commandes.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(commandes.getId().intValue()))
            .andExpect(jsonPath("$.dateCommande").value(DEFAULT_DATE_COMMANDE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingCommandes() throws Exception {
        // Get the commandes
        restCommandesMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCommandes() throws Exception {
        // Initialize the database
        commandesRepository.saveAndFlush(commandes);

        int databaseSizeBeforeUpdate = commandesRepository.findAll().size();

        // Update the commandes
        Commandes updatedCommandes = commandesRepository.findById(commandes.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCommandes are not directly saved in db
        em.detach(updatedCommandes);
        updatedCommandes.dateCommande(UPDATED_DATE_COMMANDE);

        restCommandesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCommandes.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedCommandes))
            )
            .andExpect(status().isOk());

        // Validate the Commandes in the database
        List<Commandes> commandesList = commandesRepository.findAll();
        assertThat(commandesList).hasSize(databaseSizeBeforeUpdate);
        Commandes testCommandes = commandesList.get(commandesList.size() - 1);
        assertThat(testCommandes.getDateCommande()).isEqualTo(UPDATED_DATE_COMMANDE);
    }

    @Test
    @Transactional
    void putNonExistingCommandes() throws Exception {
        int databaseSizeBeforeUpdate = commandesRepository.findAll().size();
        commandes.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCommandesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, commandes.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(commandes))
            )
            .andExpect(status().isBadRequest());

        // Validate the Commandes in the database
        List<Commandes> commandesList = commandesRepository.findAll();
        assertThat(commandesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCommandes() throws Exception {
        int databaseSizeBeforeUpdate = commandesRepository.findAll().size();
        commandes.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommandesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(commandes))
            )
            .andExpect(status().isBadRequest());

        // Validate the Commandes in the database
        List<Commandes> commandesList = commandesRepository.findAll();
        assertThat(commandesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCommandes() throws Exception {
        int databaseSizeBeforeUpdate = commandesRepository.findAll().size();
        commandes.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommandesMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(commandes)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Commandes in the database
        List<Commandes> commandesList = commandesRepository.findAll();
        assertThat(commandesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCommandesWithPatch() throws Exception {
        // Initialize the database
        commandesRepository.saveAndFlush(commandes);

        int databaseSizeBeforeUpdate = commandesRepository.findAll().size();

        // Update the commandes using partial update
        Commandes partialUpdatedCommandes = new Commandes();
        partialUpdatedCommandes.setId(commandes.getId());

        restCommandesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCommandes.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCommandes))
            )
            .andExpect(status().isOk());

        // Validate the Commandes in the database
        List<Commandes> commandesList = commandesRepository.findAll();
        assertThat(commandesList).hasSize(databaseSizeBeforeUpdate);
        Commandes testCommandes = commandesList.get(commandesList.size() - 1);
        assertThat(testCommandes.getDateCommande()).isEqualTo(DEFAULT_DATE_COMMANDE);
    }

    @Test
    @Transactional
    void fullUpdateCommandesWithPatch() throws Exception {
        // Initialize the database
        commandesRepository.saveAndFlush(commandes);

        int databaseSizeBeforeUpdate = commandesRepository.findAll().size();

        // Update the commandes using partial update
        Commandes partialUpdatedCommandes = new Commandes();
        partialUpdatedCommandes.setId(commandes.getId());

        partialUpdatedCommandes.dateCommande(UPDATED_DATE_COMMANDE);

        restCommandesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCommandes.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCommandes))
            )
            .andExpect(status().isOk());

        // Validate the Commandes in the database
        List<Commandes> commandesList = commandesRepository.findAll();
        assertThat(commandesList).hasSize(databaseSizeBeforeUpdate);
        Commandes testCommandes = commandesList.get(commandesList.size() - 1);
        assertThat(testCommandes.getDateCommande()).isEqualTo(UPDATED_DATE_COMMANDE);
    }

    @Test
    @Transactional
    void patchNonExistingCommandes() throws Exception {
        int databaseSizeBeforeUpdate = commandesRepository.findAll().size();
        commandes.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCommandesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, commandes.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(commandes))
            )
            .andExpect(status().isBadRequest());

        // Validate the Commandes in the database
        List<Commandes> commandesList = commandesRepository.findAll();
        assertThat(commandesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCommandes() throws Exception {
        int databaseSizeBeforeUpdate = commandesRepository.findAll().size();
        commandes.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommandesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(commandes))
            )
            .andExpect(status().isBadRequest());

        // Validate the Commandes in the database
        List<Commandes> commandesList = commandesRepository.findAll();
        assertThat(commandesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCommandes() throws Exception {
        int databaseSizeBeforeUpdate = commandesRepository.findAll().size();
        commandes.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommandesMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(commandes))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Commandes in the database
        List<Commandes> commandesList = commandesRepository.findAll();
        assertThat(commandesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCommandes() throws Exception {
        // Initialize the database
        commandesRepository.saveAndFlush(commandes);

        int databaseSizeBeforeDelete = commandesRepository.findAll().size();

        // Delete the commandes
        restCommandesMockMvc
            .perform(delete(ENTITY_API_URL_ID, commandes.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Commandes> commandesList = commandesRepository.findAll();
        assertThat(commandesList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
