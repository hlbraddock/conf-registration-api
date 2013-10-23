package org.cru.crs.model;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.joda.time.DateTime;


public class SessionEntity implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;

	public static Map<String,String> columnMappings;
	
	static
	{
		columnMappings = new HashMap<String,String>();
		columnMappings.put("AUTH_PROVIDER_ID", "authProviderId");
		columnMappings.put("AUTH_CODE", "authCode");
	}
	
	private UUID id;
	private UUID authProviderId;
	private String authCode;
	DateTime expiration;

	public UUID getId()
	{
		return id;
	}

	public void setId(UUID id)
	{
		this.id = id;
	}

	public String getAuthCode()
	{
		return authCode;
	}

	public void setAuthCode(String authCode)
	{
		this.authCode = authCode;
	}

	public DateTime getExpiration()
	{
		return expiration;
	}

	public void setExpiration(DateTime expiration)
	{
		this.expiration = expiration;
	}

	public UUID getAuthProviderId() {
		return authProviderId;
	}

	public void setAuthProviderId(UUID authProviderId) {
		this.authProviderId = authProviderId;
	}
}
