package org.cru.crs.payment.authnet;

import java.util.HashMap;
import java.util.Map;

public class GatewayConfiguration
{

	String delimiter = ",";
	Boolean emailCustomer;
	String version = "3.1";
	Boolean testRequest;

	public Map<String, String> getParamMap()
	{
		Map<String, String> request = new HashMap<String, String>();
		request.put("x_delim_data", toString(true));
		request.put("x_delim_char", delimiter);
		request.put("x_email_customer", toString(emailCustomer));
		request.put("x_version", version);
		request.put("x_test_request", toString(testRequest));
		return request;
	}

	public String getDelimiter()
	{
		return delimiter;
	}

	public void setDelimiter(String delimiter)
	{
		this.delimiter = delimiter;
	}

	public Boolean getEmailCustomer()
	{
		return emailCustomer;
	}

	public void setEmailCustomer(Boolean emailCustomer)
	{
		this.emailCustomer = emailCustomer;
	}

	public Boolean getTestRequest()
	{
		return testRequest;
	}

	public void setTestRequest(Boolean testReq)
	{
		this.testRequest = testReq;
	}

	public String getVersion()
	{
		return version;
	}

	public void setVersion(String version)
	{
		this.version = version;
	}

	private String toString(Boolean emailCustomer)
	{
		return emailCustomer == null ? null : emailCustomer.toString().toUpperCase();
	}

}
