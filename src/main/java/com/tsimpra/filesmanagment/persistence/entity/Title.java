package com.tsimpra.filesmanagment.persistence.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "TITLES")
public class Title implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private BigDecimal id;
    @Column(name = "NAME")
    @JsonProperty("name")
    private String name;
    @ManyToOne(optional = false,fetch = FetchType.EAGER)
    @JoinColumn(name="PERSON_ID",referencedColumnName = "ID")
    @JsonBackReference
    private Person person;

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
