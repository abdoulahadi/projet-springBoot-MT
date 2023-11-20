package com.mycompany.myapp.domain;

import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Produits.
 */
@Table("produits")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Produits implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("nom_produit")
    private String nomProduit;

    @Column("description_produit")
    private String descriptionProduit;

    @Column("prix_produit")
    private Long prixProduit;

    @Column("image_produit")
    private String imageProduit;

    @Transient
    private Categories categories;

    @Column("categories_id")
    private Long categoriesId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Produits id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomProduit() {
        return this.nomProduit;
    }

    public Produits nomProduit(String nomProduit) {
        this.setNomProduit(nomProduit);
        return this;
    }

    public void setNomProduit(String nomProduit) {
        this.nomProduit = nomProduit;
    }

    public String getDescriptionProduit() {
        return this.descriptionProduit;
    }

    public Produits descriptionProduit(String descriptionProduit) {
        this.setDescriptionProduit(descriptionProduit);
        return this;
    }

    public void setDescriptionProduit(String descriptionProduit) {
        this.descriptionProduit = descriptionProduit;
    }

    public Long getPrixProduit() {
        return this.prixProduit;
    }

    public Produits prixProduit(Long prixProduit) {
        this.setPrixProduit(prixProduit);
        return this;
    }

    public void setPrixProduit(Long prixProduit) {
        this.prixProduit = prixProduit;
    }

    public String getImageProduit() {
        return this.imageProduit;
    }

    public Produits imageProduit(String imageProduit) {
        this.setImageProduit(imageProduit);
        return this;
    }

    public void setImageProduit(String imageProduit) {
        this.imageProduit = imageProduit;
    }

    public Categories getCategories() {
        return this.categories;
    }

    public void setCategories(Categories categories) {
        this.categories = categories;
        this.categoriesId = categories != null ? categories.getId() : null;
    }

    public Produits categories(Categories categories) {
        this.setCategories(categories);
        return this;
    }

    public Long getCategoriesId() {
        return this.categoriesId;
    }

    public void setCategoriesId(Long categories) {
        this.categoriesId = categories;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Produits)) {
            return false;
        }
        return getId() != null && getId().equals(((Produits) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Produits{" +
            "id=" + getId() +
            ", nomProduit='" + getNomProduit() + "'" +
            ", descriptionProduit='" + getDescriptionProduit() + "'" +
            ", prixProduit=" + getPrixProduit() +
            ", imageProduit='" + getImageProduit() + "'" +
            "}";
    }
}
