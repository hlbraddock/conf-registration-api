package org.cru.crs.utils;

import org.ccci.util.mail.EmailAddress;
import org.ccci.util.mail.MailMessage;
import org.ccci.util.mail.MailMessageFactory;
import org.ccci.util.strings.Strings;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;

/**
 * @author Lee Braddock
 */
public class MailService
{
	@Inject
	CrsProperties crsProperties;

	public void send(String from, String to, String subject, String body) throws MessagingException
	{
		String mailServer = crsProperties.getProperty("mailServer");
		String mailServerUsername = crsProperties.getProperty("mailServerUsername");
		String mailServerPassword = crsProperties.getProperty("mailServerPassword");

		PasswordAuthentication passwordAuthentication = null;
		if(!Strings.isEmpty(mailServerUsername))
			passwordAuthentication = new PasswordAuthentication(mailServerUsername, mailServerPassword);

		MailMessageFactory mailMessageFactory = new MailMessageFactory(mailServer, passwordAuthentication);

		MailMessage mailMessage = mailMessageFactory.createApplicationMessage();

		mailMessage.setFrom(EmailAddress.valueOf(from));
		mailMessage.addTo(EmailAddress.valueOf(to));
		mailMessage.setMessage(subject, body, true);
		mailMessage.sendToAll();
	}
}
