package com.tsimpra.filesmanagment.persistence.service;

import com.tsimpra.filesmanagment.persistence.entity.Person;
import com.tsimpra.filesmanagment.persistence.entity.Title;
import com.tsimpra.filesmanagment.persistence.repository.PersonRepository;
import com.tsimpra.filesmanagment.persistence.repository.TitleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PersonService {
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private TitleRepository titleRepository;

    public List<Person> findAll(){return personRepository.findAll();}
    public Person findById(BigDecimal id){return personRepository.findById(id).orElse(null);}
    public void save(Person p){
        if(p.getId()==null || !personRepository.existsById(p.getId())) {
            Person entity = personRepository.save(p);
            if (p.getTitles() != null) {
                for (Title title : p.getTitles()) {
                    title.setPerson(entity);
                    titleRepository.save(title);
                }
            }
        }
    }
}
