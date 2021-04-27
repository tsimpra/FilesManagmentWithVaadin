package com.tsimpra.filesmanagment.persistence.repository;

import com.tsimpra.filesmanagment.persistence.entity.PersonalDocument;
import org.springframework.content.commons.repository.ContentStore;
import org.springframework.stereotype.Component;

@Component
public interface PersonalDocumentContentStore extends ContentStore<PersonalDocument,String> {
}
