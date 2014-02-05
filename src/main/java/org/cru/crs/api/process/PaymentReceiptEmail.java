package org.cru.crs.api.process;

import com.google.common.base.Strings;
import org.cru.crs.api.model.Payment;
import org.cru.crs.api.model.Registration;
import org.cru.crs.model.ConferenceEntity;
import org.cru.crs.model.UserEntity;
import org.cru.crs.utils.HtmlHelper;

import java.math.BigDecimal;

public class PaymentReceiptEmail
{
	public static String subject(String conferenceName)
	{
		return "'" + conferenceName + "' registration payment receipt";
	}

	public static String body(UserEntity userEntity, Registration registration, ConferenceEntity conferenceEntity, Payment payment, String registrationUrl)
	{
		StringBuilder builder = new StringBuilder();

		String first = Strings.isNullOrEmpty(userEntity.getFirstName()) ? "" : userEntity.getFirstName();

		builder.append(HtmlHelper.paragraph("Hello " + first + "!"));

		builder.append(HtmlHelper.paragraph("Thank you for your recent payment of $" + payment.getAmount() + " for the '" + HtmlHelper.bold(conferenceEntity.getName()) + "' event."));

		builder.append(HtmlHelper.paragraph(HtmlHelper.bold(HtmlHelper.underline("Cost and Payment Overview"))));
		builder.append(HtmlHelper.paragraph(HtmlHelper.bold("Total Cost: ") + "$" + registration.getTotalDue()));
		builder.append(HtmlHelper.paragraph(HtmlHelper.bold("Total Amount Paid: ") + "$" + registration.getTotalPaid()));

		if(registration.getOutstandingBalance().compareTo(BigDecimal.ZERO) > 0)
		{
			builder.append(HtmlHelper.paragraph(HtmlHelper.bold("Balance Due: ") + " $" + registration.getOutstandingBalance()));
			builder.append(HtmlHelper.paragraph("You can pay the balance due online at " + HtmlHelper.href(registrationUrl, "Online Registration Payment Page")));
		}

		return builder.toString();
	}
}
