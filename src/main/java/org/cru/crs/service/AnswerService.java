package org.cru.crs.service;

import java.util.List;
import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.cru.crs.model.AnswerEntity;
import org.cru.crs.model.queries.AnswerQueries;
import org.sql2o.Connection;

/**
 * User: lee.braddock
 */

@RequestScoped
public class AnswerService
{
	org.sql2o.Connection sqlConnection;
	
	AnswerQueries answerQueries = new AnswerQueries();
	
	/*required for Weld*/
	public AnswerService(){ }
	
    @Inject
    public AnswerService(Connection sqlConnection)
    {
    	this.sqlConnection = sqlConnection;
    }
    
    public AnswerEntity getAnswerBy(UUID answerId)
    {
        return sqlConnection.createQuery(answerQueries.selectById())
        						.addParameter("id", answerId)
        						.setAutoDeriveColumnNames(true)
        						.executeAndFetchFirst(AnswerEntity.class);
    }

    public List<AnswerEntity> getAllAnswersForRegistration(UUID registrationId)
    {
    	return sqlConnection.createQuery(answerQueries.selectAllForRegistration())
    							.addParameter("registrationId", registrationId)
    							.setAutoDeriveColumnNames(true)
    							.executeAndFetch(AnswerEntity.class);
    }
    
	public List<AnswerEntity> getAllAnswersForBlock(UUID blockId)
	{
		return sqlConnection.createQuery(answerQueries.selectAllForBlock())
								.addParameter("blockId", blockId)
								.setAutoDeriveColumnNames(true)
								.executeAndFetch(AnswerEntity.class);
	}
    
    public void updateAnswer(AnswerEntity answerToUpdate)
    {
    	sqlConnection.createQuery(answerQueries.update())
        				.addParameter("id", answerToUpdate.getId())
        				.addParameter("registrationId", answerToUpdate.getRegistrationId())
        				.addParameter("blockId", answerToUpdate.getBlockId())
        				.addParameter("answer", answerToUpdate.getAnswer())
        				.executeUpdate();
        		
    }

    public void insertAnswer(AnswerEntity answerToInsert)
    {
    	sqlConnection.createQuery(answerQueries.insert())
        				.addParameter("id", answerToInsert.getId())
        				.addParameter("registrationId", answerToInsert.getRegistrationId())
        				.addParameter("blockId", answerToInsert.getBlockId())
        				.addParameter("answer", answerToInsert.getAnswer())
        				.executeUpdate();
    }
    
	public void deleteAnswer(AnswerEntity answerToDelete)
	{
		sqlConnection.createQuery(answerQueries.delete())
						.addParameter("id", answerToDelete.getId())
						.executeUpdate();
	}

	public void deleteAnswersByBlockId(UUID blockId)
	{
		for(AnswerEntity answerEntity : getAllAnswersForBlock(blockId))
		{
			deleteAnswer(answerEntity);
		}
	}
	
	public void deleteAnswersByRegistrationId(UUID registrationId)
	{
		for(AnswerEntity answerEntity : getAllAnswersForRegistration(registrationId))
		{
			deleteAnswer(answerEntity);
		}
	}
}
