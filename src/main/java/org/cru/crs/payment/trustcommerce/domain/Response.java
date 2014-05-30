package org.cru.crs.payment.trustcommerce.domain;

public enum Response
{
	TRANSID("transid"), STATUS("status"), DECLINETYPE("declinetype"), ERROR("error"), OFFENDERS("offenders"), ERRORTYPE("errortype"), AUTHCODE("authcode");

	private String value;

	Response(String value)
	{
		this.value = value;
	}

	public String getValue()
	{
		return value;
	}
}
