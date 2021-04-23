package com.tsimpra.filesmanagment.persistence.entity;

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
    private BigDecimal id;
    @Column(name = "NAME")
    private String name;
    @Column(name="JOB")
    private String job;
    @OneToMany(mappedBy = "person",targetEntity = Title.class,orphanRemoval = true,fetch = FetchType.EAGER)
    private List<Title> titles;

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
