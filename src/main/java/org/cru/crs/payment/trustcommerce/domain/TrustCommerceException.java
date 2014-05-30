package org.cru.crs.payment.trustcommerce.domain;

import java.util.Map;

public class TrustCommerceException extends Exception
{
	public TrustCommerceException(String message)
	{
		super(message);
	}

	public TrustCommerceException(Map<String,String> response)
	{
		super("status: " + response.get(Response.STATUS.getValue()) +
				", error: " + response.get(Response.ERROR.getValue()) +
				", offenders: " + response.get(Response.OFFENDERS.getValue()));
	}
}
