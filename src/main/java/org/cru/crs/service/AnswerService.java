package org.cru.crs.service;

import org.cru.crs.model.AnswerEntity;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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

	public void deleteAnswersByBlockId(UUID blockId)
	{
		for(AnswerEntity answerEntity : fetchAnswersByBlockId(blockId))
			deleteAnswer(answerEntity);
	}

	private Set<AnswerEntity> fetchAnswersByBlockId(UUID blockId)
	{
		TypedQuery<AnswerEntity> query = em.createQuery("SELECT answer " +
				"FROM AnswerEntity answer " +
				"WHERE answer.blockId = :block_id", AnswerEntity.class);

		query.setParameter("block_id", blockId);

		return new HashSet<AnswerEntity>(query.getResultList());
	}
}
