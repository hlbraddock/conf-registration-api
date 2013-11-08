package org.cru.crs.api.model;

public abstract class Error implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;
	
	protected int statusCode;
	protected String errorMessage;
	protected String customErrorMessage;
	
	public int getStatusCode()
	{
		return statusCode;
	}
	
	protected void setStatusCode(int statusCode)
	{
		this.statusCode = statusCode;
	}
	
	public String getErrorMessage()
	{
		return errorMessage;
	}
	
	protected void setErrorMessage(String errorMessage)
	{
		this.errorMessage = errorMessage;
	}
	
	public String getCustomErrorMessage()
	{
		return customErrorMessage;
	}
	
	public Error setCustomErrorMessage(String customErrorMessage)
	{
		this.customErrorMessage = customErrorMessage;
		return this;
	}
}
