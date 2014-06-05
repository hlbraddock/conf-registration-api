package org.cru.crs.payment.authnet.transaction;

import java.util.HashMap;
import java.util.Map;

public class TransactionResult
{

	private String transactionID;
	private String authCode;

	public Map<String, String> getParamMap()
	{
		Map<String, String> request = new HashMap<String, String>();
		request.put("x_trans_id", transactionID);
		request.put("x_auth_code", authCode);
		return request;
	}

	public String getAuthCode()
	{
		return authCode;
	}

	public void setAuthCode(String authCode)
	{
		this.authCode = authCode;
	}

	public String getTransactionID()
	{
		return transactionID;
	}

	public void setTransactionID(String transactionID)
	{
		this.transactionID = transactionID;
	}
}
