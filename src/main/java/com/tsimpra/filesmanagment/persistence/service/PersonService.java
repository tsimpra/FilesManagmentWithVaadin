package com.tsimpra.filesmanagment.persistence.service;

import com.tsimpra.filesmanagment.persistence.entity.Person;
import com.tsimpra.filesmanagment.persistence.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PersonService {
    @Autowired
    private PersonRepository personRepository;

    public List<Person> findAll(){return personRepository.findAll();}
    public Person findById(BigDecimal id){return personRepository.findById(id).orElse(null);}
}
