package org.cru.crs.payment.authnet.transaction;

import org.cru.crs.payment.authnet.Method;

import com.google.common.base.Preconditions;


public final class Void extends Transaction
{

	public Void(Method method)
	{
		super(method);
	}

	@Override
	public String getTransactionType()
	{
		return "VOID";
	}

	@Override
	protected void checkTypeSpecificProperties()
	{
		Preconditions.checkNotNull(transactionResult.getTransactionID());
	}
}
