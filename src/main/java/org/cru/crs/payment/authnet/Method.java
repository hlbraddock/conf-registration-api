package org.cru.crs.payment.authnet;

import org.cru.crs.payment.authnet.transaction.Transaction;

public interface Method
{

	public void checkProperties(Transaction transaction) throws IllegalArgumentException;

	public String getCode();

}
