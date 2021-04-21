package com.tsimpra.filesmanagment.persistence.service;

import com.tsimpra.filesmanagment.persistence.entity.Title;
import com.tsimpra.filesmanagment.persistence.repository.TitleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.Access;
import java.math.BigDecimal;
import java.util.List;

@Service
public class TitleService {
    @Autowired
    private TitleRepository  titleRepository;

    public List<Title> findAll(){return titleRepository.findAll();}
    public Title findById(BigDecimal id){return titleRepository.findById(id).orElse(null);}
}
