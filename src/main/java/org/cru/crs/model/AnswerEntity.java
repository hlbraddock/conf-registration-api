package org.cru.crs.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.UUID;

public class AnswerEntity implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;

	private UUID id;
	private UUID registrationId;
	private UUID blockId;
	private JsonNode answer;

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(29, 79). // two randomly chosen prime numbers
				append(id).
				toHashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null) return false;
		if (obj == this) return true;
		if (!(obj instanceof AnswerEntity)) return false;

		AnswerEntity rhs = (AnswerEntity) obj;
		return new EqualsBuilder().
				append(id, rhs.id).
				isEquals();
	}

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
