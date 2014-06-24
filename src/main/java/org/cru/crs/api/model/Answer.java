package org.cru.crs.api.model;

import com.fasterxml.jackson.databind.JsonNode;
import org.cru.crs.model.AnswerEntity;

import java.util.UUID;

public class Answer implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;

	private UUID id;
	private UUID registrationId;
	private UUID blockId;

	private JsonNode value;

	public Answer()
	{
	}

	public Answer(UUID id, UUID registrationId, UUID blockId, JsonNode value)
	{
		this.id = id;
		this.registrationId = registrationId;
		this.blockId = blockId;
		this.value = value;
	}

	public static Answer fromDb(AnswerEntity dbAnswer)
	{
		return new Answer(dbAnswer.getId(), dbAnswer.getRegistrationId(), dbAnswer.getBlockId(), dbAnswer.getAnswer());
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
