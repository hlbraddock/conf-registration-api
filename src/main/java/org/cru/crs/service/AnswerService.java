package org.cru.crs.service;

import java.util.UUID;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.cru.crs.model.AnswerEntity;

/**
 * User: lee.braddock
 */
public class AnswerService {

    EntityManager em;

    @Inject
    public AnswerService(EntityManager em)
    {
        this.em = em;
    }

    public AnswerEntity getAnswerBy(UUID answerId)
    {
        return em.find(AnswerEntity.class, answerId);
    }

    public void updateAnswer(AnswerEntity answerToUpdate)
    {
        em.merge(answerToUpdate);
    }

    public void deleteAnswer(AnswerEntity answerToDelete)
    {
        em.remove(answerToDelete);
    }
}
