package org.cru.crs.payment.authnet;

public class CreditCardMethod
{

	public void checkProperties(Transaction transaction) throws IllegalArgumentException
	{
		transaction.checkCreditCard();
	}

	public String getCode()
	{
		return "CC";
	}
}
