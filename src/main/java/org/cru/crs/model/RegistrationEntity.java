package org.cru.crs.model;


import java.math.BigDecimal;
import java.util.UUID;

import org.joda.time.DateTime;

public class RegistrationEntity implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;

	private UUID id;
	private UUID conferenceId;
	private UUID userId;
	private BigDecimal totalDue;
    private boolean completed;
    private DateTime completedTimestamp;
    
	public UUID getId()
	{
		return id;
	}
	public void setId(UUID id)
	{
		this.id = id;
	}
	public UUID getConferenceId()
	{
		return conferenceId;
	}
	public void setConferenceId(UUID conferenceId)
	{
		this.conferenceId = conferenceId;
	}
	public UUID getUserId()
	{
		return userId;
	}
	public void setUserId(UUID userId)
	{
		this.userId = userId;
	}
	public boolean getCompleted()
	{
		return completed;
	}
	public void setCompleted(boolean completed)
	{
		this.completed = completed;
	}
	public BigDecimal getTotalDue()
	{
		return totalDue;
	}
	public void setTotalDue(BigDecimal totalDue)
	{
		this.totalDue = totalDue;
	}
	public DateTime getCompletedTimestamp() 
	{
		return completedTimestamp;
	}
	public void setCompletedTimestamp(DateTime completedTimestamp) 
	{
		this.completedTimestamp = completedTimestamp;
	}
	

}
