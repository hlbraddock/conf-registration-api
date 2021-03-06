/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.cru.crs.payment.authnet.transaction;

import org.apache.log4j.Logger;
import org.cru.crs.payment.authnet.AuthnetResponse;
import org.cru.crs.payment.authnet.AuthnetTransactionException;
import org.cru.crs.payment.authnet.CreditCardMethod;
import org.cru.crs.payment.authnet.HttpProvider;
import org.cru.crs.payment.authnet.Method;
import org.cru.crs.payment.authnet.model.CreditCard;
import org.cru.crs.payment.authnet.model.Customer;
import org.cru.crs.payment.authnet.model.GatewayConfiguration;
import org.cru.crs.payment.authnet.model.Invoice;
import org.cru.crs.payment.authnet.model.Merchant;
import org.scribe.utils.Preconditions;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class Transaction
{

	private Logger log = Logger.getLogger(this.getClass());

	private HttpProvider httpProvider;

	private String url;

	private Merchant merchant = new Merchant();
	private GatewayConfiguration gatewayConfiguration = new GatewayConfiguration();
	private Customer customer = new Customer();
	private Invoice invoice = new Invoice();

	private String currency;
	private BigDecimal amount;
	private Method method;

	private Map<String, String> request = new HashMap<String, String>();
	protected AuthnetResponse authnetResponse;
	protected TransactionResult transactionResult;

	private String escapeCharacter = " ";

	private CreditCard creditCard;

	public Transaction(Method method)
	{
		this.method = method;
	}
	
	public Transaction(CreditCardMethod method)
	{
		this.method = method;
	}

	public void execute() throws IOException
	{
		checkProperties();
		buildRequest();
		submitRequest();
		processResponse();
	}

	protected void processResponse()
	{
		if (!authnetResponse.isApproved())
		{
			log.warn("transaction not approved");
			log.warn("responseCode:   " + authnetResponse.getResponseField(AuthnetResponse.RESPONSE_CODE));
			log.warn("responseReason: " + authnetResponse.getResponseField(AuthnetResponse.RESPONSE_REASON_CODE));
			log.warn("reasonText:     " + authnetResponse.getResponseField(AuthnetResponse.RESPONSE_REASON_TEXT));
			
			throw new AuthnetTransactionException(authnetResponse);
		}
	}

	private void buildRequest()
	{
		buildTransactionData();
		if (merchant != null)
		{
			request.putAll(merchant.getParamMap());
		}
		if (gatewayConfiguration != null)
		{
			request.putAll(gatewayConfiguration.getParamMap());
		}
		if (customer != null)
		{
			request.putAll(customer.getParamMap());
		}
		if (invoice != null)
		{
			request.putAll(invoice.getParamMap());
		}
		if (transactionResult != null)
		{
			request.putAll(transactionResult.getParamMap());
		}
	}

	/**
	 * Wraps IOExceptions as RuntimeExceptions
	 * 
	 */
	private void submitRequest() throws IOException
	{
		if (isEmpty(url))
		{
			url = "https://certification.authorize.net/gateway/transact.dll"; // test
																				// url
			log.info("url not set.  Using a default of [" + url + "]");
		}
		for (Iterator<String> iter = request.keySet().iterator(); iter.hasNext();)
		{
			String key = iter.next();
			String value = request.get(key);
			if (value != null)
			{
				request.put(key, key.equals("x_delim_char") ? value : escape(value));
			}
			else
			{
				iter.remove();
			}
		}

		if (isTestRequest())
		{
			log.debug("TEST Authorize.net using url [" + url + "]");
			log.debug("TEST Authorize.net request string " + request.toString());
		}

		try
		{
			String httpResponse = httpProvider.getContentFromPost(url, request);

			log.debug("transaction response: " + httpResponse);

			if (httpResponse == null)
			{
				throw new IOException("Authorize.net service is unavailable");
			}
			authnetResponse = new AuthnetResponse(httpResponse, gatewayConfiguration.getDelimiter());


		}
		catch (IOException e)
		{
			log.info("Unable to execute Authorize.net transaction due to IOException", e);
			throw e;
		}
	}

	private String escape(String string)
	{
		return string.replace(gatewayConfiguration.getDelimiter(), escapeCharacter);
	}

	private boolean isEmpty(String string)
	{
		return string == null || string.length() == 0;
	}

	private boolean isTestRequest()
	{
		return gatewayConfiguration.getTestRequest() != null && gatewayConfiguration.getTestRequest();
	}

	private void checkProperties() throws IllegalArgumentException
	{
		Preconditions.checkNotNull(merchant, "merchant was null");
		Preconditions.checkEmptyString(merchant.getLogin(),"merchant.login was null/empty");
		Preconditions.checkEmptyString(merchant.getTranKey(),"merchant.tranKey was null/empty");
		Preconditions.checkNotNull(method, "method was null");
		Preconditions.checkEmptyString(getTransactionType(), "transactionType was null/empty");
		Preconditions.checkNotNull(gatewayConfiguration, "gatewayConfiguration was null");
		checkTypeSpecificProperties();
		method.checkProperties(this);
	}

	protected void checkTypeSpecificProperties()
	{
		
	}

	protected void buildTransactionData()
	{
		if (amount != null)
		{
			request.put("x_amount", amount.toString());
		}
		request.put("x_currency_Code", currency);
		request.put("x_method", method.getCode());
		request.put("x_type", getTransactionType());
		if (creditCard != null)
		{
			request.putAll(creditCard.getParamMap());
		}
	}

	public BigDecimal getAmount()
	{
		return amount;
	}

	public void setAmount(BigDecimal amount)
	{
		this.amount = amount;
	}

	public TransactionResult getTransactionResult()
	{
		return transactionResult;
	}

	public void setTransactionResult(TransactionResult transactionResult)
	{
		this.transactionResult = transactionResult;
	}

	public Customer getCustomer()
	{
		return customer;
	}

	public void setCustomer(Customer customer)
	{
		this.customer = customer;
	}

	public CreditCard getCreditCard()
	{
		return creditCard;
	}

	public void setCreditCard(CreditCard creditCard)
	{
		this.creditCard = creditCard;
	}

	public String getCurrency()
	{
		return currency;
	}

	public void setCurrency(String currency)
	{
		this.currency = currency;
	}

	public Logger getLog()
	{
		return log;
	}

	public void setLog(Logger log)
	{
		this.log = log;
	}

	public Method getMethod()
	{
		return method;
	}

	public void setMethod(Method method)
	{
		this.method = method;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public AuthnetResponse getAuthnetResponse()
	{
		return authnetResponse;
	}

	public GatewayConfiguration getGatewayConfiguration()
	{
		return gatewayConfiguration;
	}

	public void setGatewayConfiguration(GatewayConfiguration gatewayConfiguration) 
	{
		this.gatewayConfiguration = gatewayConfiguration;
	}

	public Invoice getInvoice()
	{
		return invoice;
	}

	public void setInvoice(Invoice invoice)
	{
		this.invoice = invoice;
	}

	public Merchant getMerchant()
	{
		return merchant;
	}

	public void setMerchant(Merchant merchant)
	{
		this.merchant = merchant;
	}

	public abstract String getTransactionType();

	public HttpProvider getHttpProvider()
	{
		return httpProvider;
	}

	public void setHttpProvider(HttpProvider httpProvider)
	{
		this.httpProvider = httpProvider;
	}

	public void checkCreditCard()
	{
		Preconditions.checkNotNull(getCreditCard().getCardNumber(), "creditCard.cardNumber was null");
		Preconditions.checkNotNull(getCreditCard().getExpirationDate(), "creditCard.expirationDate was null");
		Preconditions.checkNotNull(getCreditCard().getCardCode(), "creditCard.cardCode was null");
	}

	/**
	 * Only the CC last four digits are required for a Credit
	 */
	public void checkCreditCardRefund()
	{
		Preconditions.checkNotNull(getCreditCard().getCardNumber(), "creditCard.cardNumber was null");	
	}
}
