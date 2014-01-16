package org.cru.crs.payment.authnet;

public class AuthnetTransactionException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	AuthnetResponse authnetResponse;
	
	public AuthnetTransactionException(AuthnetResponse authnetResponse)
	{
		this.authnetResponse = authnetResponse;
	}
	
	@Override
	public String getMessage()
	{
		return authnetResponse.getReasonText();
	}
	
	public Integer getReasonCode()
	{
		return authnetResponse.getReasonCode();
	}
}
