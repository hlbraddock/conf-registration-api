package org.cru.crs.payment.authnet;

import org.uscm.crs.util.Contract;

public final class Auth extends Transaction
{

	Auth(Method method)
	{
		super(method);
	}

	@Override
	public String getTransactionType()
	{
		return "AUTH_ONLY";
	}

	@Override
	protected void processResponse()
	{
		if (authnetResponse.isApproved())
		{
			transactionResult = new TransactionResult();
			transactionResult.setAuthCode(authnetResponse.getAuthorizationCode());
			transactionResult.setTransactionID(authnetResponse.getTransactionID());
		}
	}

	@Override
	protected void checkTypeSpecificProperties()
	{
		Contract.require(getAmount(), "amount");
	}

}
