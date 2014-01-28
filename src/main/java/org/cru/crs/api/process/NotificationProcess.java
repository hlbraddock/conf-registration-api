package org.cru.crs.api.process;

import com.beust.jcommander.internal.Sets;
import com.google.common.base.Strings;
import org.cru.crs.api.model.Payment;
import org.cru.crs.api.model.Registration;
import org.cru.crs.auth.model.CrsApplicationUser;
import org.cru.crs.model.ConferenceCostsEntity;
import org.cru.crs.model.ConferenceEntity;
import org.cru.crs.model.ProfileEntity;
import org.cru.crs.model.UserEntity;
import org.cru.crs.service.ConferenceCostsService;
import org.cru.crs.service.ConferenceService;
import org.cru.crs.service.ProfileService;
import org.cru.crs.service.UserService;
import org.cru.crs.utils.CrsProperties;
import org.cru.crs.utils.MailService;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

public class NotificationProcess
{
	MailService mailService;
	UserService userService;
	ConferenceService conferenceService;
	ConferenceCostsService conferenceCostsService;
	ProfileService profileService;

	CrsProperties properties;

	RetrieveRegistrationProcess retrieveRegistrationProcess;

	Logger logger = Logger.getLogger(NotificationProcess.class);

	@Inject
	public NotificationProcess(MailService mailService, UserService userService, ConferenceService conferenceService, ConferenceCostsService conferenceCostsService, ProfileService profileService, CrsProperties properties, RetrieveRegistrationProcess retrieveRegistrationProcess)
	{
		this.mailService = mailService;
		this.userService = userService;
		this.conferenceService = conferenceService;
		this.conferenceCostsService = conferenceCostsService;
		this.profileService = profileService;

		this.properties = properties;

		this.retrieveRegistrationProcess = retrieveRegistrationProcess;
	}

	public void registrationComplete(CrsApplicationUser loggedInUser, Registration registration, ConferenceEntity conferenceEntity)
	{
		if(isServiceDisabled())
			return;

		// re-retrieve the registration so as to ensure you have payment info
		registration = retrieveRegistrationProcess.get(registration.getId());

		UserEntity userEntity = userService.getUserById(loggedInUser.getId());

		ConferenceCostsEntity conferenceCostsEntity = conferenceCostsService.fetchBy(conferenceEntity.getConferenceCostsId());

		// send the registrant an email notifying them of the conference and registration completion
		try
		{
			String registrationUrl = getRegistrationUrl(registration);

			Set<String> recipientEmails = getUserEmails(userEntity);

			mailService.send(properties.getProperty("crsEmail"), recipientEmails,
					RegistrationCompleteEmail.subject(conferenceEntity.getName()),
					RegistrationCompleteEmail.body(userEntity, registration, conferenceEntity, conferenceCostsEntity, registrationUrl));
		}
		catch (Exception e)
		{
			logger.error("Could not send registration complete email for user " + userEntity.getId(), e);
		}
	}

	public void paymentReceipt(Payment payment, CrsApplicationUser loggedInUser)
	{
		if(isServiceDisabled())
			return;

		// retrieve the registration so as to ensure you have payment info
		Registration registration = retrieveRegistrationProcess.get(payment.getRegistrationId());

		// on first payment, user will be notified as part of registration complete
		if(registration.getPastPayments().size() <= 1)
			if(Strings.isNullOrEmpty(testRecipientEmail()))
				return;

		ConferenceEntity conferenceEntity = conferenceService.fetchConferenceBy(registration.getConferenceId());

		UserEntity userEntity = userService.getUserById(loggedInUser.getId());

		// send the registrant an email notifying them of payment due
		try
		{
			String registrationUrl = getRegistrationUrl(registration);

			Set<String> recipientEmails = getUserEmails(userEntity);

			mailService.send(properties.getProperty("crsEmail"), recipientEmails,
					PaymentReceiptEmail.subject(conferenceEntity.getName()),
					PaymentReceiptEmail.body(userEntity, registration, conferenceEntity, payment, registrationUrl));
		}
		catch (Exception e)
		{
			logger.error("Could not send payment receipt email for user " + userEntity.getId(), e);
		}
	}

	private String getRegistrationUrl(Registration registration)
	{
		String url = properties.getProperty("clientUrl") + properties.getProperty("registerUrlPath") + "/" + registration.getId();

		try
		{
			return new URL(url).toString();
		}
		catch (MalformedURLException e)
		{
			logger.error("Error constructing url from " + url, e);
			return url;
		}
	}

	private Set<String> getUserEmails(UserEntity userEntity)
	{
		Set<String> emails = Sets.newHashSet();

		if(!Strings.isNullOrEmpty(testRecipientEmail()))
		{
			emails.add(testRecipientEmail());
			return emails;
		}

		emails.add(userEntity.getEmailAddress());

		ProfileEntity profileEntity = profileService.getProfileByUser(userEntity.getId());
		if(profileEntity != null)
			emails.add(profileEntity.getEmail());

		return emails;
	}

	private boolean isServiceDisabled()
	{
		return properties.getNonNullProperty("notificationServiceDisabled").equals("true");
	}

	private String testRecipientEmail()
	{
		return properties.getNonNullProperty("notificationServiceRecipientEmail");
	}
}
