package org.cru.crs.payment.trustcommerce;

import com.google.common.base.Strings;
import com.trustcommerce.TCLink;
import org.cru.crs.api.model.Payment;
import org.cru.crs.payment.trustcommerce.domain.Action;
import org.cru.crs.payment.trustcommerce.domain.Request;
import org.cru.crs.payment.trustcommerce.domain.Response;
import org.cru.crs.payment.trustcommerce.domain.Status;
import org.cru.crs.payment.trustcommerce.domain.TrustCommerceException;
import org.cru.crs.utils.CrsProperties;
import org.jboss.logging.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import javax.inject.Inject;
import java.util.Map;
import java.util.Properties;

public class TrustCommercePaymentProcess
{
	private CrsProperties crsProperties;

	private Logger logger = Logger.getLogger(getClass());

	@Inject
	public TrustCommercePaymentProcess(CrsProperties crsProperties)
	{
		this.crsProperties = crsProperties;
	}

	public String processCreditCardTransaction(Payment payment) throws TrustCommerceException
	{
		Map<String,String> response = processCreditCardTransaction(payment, Action.SALE);

		return response.get(Response.TRANSID.getValue());
	}

	public Map<String,String> processCreditCardTransaction(Payment payment, Action action) throws TrustCommerceException
	{
		Properties properties = paymentTo(payment, action);

		TCLink tcLink = new TCLink();

		@SuppressWarnings("unchecked")
		Map<String, String> response = tcLink.Submit(properties);

		logger.info("trust commerce response : " + response);

		String status = response.get(Response.STATUS.getValue());

		if (Strings.isNullOrEmpty(status))
			throw new TrustCommerceException("no return status");

		if (!(status.equalsIgnoreCase(Status.ACCEPTED.getValue()) || status.equalsIgnoreCase(Status.APPROVED.getValue())))
			throw new TrustCommerceException(response);

		return response;
	}

	private final static String expirationDateFormat = "MMYY";

	private Properties paymentTo(Payment payment, Action action)
	{
		Properties properties = new Properties();

		properties.put(Request.CUSTOMER_ID.getValue(), crsProperties.getProperty("trustCommerceCustomerId"));
		properties.put(Request.PASSWORD.getValue(), crsProperties.getProperty("trustCommercePassword"));

		properties.put(Request.CREDIT_CARD.getValue(), payment.getCreditCard().getNumber());

		String expDate = DateTimeFormat.forPattern(expirationDateFormat).print(new DateTime()
				.withYear(Integer.parseInt(payment.getCreditCard().getExpirationYear()))
				.withMonthOfYear(Integer.parseInt(payment.getCreditCard().getExpirationMonth())));

		properties.put(Request.EXPIRATION.getValue(), expDate);

		properties.put(Request.AMOUNT.getValue(), payment.getCreditCard().getExpirationMonth() + payment.getAmount());
		properties.put(Request.ACTION.getValue(), action.getValue());

		return properties;
	}
}
