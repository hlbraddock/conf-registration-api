package org.cru.crs.payment.authnet.model;

import java.util.HashMap;
import java.util.Map;

public class Invoice
{
	private String description;
	private String invoiceNum;

	public Map<String, String> getParamMap()
	{
		Map<String, String> request = new HashMap<String, String>();
		request.put("x_invoice_num", invoiceNum);
		request.put("x_description", description);
		return request;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getInvoiceNum()
	{
		return invoiceNum;
	}

	public void setInvoiceNum(String invoiceNum)
	{
		this.invoiceNum = invoiceNum;
	}

}
