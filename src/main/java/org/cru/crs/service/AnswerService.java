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
	
	AnswerQueries answerQueries;
	
    @Inject
    public AnswerService(Sql2o sql, AnswerQueries answerQueries)
    {
    	this.sql = sql;
		this.sql.setDefaultColumnMappings(EntityColumnMappings.get(AnswerEntity.class));
		this.answerQueries = answerQueries;
    }
    

    public AnswerEntity getAnswerBy(UUID answerId)
    {
        return sql.createQuery(answerQueries.selectById())
        			.addParameter("id", answerId)
        			.executeAndFetchFirst(AnswerEntity.class);
    }

    public void updateAnswer(AnswerEntity answerToUpdate)
    {
        sql.createQuery(answerQueries.update())
        		.addParameter("id", answerToUpdate.getId())
        		.addParameter("registrationId", answerToUpdate.getRegistrationId())
        		.addParameter("blockId", answerToUpdate.getBlockId())
//        		.addParameter("Json", "json")
        		.executeUpdate();
        		
    }

	public void deleteAnswer(AnswerEntity answerToDelete)
	{
		sql.createQuery(answerQueries.delete())
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
