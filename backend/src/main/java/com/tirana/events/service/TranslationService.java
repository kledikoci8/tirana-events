package com.tirana.events.service;

import com.tirana.events.model.Translation;
import com.tirana.events.repository.TranslationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TranslationService {
    private final TranslationRepository translationRepository;

    public String getTranslation(String entityType, Long entityId, String fieldName, String language) {
        Optional<Translation> translation = translationRepository
            .findByEntityTypeAndEntityIdAndFieldNameAndLanguage(entityType, entityId, fieldName, language);
        
        return translation.map(Translation::getTranslatedText).orElse(null);
    }

    public void saveTranslation(String entityType, Long entityId, String fieldName, 
                               String language, String translatedText) {
        Translation translation = translationRepository
            .findByEntityTypeAndEntityIdAndFieldNameAndLanguage(entityType, entityId, fieldName, language)
            .orElse(new Translation());
        
        translation.setEntityType(entityType);
        translation.setEntityId(entityId);
        translation.setFieldName(fieldName);
        translation.setLanguage(language);
        translation.setTranslatedText(translatedText);
        
        translationRepository.save(translation);
    }
}
