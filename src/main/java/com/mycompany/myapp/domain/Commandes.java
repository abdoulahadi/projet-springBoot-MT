package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Commandes.
 */
@Entity
@Table(name = "commandes")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Commandes implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "date_commande")
    private Instant dateCommande;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user", "prenom", "telephone" }, allowSetters = true)
    private Clients clients;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "categories", "nomProduit" }, allowSetters = true)
    private Produits produits;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Commandes id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getDateCommande() {
        return this.dateCommande;
    }

    public Commandes dateCommande(Instant dateCommande) {
        this.setDateCommande(dateCommande);
        return this;
    }

    public void setDateCommande(Instant dateCommande) {
        this.dateCommande = dateCommande;
    }

    public Clients getClients() {
        return this.clients;
    }

    public void setClients(Clients clients) {
        this.clients = clients;
    }

    public Commandes clients(Clients clients) {
        this.setClients(clients);
        return this;
    }

    public Produits getProduits() {
        return this.produits;
    }

    public void setProduits(Produits produits) {
        this.produits = produits;
    }

    public Commandes produits(Produits produits) {
        this.setProduits(produits);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Commandes)) {
            return false;
        }
        return getId() != null && getId().equals(((Commandes) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Commandes{" +
            "id=" + getId() +
            ", dateCommande='" + getDateCommande() + "'" +
            "}";
    }
}
