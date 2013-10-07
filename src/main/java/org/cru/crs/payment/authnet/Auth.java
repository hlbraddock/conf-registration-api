package org.cru.crs.payment.authnet;

import com.google.common.base.Preconditions;


public final class Auth extends Transaction
{

	Auth(CreditCardMethod method)
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
		Preconditions.checkNotNull(getAmount(),"amount was null");
	}

}
