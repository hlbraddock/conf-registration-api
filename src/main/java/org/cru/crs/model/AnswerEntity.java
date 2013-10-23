package org.cru.crs.model;

import java.util.UUID;

import org.codehaus.jackson.JsonNode;

public class AnswerEntity implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;

	private UUID id;
	private UUID registrationId;
	private UUID blockId;
	private JsonNode answer;

	public UUID getId()
	{
		return id;
	}

	public void setId(UUID id)
	{
		this.id = id;
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

	public JsonNode getAnswer()
	{
		return answer;
	}

	public void setAnswer(JsonNode answer)
	{
		this.answer = answer;
	}
}
