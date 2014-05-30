package org.cru.crs.payment.trustcommerce.domain;

public enum Status
{
	APPROVED("approved"), ACCEPTED("accepted"), DECLINE("decline"), BADDATA("baddata"), ERROR("error");

	private String value;

	Status(String value)
	{
		this.value = value;
	}

	public String getValue()
	{
		return value;
	}
}
