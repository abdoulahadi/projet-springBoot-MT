package com.mycompany.myapp.domain;

import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Categories.
 */
@Table("categories")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Categories implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("id_categorie")
    private Long idCategorie;

    @Column("nom_categorie")
    private String nomCategorie;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Categories id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdCategorie() {
        return this.idCategorie;
    }

    public Categories idCategorie(Long idCategorie) {
        this.setIdCategorie(idCategorie);
        return this;
    }

    public void setIdCategorie(Long idCategorie) {
        this.idCategorie = idCategorie;
    }

    public String getNomCategorie() {
        return this.nomCategorie;
    }

    public Categories nomCategorie(String nomCategorie) {
        this.setNomCategorie(nomCategorie);
        return this;
    }

    public void setNomCategorie(String nomCategorie) {
        this.nomCategorie = nomCategorie;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Categories)) {
            return false;
        }
        return getId() != null && getId().equals(((Categories) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Categories{" +
            "id=" + getId() +
            ", idCategorie=" + getIdCategorie() +
            ", nomCategorie='" + getNomCategorie() + "'" +
            "}";
    }
}
