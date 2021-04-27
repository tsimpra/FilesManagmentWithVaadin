package com.tsimpra.filesmanagment.persistence.repository;

import com.tsimpra.filesmanagment.persistence.entity.PersonalDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface PersonalDocumentRepository extends JpaRepository<PersonalDocument, BigDecimal> {
}
