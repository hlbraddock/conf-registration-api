package org.cru.crs.payment.authnet;

import java.util.HashMap;
import java.util.Map;

public class PostalAddress
{

	private String address;
	private String city;
	private String state;
	private String country;
	private String zip;

	public Map<String, String> getParamMap()
	{
		Map<String, String> request = new HashMap<String, String>();
		request.put("x_address", address);
		request.put("x_city", city);
		request.put("x_state", state);
		request.put("x_zip", zip);
		request.put("x_country", country);
		return request;
	}

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}

	public String getCity()
	{
		return city;
	}

	public void setCity(String city)
	{
		this.city = city;
	}

	public String getCountry()
	{
		return country;
	}

	/**
	 * valid two-digit country code or full name in English
	 * 
	 * @return
	 */
	public void setCountry(String country)
	{
		this.country = country;
	}

	public String getState()
	{
		return state;
	}

	/**
	 * valid two-letter state code or full name
	 * 
	 * @return
	 */
	public void setState(String state)
	{
		this.state = state;
	}

	public String getZip() 
	{
		return zip;
	}

	public void setZip(String zip)
	{
		this.zip = zip;
	}

}
