package org.cru.crs.payment.authnet.transaction;

import org.cru.crs.payment.authnet.CreditCardMethod;
import org.cru.crs.payment.authnet.Transaction;
import org.cru.crs.payment.authnet.TransactionResult;

import com.google.common.base.Preconditions;

public final class AuthCapture extends Transaction
{

	public AuthCapture()
	{
		super(new CreditCardMethod());
	}
	
	AuthCapture(CreditCardMethod method)
	{
		super(method);
	}

	@Override
	public String getTransactionType()
	{
		return "AUTH_CAPTURE";
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
		Preconditions.checkNotNull(getAmount(), "amount was null");
	}

}
