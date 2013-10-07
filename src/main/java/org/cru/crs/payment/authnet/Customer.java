package org.cru.crs.payment.authnet;

import java.util.HashMap;
import java.util.Map;

public class Customer
{

	private String email;

	private String firstName;

	private String lastName;

	private String companyName;

	private PostalAddress billingAddress = new PostalAddress();

	private String phone;

	private String fax;

	private String custID;

	private String customerIP;

	private String customerTaxId;

	public Map<String, String> getParamMap()
	{
		Map<String, String> request = new HashMap<String, String>();
		request.put("x_first_name", firstName);
		request.put("x_last_name", lastName);
		request.put("x_company", companyName);
		request.put("x_phone", phone);
		request.put("x_fax", fax);
		request.put("x_email", email);
		request.put("x_cust_id", custID);
		request.put("x_customer_ip", customerIP);
		request.put("x_customer_tax_id", customerTaxId);
		if (billingAddress != null)
			request.putAll(billingAddress.getParamMap());
		return request;
	}

	public String getCustomerTaxId()
	{
		return customerTaxId;
	}

	/**
	 * 9 digit tax ID or SSN
	 * 
	 * @return
	 */
	public void setCustomerTaxId(String customerTaxId)
	{
		this.customerTaxId = customerTaxId;
	}

	public String getCustomerIP()
	{
		return customerIP;
	}

	/**
	 * Required when using the Fraud Detection Suite IP Address Blocking tool.
	 * 
	 * @param customerIP
	 *            Required format is 255.255.255.255. If this value is not
	 *            passed, it will default to 255.255.255.255
	 */
	public void setCustomerIP(String customerIP)
	{
		this.customerIP = customerIP;
	}

	public String getCustID()
	{
		return custID;
	}

	public void setCustID(String custID)
	{
		this.custID = custID;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public PostalAddress getBillingAddress()
	{
		return billingAddress;
	}

	public void setBillingAddress(PostalAddress postalAddress)
	{
		this.billingAddress = postalAddress;
	}

	public String getCompanyName()
	{
		return companyName;
	}

	public void setCompanyName(String companyName)
	{
		this.companyName = companyName;
	}

	public String getFax()
	{
		return fax;
	}

	/**
	 * recommended format is (123)123-1234
	 */
	public void setFax(String fax)
	{
		this.fax = fax;
	}

	public String getPhone()
	{
		return phone;
	}

	/**
	 * recommended format is (123)123-1234
	 */
	public void setPhone(String phone)
	{
		this.phone = phone;
	}
}
