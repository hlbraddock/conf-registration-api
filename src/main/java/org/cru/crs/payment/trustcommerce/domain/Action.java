package org.cru.crs.payment.trustcommerce.domain;

public enum Action
{
	SALE("sale"), PREAUTH("preauth"), POSTAUTH("postauth"), CREDIT("credit"), VOID("void"), STORE("store");

	private String value;

	Action(String value)
	{
		this.value = value;
	}

	public String getValue()
	{
		return value;
	}
}
