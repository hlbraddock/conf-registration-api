package org.cru.crs.model;

import org.codehaus.jackson.JsonNode;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "ANSWERS")
public class AnswerEntity implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID")
	@Type(type = "pg-uuid")
	private UUID id;

	@Column(name = "REGISTRATION_ID", insertable = false, updatable = false)
	@Type(type = "pg-uuid")
	private UUID registrationId;

	@Column(name = "BLOCK_ID")
	@Type(type = "pg-uuid")
	private UUID blockId;

	@Column(name = "ANSWER")
	@Type(type="org.cru.crs.utils.JsonUserType")
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
