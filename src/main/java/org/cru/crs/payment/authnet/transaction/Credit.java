package org.cru.crs.payment.authnet.transaction;

import com.google.common.base.Preconditions;
import org.cru.crs.payment.authnet.CreditCardMethod;


public final class Credit extends Transaction
{

	public Credit()
	{
		super(new CreditCardMethod());
	}

	@Override
	public String getTransactionType()
	{
		return "CREDIT";
	}

	@Override
	protected void checkTypeSpecificProperties()
	{
		Preconditions.checkNotNull(transactionResult);
		Preconditions.checkNotNull(transactionResult.getTransactionID());		
	}
}
