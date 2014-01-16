package org.cru.crs.payment.authnet.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Preconditions;

public class CreditCard
{

	// TODO: change this to a list if not used in any templates
	public static final String[] VALID_PATTERNS = { "MMyy", "MM/yy", "MM-yy", "MMyyyy", "MM/yyyy", "MM-yyyy",
			"yyyy-MM-dd", "yyyy/MM/dd" };

	private String pattern = "MM/yyyy";
	private Calendar calendar = Calendar.getInstance();;

	private String cardNumber;
	private Date expirationDate;
	private String cardCode;

	public Map<String, String> getParamMap()
	{
		Map<String, String> request = new HashMap<String, String>();
		request.put("x_card_code", cardCode);
		request.put("x_card_num", cardNumber);
		request.put("x_exp_date", getFormattedExpirationDate());
		return request;
	}

	public String getFormattedExpirationDate()
	{
		if(expirationDate == null) return "";
		validatePattern();
		DateFormat expirationFormat = new SimpleDateFormat(pattern);
		expirationFormat.setCalendar(calendar);
		return expirationFormat.format(expirationDate);
	}

	// FUTURE: abstract this into a plugable converter (maybe same api as a jsf
	// converter)
	public String getMaskedCardNumber()
	{
		if (cardNumber == null || cardNumber.length() <= 4)
			return null;
		else
			return "************" + cardNumber.substring(cardNumber.length() - 4, cardNumber.length());
	}

	private void validatePattern()
	{
		Preconditions.checkArgument(Arrays.asList(VALID_PATTERNS).indexOf(pattern) != -1, "Invalid date pattern: " + pattern);
	}

	public String getCardNumber()
	{
		return cardNumber;
	}

	public void setCardNumber(String cardNumber)
	{
		this.cardNumber = cardNumber;
	}

	public Date getExpirationDate()
	{
		return expirationDate;
	}

	public void setExpirationDate(Date expireDate)
	{
		this.expirationDate = expireDate;
	}

	public String getCardCode()
	{
		return cardCode;
	}

	public void setCardCode(String cardCode)
	{
		this.cardCode = cardCode;
	}

	public Calendar getCalendar()
	{
		return calendar;
	}

	public void setCalendar(Calendar calendar)
	{
		this.calendar = calendar;
	}

	public String getPattern()
	{
		return pattern;
	}

	public void setPattern(String pattern)
	{
		this.pattern = pattern;
	}

	public static CreditCard createTestVisaCard()
	{
		CreditCard card = createTestCard();
		card.setCardNumber("4007000000027");
		card.setCardCode("124");
		return card;
	}

	public static CreditCard createTestDiscoverCard()
	{
		CreditCard card = createTestCard();
		card.setCardNumber("6011000000000012");
		card.setCardCode("124");
		return card;
	}

	public static CreditCard createTestMasterCardCard()
	{
		CreditCard card = createTestCard();
		card.setCardNumber("5424000000000015");
		card.setCardCode("124");
		return card;
	}

	public static CreditCard createTestAmericanExpressCard()
	{
		CreditCard card = createTestCard();
		card.setCardNumber("370000000000002");
		card.setCardCode("124");
		return card;
	}

	public static CreditCard createTestErrorCard()
	{
		CreditCard card = createTestCard();
		card.setCardNumber("4222222222222");
		card.setCardCode("124");
		return card;
	}

	private static CreditCard createTestCard()
	{
		CreditCard card = new CreditCard();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, 1);
		card.setExpirationDate(calendar.getTime());
		return card;
	}

}
