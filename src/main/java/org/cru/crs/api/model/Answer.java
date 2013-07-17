package org.cru.crs.api.model;

import org.cru.crs.model.AnswerEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Answer implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;

	private UUID id;
    private UUID blockId;
	
	private String value;
	
	public static Answer fromJpa(AnswerEntity jpaAnswer)
	{
		Answer answer = new Answer();
		
		answer.id = jpaAnswer.getId();
		answer.blockId = jpaAnswer.getBlockId();
		answer.value = jpaAnswer.getAnswer();

		return answer;
	}
	
	public static List<Answer> fromJpa(List<AnswerEntity> jpaAnswers)
	{
		List<Answer> answers = new ArrayList<Answer>();
		
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

    public UUID getBlockId()
    {
        return blockId;
    }

    public void setBlockId(UUID blockId)
    {
        this.blockId = blockId;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }
}
