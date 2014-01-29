package org.cru.crs.payment.authnet.transaction;

import java.util.HashMap;
import java.util.Map;

public class TransactionResult
{

	private Long transactionID;
	private String authCode;

	public Map<String, String> getParamMap()
	{
		Map<String, String> request = new HashMap<String, String>();
		request.put("x_trans_id", transactionID == null ? null : transactionID.toString());
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

	public Long getTransactionID()
	{
		return transactionID;
	}

	public void setTransactionID(Long transactionID)
	{
		this.transactionID = transactionID;
	}
}
