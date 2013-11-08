package org.cru.crs.api.model;

import org.codehaus.jackson.JsonNode;
import org.cru.crs.model.AnswerEntity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Answer implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;

	private UUID id;
	private UUID registrationId;
	private UUID blockId;

	private JsonNode value;
	
	public static Answer fromDb(AnswerEntity dbAnswer)
	{
		Answer answer = new Answer();

		answer.id = dbAnswer.getId();
		answer.registrationId = dbAnswer.getRegistrationId();
		answer.blockId = dbAnswer.getBlockId();
		answer.value = dbAnswer.getAnswer();

		return answer;
	}
	
	public AnswerEntity toDbAnswerEntity()
	{
		AnswerEntity jpaAnswer = new AnswerEntity();
		
		jpaAnswer.setId(id);
		jpaAnswer.setRegistrationId(registrationId);
        jpaAnswer.setBlockId(blockId);
		jpaAnswer.setAnswer(value);

		return jpaAnswer;
	}

	public UUID getId()
	{
		return id;
	}

	public Answer setId(UUID id)
	{
		this.id = id;
		return this;
	}

	public UUID getRegistrationId()
	{
		return registrationId;
	}

	public void setRegistrationId(UUID registrationId)
	{
		this.registrationId = registrationId;
	}

	public UUID getBlockId()
    {
        return blockId;
    }

    public void setBlockId(UUID blockId)
    {
        this.blockId = blockId;
    }

	public JsonNode getValue()
	{
		return value;
	}

	public void setValue(JsonNode value)
	{
		this.value = value;
	}
}
