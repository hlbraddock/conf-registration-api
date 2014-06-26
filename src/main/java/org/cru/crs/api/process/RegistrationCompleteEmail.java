package org.cru.crs.api.process;

import com.google.common.base.Strings;
import org.cru.crs.api.model.Registration;
import org.cru.crs.model.ConferenceCostsEntity;
import org.cru.crs.model.ConferenceEntity;
import org.cru.crs.model.UserEntity;
import org.cru.crs.utils.HtmlHelper;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.math.BigDecimal;

public class RegistrationCompleteEmail
{
	private static DateTimeFormatter formatter = DateTimeFormat.forPattern("EEEE MMMM dd, yyyy hh:mma");

	public static String subject(String conferenceName)
	{
		return "'" + conferenceName + "' registration";
	}

	public static String body(UserEntity userEntity, Registration registration, ConferenceEntity conferenceEntity, ConferenceCostsEntity conferenceCostsEntity, String registrationUrl)
	{
		StringBuilder builder = new StringBuilder();

		String first = Strings.isNullOrEmpty(userEntity.getFirstName()) ? "" : userEntity.getFirstName();
		builder.append(HtmlHelper.paragraph("Hello " + first + "!"));

		builder.append(HtmlHelper.paragraph("You are registered for the event '" + HtmlHelper.bold(conferenceEntity.getName()) + "'"));

		if(!Strings.isNullOrEmpty(conferenceEntity.getDescription()))
			builder.append(HtmlHelper.paragraph(HtmlHelper.bold("Description: ") + conferenceEntity.getDescription()));

		builder.append(HtmlHelper.paragraph(HtmlHelper.bold("Start Time: ") + formatter.print(conferenceEntity.getEventStartTime())))
				.append(HtmlHelper.paragraph(HtmlHelper.bold("End Time: ") + formatter.print(conferenceEntity.getEventEndTime())));

		if(location(conferenceEntity).length() > 0)
			builder.append(HtmlHelper.paragraph(HtmlHelper.bold("Location: ") + location(conferenceEntity).toString()));

		if(conferenceHasCost(conferenceCostsEntity))
		{
			builder.append(HtmlHelper.paragraph(HtmlHelper.bold("Total Cost: ") + "$" + registration.getTotalDue()));
			builder.append(HtmlHelper.paragraph(HtmlHelper.bold("Total Amount Paid: ") + "$" + registration.getTotalPaid()));

			if(registration.getRemainingBalance().compareTo(BigDecimal.ZERO) > 0 && conferenceCostsEntity.isAcceptCreditCards())
			{
				builder.append(HtmlHelper.paragraph(HtmlHelper.bold("Balance Due: ") + " $" + registration.getRemainingBalance()));
				builder.append(HtmlHelper.paragraph("You can pay the balance due here " + HtmlHelper.href(registrationUrl, "Online Registration Payment Page")));
			}
		}

		builder.append(HtmlHelper.paragraph("We look forward to seeing you there!"));

		return builder.toString();
	}

	private static StringBuilder location(ConferenceEntity conferenceEntity)
	{
		StringBuilder builder = new StringBuilder();

		if(!Strings.isNullOrEmpty(conferenceEntity.getLocationAddress()))
			builder.append(conferenceEntity.getLocationAddress() + ", ");

		if(!Strings.isNullOrEmpty(conferenceEntity.getLocationCity()))
			builder.append(conferenceEntity.getLocationCity() + ", ");

		if(!Strings.isNullOrEmpty(conferenceEntity.getLocationState()))
			builder.append(conferenceEntity.getLocationState() + ", ");

		if(!Strings.isNullOrEmpty(conferenceEntity.getLocationZipCode()))
			builder.append(conferenceEntity.getLocationZipCode());

		return builder;
	}

	private static boolean conferenceHasCost(ConferenceCostsEntity conferenceCostsEntity)
	{
		if(conferenceCostsEntity == null)
			return false;

		return conferenceCostsEntity.getBaseCost() != null && conferenceCostsEntity.getBaseCost().compareTo(BigDecimal.ZERO) > 0;
	}
}
