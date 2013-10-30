package org.cru.crs.service;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.cru.crs.model.AnswerEntity;
import org.cru.crs.model.queries.AnswerQueries;
import org.cru.crs.model.queries.EntityColumnMappings;
import org.sql2o.Sql2o;

/**
 * User: lee.braddock
 */
public class AnswerService
{
	Sql2o sql;
	
	AnswerQueries answerQueries = new AnswerQueries();
	
    @Inject
    public AnswerService(Sql2o sql)
    {
    	this.sql = sql;
		this.sql.setDefaultColumnMappings(EntityColumnMappings.get(AnswerEntity.class));
    }
    
    public AnswerEntity getAnswerBy(UUID answerId)
    {
        return sql.createQuery(answerQueries.selectById(), false)
        			.addParameter("id", answerId)
        			.executeAndFetchFirst(AnswerEntity.class);
    }

    public List<AnswerEntity> getAllAnswersForRegistration(UUID registrationId)
    {
    	return sql.createQuery(answerQueries.selectAllForRegistration(), false)
    			.addParameter("registrationId", registrationId)
    			.executeAndFetch(AnswerEntity.class);
    }
    
    public void updateAnswer(AnswerEntity answerToUpdate)
    {
        sql.createQuery(answerQueries.update(), false)
        		.addParameter("id", answerToUpdate.getId())
        		.addParameter("registrationId", answerToUpdate.getRegistrationId())
        		.addParameter("blockId", answerToUpdate.getBlockId())
        		.addParameter("content", answerToUpdate.getAnswer())
        		.executeUpdate();
        		
    }

    public void insertAnswer(AnswerEntity answerToInsert)
    {
        sql.createQuery(answerQueries.insert(), false)
        		.addParameter("id", answerToInsert.getId())
        		.addParameter("registrationId", answerToInsert.getRegistrationId())
        		.addParameter("blockId", answerToInsert.getBlockId())
        		.addParameter("content", answerToInsert.getAnswer())
        		.executeUpdate();
    }
    
	public void deleteAnswer(AnswerEntity answerToDelete)
	{
		sql.createQuery(answerQueries.delete(), false)
						.addParameter("id", answerToDelete.getId())
						.executeUpdate();
	}

	public void deleteAnswersByBlockId(UUID blockId)
	{
		for(AnswerEntity answerEntity : fetchAnswersByBlockId(blockId))
		{
			deleteAnswer(answerEntity);
		}
	}

	private List<AnswerEntity> fetchAnswersByBlockId(UUID blockId)
	{
		return sql.createQuery(answerQueries.selectAllForBlock())
				.addParameter("blockId", blockId)
				.executeAndFetch(AnswerEntity.class);
	}
	
}
