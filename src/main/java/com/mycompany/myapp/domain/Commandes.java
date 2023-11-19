package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Commandes.
 */
@Table("commandes")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Commandes implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("id_commande")
    private Long idCommande;

    @Column("date_commande")
    private Instant dateCommande;

    @Transient
    @JsonIgnoreProperties(value = { "user" }, allowSetters = true)
    private Clients clients;

    @Transient
    @JsonIgnoreProperties(value = { "categories" }, allowSetters = true)
    private Produits produits;

    @Column("clients_id")
    private Long clientsId;

    @Column("produits_id")
    private Long produitsId;

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

    public Long getIdCommande() {
        return this.idCommande;
    }

    public Commandes idCommande(Long idCommande) {
        this.setIdCommande(idCommande);
        return this;
    }

    public void setIdCommande(Long idCommande) {
        this.idCommande = idCommande;
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
        this.clientsId = clients != null ? clients.getId() : null;
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
        this.produitsId = produits != null ? produits.getId() : null;
    }

    public Commandes produits(Produits produits) {
        this.setProduits(produits);
        return this;
    }

    public Long getClientsId() {
        return this.clientsId;
    }

    public void setClientsId(Long clients) {
        this.clientsId = clients;
    }

    public Long getProduitsId() {
        return this.produitsId;
    }

    public void setProduitsId(Long produits) {
        this.produitsId = produits;
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
            ", idCommande=" + getIdCommande() +
            ", dateCommande='" + getDateCommande() + "'" +
            "}";
    }
}
