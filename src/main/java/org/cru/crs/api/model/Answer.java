package org.cru.crs.api.model;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.cru.crs.model.AnswerEntity;

import java.io.IOException;
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
	
	public static Answer fromJpa(AnswerEntity jpaAnswer)
	{
		Answer answer = new Answer();

		answer.id = jpaAnswer.getId();
		answer.registrationId = jpaAnswer.getRegistrationId();
		answer.blockId = jpaAnswer.getBlockId();
		answer.value = jsonNodeFromString(jpaAnswer.getAnswer());

		return answer;
	}

	public static Set<Answer> fromJpa(Set<AnswerEntity> jpaAnswers)
	{
		Set<Answer> answers = new HashSet<Answer>();
		
		if(jpaAnswers == null) return answers;
		
		for(AnswerEntity jpaAnswer : jpaAnswers)
		{
			answers.add(fromJpa(jpaAnswer));
		}
		
		return answers;
	}
	
	public AnswerEntity toJpaAnswerEntity()
	{
		AnswerEntity jpaAnswer = new AnswerEntity();
		
		jpaAnswer.setId(id);
		jpaAnswer.setRegistrationId(registrationId);
        jpaAnswer.setBlockId(blockId);
		jpaAnswer.setAnswer(value.toString());

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

	private static JsonNode jsonNodeFromString(String jsonString)
	{
		try
		{
			return (new ObjectMapper()).readTree(jsonString);
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		return null;
	}
}
