package com.tsimpra.filesmanagment.persistence.service;

import com.tsimpra.filesmanagment.persistence.entity.PersonalDocument;
import com.tsimpra.filesmanagment.persistence.repository.PersonalDocumentContentStore;
import com.tsimpra.filesmanagment.persistence.repository.PersonalDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class PersonalDocumentService {
    @Autowired
    private PersonalDocumentRepository docRepo;
    @Autowired
    private PersonalDocumentContentStore contentStore;

    public void savePersonalDocument(PersonalDocument doc, InputStream input){
        docRepo.save(doc);
        contentStore.setContent(doc,input);
    }

    public InputStream getDocumentStream(PersonalDocument doc) {
        return contentStore.getContent(doc);
    }
}
