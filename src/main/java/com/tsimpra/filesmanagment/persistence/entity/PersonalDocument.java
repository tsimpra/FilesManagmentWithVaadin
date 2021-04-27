package com.tsimpra.filesmanagment.persistence.entity;

import org.springframework.content.commons.annotations.ContentId;
import org.springframework.content.commons.annotations.ContentLength;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "PERSONAL_DOCUMENTS")
public class PersonalDocument {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigDecimal id;
    @Column(name="NAME")
    private String name;
    @OneToOne
    private Person person;

    @ContentId
    @Column(name="CONTENT_ID")
    private String contentId;
    @ContentLength
    @Column(name="CONTENT_LENGTH")
    private Long contentLength;

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

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public Long getContentLength() {
        return contentLength;
    }

    public void setContentLength(Long contentLength) {
        this.contentLength = contentLength;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
