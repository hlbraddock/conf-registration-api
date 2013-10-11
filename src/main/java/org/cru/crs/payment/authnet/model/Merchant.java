package org.cru.crs.payment.authnet.model;

import java.util.HashMap;
import java.util.Map;

public class Merchant
{

	String login;
	String tranKey;
	String email;

	public Map<String, String> getParamMap()
	{
		Map<String, String> request = new HashMap<String, String>();
		request.put("x_login", login);
		request.put("x_tran_key", tranKey);
		request.put("x_email_merchant", email);
		return request;
	}

	public String getLogin()
	{
		return login;
	}

	public void setLogin(String login)
	{
		this.login = login;
	}

	public String getTranKey()
	{
		return tranKey;
	}

	public void setTranKey(String tranKey)
	{
		this.tranKey = tranKey;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

}
