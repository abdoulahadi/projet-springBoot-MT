package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Produits;
import com.mycompany.myapp.repository.ProduitsRepository;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Produits}.
 */
@RestController
@RequestMapping("/api/produits")
@Transactional
public class ProduitsResource {

    private final Logger log = LoggerFactory.getLogger(ProduitsResource.class);

    private static final String ENTITY_NAME = "produits";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProduitsRepository produitsRepository;

    public ProduitsResource(ProduitsRepository produitsRepository) {
        this.produitsRepository = produitsRepository;
    }

    /**
     * {@code POST  /produits} : Create a new produits.
     *
     * @param produits the produits to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new produits, or with status {@code 400 (Bad Request)} if the produits has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<Produits>> createProduits(@RequestBody Produits produits) throws URISyntaxException {
        log.debug("REST request to save Produits : {}", produits);
        if (produits.getId() != null) {
            throw new BadRequestAlertException("A new produits cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return produitsRepository
            .save(produits)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/produits/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /produits/:id} : Updates an existing produits.
     *
     * @param id the id of the produits to save.
     * @param produits the produits to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated produits,
     * or with status {@code 400 (Bad Request)} if the produits is not valid,
     * or with status {@code 500 (Internal Server Error)} if the produits couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Produits>> updateProduits(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Produits produits
    ) throws URISyntaxException {
        log.debug("REST request to update Produits : {}, {}", id, produits);
        if (produits.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, produits.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return produitsRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return produitsRepository
                    .save(produits)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /produits/:id} : Partial updates given fields of an existing produits, field will ignore if it is null
     *
     * @param id the id of the produits to save.
     * @param produits the produits to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated produits,
     * or with status {@code 400 (Bad Request)} if the produits is not valid,
     * or with status {@code 404 (Not Found)} if the produits is not found,
     * or with status {@code 500 (Internal Server Error)} if the produits couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Produits>> partialUpdateProduits(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Produits produits
    ) throws URISyntaxException {
        log.debug("REST request to partial update Produits partially : {}, {}", id, produits);
        if (produits.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, produits.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return produitsRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Produits> result = produitsRepository
                    .findById(produits.getId())
                    .map(existingProduits -> {
                        if (produits.getIdProduit() != null) {
                            existingProduits.setIdProduit(produits.getIdProduit());
                        }
                        if (produits.getNomProduit() != null) {
                            existingProduits.setNomProduit(produits.getNomProduit());
                        }
                        if (produits.getDescriptionProduit() != null) {
                            existingProduits.setDescriptionProduit(produits.getDescriptionProduit());
                        }
                        if (produits.getPrixProduit() != null) {
                            existingProduits.setPrixProduit(produits.getPrixProduit());
                        }
                        if (produits.getImageProduit() != null) {
                            existingProduits.setImageProduit(produits.getImageProduit());
                        }

                        return existingProduits;
                    })
                    .flatMap(produitsRepository::save);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /produits} : get all the produits.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of produits in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<Produits>> getAllProduits(@RequestParam(required = false, defaultValue = "true") boolean eagerload) {
        log.debug("REST request to get all Produits");
        if (eagerload) {
            return produitsRepository.findAllWithEagerRelationships().collectList();
        } else {
            return produitsRepository.findAll().collectList();
        }
    }

    /**
     * {@code GET  /produits} : get all the produits as a stream.
     * @return the {@link Flux} of produits.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Produits> getAllProduitsAsStream() {
        log.debug("REST request to get all Produits as a stream");
        return produitsRepository.findAll();
    }

    /**
     * {@code GET  /produits/:id} : get the "id" produits.
     *
     * @param id the id of the produits to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the produits, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Produits>> getProduits(@PathVariable Long id) {
        log.debug("REST request to get Produits : {}", id);
        Mono<Produits> produits = produitsRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(produits);
    }

    /**
     * {@code DELETE  /produits/:id} : delete the "id" produits.
     *
     * @param id the id of the produits to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteProduits(@PathVariable Long id) {
        log.debug("REST request to delete Produits : {}", id);
        return produitsRepository
            .deleteById(id)
            .then(
                Mono.just(
                    ResponseEntity
                        .noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }
}
