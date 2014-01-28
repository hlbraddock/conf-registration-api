package org.cru.crs.utils;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;

import com.google.common.collect.Sets;
import org.ccci.util.mail.EmailAddress;
import org.ccci.util.mail.MailMessage;
import org.ccci.util.mail.MailMessageFactory;
import org.ccci.util.strings.Strings;

import java.util.Set;

/**
 * @author Lee Braddock
 */
public class MailService
{
	CrsProperties crsProperties;

	@Inject
	public MailService(CrsProperties crsProperties)
	{
		this.crsProperties = crsProperties;
	}

	public void send(String from, String to, String subject, String body) throws MessagingException
	{
		Set<String> recipients = Sets.newHashSet();
		recipients.add(to);
		send(from, recipients, subject, body);
	}

	public void send(String from, Set<String> to, String subject, String body) throws MessagingException
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
		for(String recipient : to)
			mailMessage.addTo(EmailAddress.valueOf(recipient));
		mailMessage.setMessage(subject, body, true);
		mailMessage.sendToAll();
	}
}
