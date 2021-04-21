package com.tsimpra.filesmanagment.persistence.repository;

import com.tsimpra.filesmanagment.persistence.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface PersonRepository extends JpaRepository<Person, BigDecimal> {
}
