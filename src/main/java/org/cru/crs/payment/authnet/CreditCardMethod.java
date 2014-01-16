package org.cru.crs.payment.authnet;

import org.cru.crs.payment.authnet.transaction.AuthCapture;
import org.cru.crs.payment.authnet.transaction.Credit;
import org.cru.crs.payment.authnet.transaction.Void;
import org.cru.crs.payment.authnet.transaction.Transaction;


public class CreditCardMethod implements Method
{

	public void checkProperties(Transaction transaction) throws IllegalArgumentException
	{
		if(transaction instanceof AuthCapture)
		{
			transaction.checkCreditCard();
		}
		else if(transaction instanceof Credit || transaction instanceof Void)
		{
			transaction.checkCreditCardRefund();
		}
	}

	public String getCode()
	{
		return "CC";
	}
}
