package org.cru.crs.payment.trustcommerce.domain;

public enum Action
{
	SALE("sale"), PRE_AUTH("preauth"), POST_AUTH("postauth"), CREDIT("credit"), VOID("void"), STORE("store");

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
