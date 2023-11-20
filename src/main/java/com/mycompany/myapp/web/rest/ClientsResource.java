package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Clients;
import com.mycompany.myapp.repository.ClientsRepository;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Clients}.
 */
@RestController
@RequestMapping("/api/clients")
@Transactional
public class ClientsResource {

    private final Logger log = LoggerFactory.getLogger(ClientsResource.class);

    private static final String ENTITY_NAME = "clients";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ClientsRepository clientsRepository;

    public ClientsResource(ClientsRepository clientsRepository) {
        this.clientsRepository = clientsRepository;
    }

    /**
     * {@code POST  /clients} : Create a new clients.
     *
     * @param clients the clients to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new clients, or with status {@code 400 (Bad Request)} if the clients has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<Clients>> createClients(@RequestBody Clients clients) throws URISyntaxException {
        log.debug("REST request to save Clients : {}", clients);
        if (clients.getId() != null) {
            throw new BadRequestAlertException("A new clients cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return clientsRepository
            .save(clients)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/clients/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /clients/:id} : Updates an existing clients.
     *
     * @param id the id of the clients to save.
     * @param clients the clients to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clients,
     * or with status {@code 400 (Bad Request)} if the clients is not valid,
     * or with status {@code 500 (Internal Server Error)} if the clients couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Clients>> updateClients(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Clients clients
    ) throws URISyntaxException {
        log.debug("REST request to update Clients : {}, {}", id, clients);
        if (clients.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, clients.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return clientsRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return clientsRepository
                    .save(clients)
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
     * {@code PATCH  /clients/:id} : Partial updates given fields of an existing clients, field will ignore if it is null
     *
     * @param id the id of the clients to save.
     * @param clients the clients to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clients,
     * or with status {@code 400 (Bad Request)} if the clients is not valid,
     * or with status {@code 404 (Not Found)} if the clients is not found,
     * or with status {@code 500 (Internal Server Error)} if the clients couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Clients>> partialUpdateClients(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Clients clients
    ) throws URISyntaxException {
        log.debug("REST request to partial update Clients partially : {}, {}", id, clients);
        if (clients.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, clients.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return clientsRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Clients> result = clientsRepository
                    .findById(clients.getId())
                    .map(existingClients -> {
                        if (clients.getNom() != null) {
                            existingClients.setNom(clients.getNom());
                        }
                        if (clients.getPrenom() != null) {
                            existingClients.setPrenom(clients.getPrenom());
                        }
                        if (clients.getAdresse() != null) {
                            existingClients.setAdresse(clients.getAdresse());
                        }
                        if (clients.getTelephone() != null) {
                            existingClients.setTelephone(clients.getTelephone());
                        }
                        if (clients.getEmail() != null) {
                            existingClients.setEmail(clients.getEmail());
                        }
                        if (clients.getIdUser() != null) {
                            existingClients.setIdUser(clients.getIdUser());
                        }

                        return existingClients;
                    })
                    .flatMap(clientsRepository::save);

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
     * {@code GET  /clients} : get all the clients.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of clients in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<Clients>> getAllClients() {
        log.debug("REST request to get all Clients");
        return clientsRepository.findAll().collectList();
    }

    /**
     * {@code GET  /clients} : get all the clients as a stream.
     * @return the {@link Flux} of clients.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Clients> getAllClientsAsStream() {
        log.debug("REST request to get all Clients as a stream");
        return clientsRepository.findAll();
    }

    /**
     * {@code GET  /clients/:id} : get the "id" clients.
     *
     * @param id the id of the clients to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the clients, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Clients>> getClients(@PathVariable Long id) {
        log.debug("REST request to get Clients : {}", id);
        Mono<Clients> clients = clientsRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(clients);
    }

    /**
     * {@code DELETE  /clients/:id} : delete the "id" clients.
     *
     * @param id the id of the clients to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteClients(@PathVariable Long id) {
        log.debug("REST request to delete Clients : {}", id);
        return clientsRepository
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
