package org.cru.crs.payment.trustcommerce.domain;

public enum Response
{
	TRANSACTION_ID("transid"), STATUS("status"), DECLINE_TYPE("declinetype"), ERROR("error"), OFFENDERS("offenders"), ERROR_TYPE("errortype"), AUTH_CODE("authcode");

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
