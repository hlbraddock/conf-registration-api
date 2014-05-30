package org.cru.crs.payment.trustcommerce.domain;

public enum Request
{
	CUSTOMER_ID("custid"), PASSWORD("password"), ACTION("action"), CREDIT_CARD("cc"), EXPIRATION("exp"), AMOUNT("amount");

	private String value;

	Request(String value)
	{
		this.value = value;
	}

	public String getValue()
	{
		return value;
	}
}
