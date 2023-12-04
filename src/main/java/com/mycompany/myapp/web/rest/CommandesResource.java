package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Commandes;
import com.mycompany.myapp.domain.Produits;
import com.mycompany.myapp.repository.CommandesRepository;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Commandes}.
 */
@RestController
@RequestMapping("/api/commandes")
@Transactional
public class CommandesResource {

    private final Logger log = LoggerFactory.getLogger(CommandesResource.class);

    private static final String ENTITY_NAME = "commandes";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CommandesRepository commandesRepository;

    public CommandesResource(CommandesRepository commandesRepository) {
        this.commandesRepository = commandesRepository;
    }

    /**
     * {@code POST  /commandes} : Create a new commandes.
     *
     * @param commandes the commandes to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new commandes, or with status {@code 400 (Bad Request)} if the commandes has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Commandes> createCommandes(@RequestBody Commandes commandes) throws URISyntaxException {
        log.debug("REST request to save Commandes : {}", commandes);
        if (commandes.getId() != null) {
            throw new BadRequestAlertException("A new commandes cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Commandes result = commandesRepository.save(commandes);
        return ResponseEntity
            .created(new URI("/api/commandes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /commandes/:id} : Updates an existing commandes.
     *
     * @param id the id of the commandes to save.
     * @param commandes the commandes to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated commandes,
     * or with status {@code 400 (Bad Request)} if the commandes is not valid,
     * or with status {@code 500 (Internal Server Error)} if the commandes couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Commandes> updateCommandes(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Commandes commandes
    ) throws URISyntaxException {
        log.debug("REST request to update Commandes : {}, {}", id, commandes);
        if (commandes.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, commandes.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!commandesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Commandes result = commandesRepository.save(commandes);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, commandes.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /commandes/:id} : Partial updates given fields of an existing commandes, field will ignore if it is null
     *
     * @param id the id of the commandes to save.
     * @param commandes the commandes to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated commandes,
     * or with status {@code 400 (Bad Request)} if the commandes is not valid,
     * or with status {@code 404 (Not Found)} if the commandes is not found,
     * or with status {@code 500 (Internal Server Error)} if the commandes couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Commandes> partialUpdateCommandes(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Commandes commandes
    ) throws URISyntaxException {
        log.debug("REST request to partial update Commandes partially : {}, {}", id, commandes);
        if (commandes.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, commandes.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!commandesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Commandes> result = commandesRepository
            .findById(commandes.getId())
            .map(existingCommandes -> {
                if (commandes.getDateCommande() != null) {
                    existingCommandes.setDateCommande(commandes.getDateCommande());
                }

                return existingCommandes;
            })
            .map(commandesRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, commandes.getId().toString())
        );
    }

    /**
     * {@code GET  /commandes} : get all the commandes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of commandes in body.
     */
    @GetMapping("")
    public List<Commandes> getAllCommandes() {
        log.debug("REST request to get all Commandes");
        return commandesRepository.findAll();
    }

    /**
     * {@code GET  /commandes/:id} : get the "id" commandes.
     *
     * @param id the id of the commandes to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the commandes, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Commandes> getCommandes(@PathVariable Long id) {
        log.debug("REST request to get Commandes : {}", id);
        Optional<Commandes> commandes = commandesRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(commandes);
    }

    // ************
    /**
     * {@code GET  /commandes/byclientId/:id} : get the "id" commandes.
     *
     * @param id the id of the commandes to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the commandes, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/byclientId/{id}")
    public List<Commandes> getAllCommandesByClientId(@PathVariable Long id) {
        log.debug("REST request to get Produits : {}", id);
        return commandesRepository.findByClientId(id);
    }

    /**
     * {@code GET  /commandes/details} : get all the commandes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of commandes in body.
     */
    @GetMapping("/details")
    public List<Commandes> getAllCommandesDetails() {
        log.debug("REST request to get all Commandes");
        return commandesRepository.findAllDetail();
    }

    /**
     * {@code GET  /commandes/sortByProduit} : get all the commandes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of commandes in body.
     */
    @GetMapping("/sortByProduit")
    public List<Produits> getProduitPlusPresent() {
        log.debug("REST request to get all Commandes");
        return commandesRepository.findProduitPlusPresent();
    }

    /**
     * {@code DELETE  /commandes/:id} : delete the "id" commandes.
     *
     * @param id the id of the commandes to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommandes(@PathVariable Long id) {
        log.debug("REST request to delete Commandes : {}", id);
        commandesRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
