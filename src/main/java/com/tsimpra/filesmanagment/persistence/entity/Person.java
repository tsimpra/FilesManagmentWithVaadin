package com.tsimpra.filesmanagment.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "PERSONS")
public class Person implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private BigDecimal id;
    @Column(name = "NAME")
    @JsonProperty("name")
    private String name;
    @Column(name="JOB")
    @JsonProperty("job")
    private String job;
    @OneToMany(mappedBy = "person",targetEntity = Title.class,orphanRemoval = true,fetch = FetchType.EAGER)
    @JsonProperty("titles")
    private List<Title> titles;
    @OneToOne(mappedBy = "person",targetEntity = PersonalDocument.class,orphanRemoval = true)
    @JoinColumn(name="PERSONAL_DOCUMENT_ID",referencedColumnName = "ID")
    @JsonIgnore
    private PersonalDocument personalDocument;

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", job='" + job + '\'' +
                ", titles=[" + titles.stream()
                .map(x->x.getName())
                .reduce("",(x,y)->{
                    if(x.isEmpty()) return y;
                    if(y.isEmpty()) return x;
                    return x+","+y;
                }) +
                "]}";
    }

    public PersonalDocument getPersonalDocument() {
        return personalDocument;
    }

    public void setPersonalDocument(PersonalDocument personalDocument) {
        this.personalDocument = personalDocument;
    }

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

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public List<Title> getTitles() {
        return titles;
    }

    public void setTitles(List<Title> titles) {
        this.titles = titles;
    }
}
