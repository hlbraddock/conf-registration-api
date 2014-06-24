package org.cru.crs.api.process;

import org.cru.crs.model.ConferenceEntity;
import org.cru.crs.model.PermissionLevel;
import org.cru.crs.model.UserEntity;

import java.net.URL;

public class PermissionEmail
{
	public static String subject()
	{
		return "Welcome to Formvent!";
	}
	
	public static String body(URL activationUrl, PermissionLevel permissionLevel, ConferenceEntity conferenceEntity, UserEntity permissionGrantor)
	{
		StringBuilder builder = new StringBuilder();
		
		builder.append(createName(permissionGrantor))
				.append("has given you the ability to ")
				.append(abilityDescription(permissionLevel))
				.append("the conference or event called: \"")
				.append(conferenceEntity.getName())
				.append("\".")
				.append("<br /><br />")
				.append("To accept these abilities, please follow the link below.  Formvent will record and remember your abilities on all subsequent visits to our site.")
				.append("<br /><br />")
				.append("Link: ")
				.append(activationUrl.toString());
		
		return builder.toString();
	}
	
	private static String createName(UserEntity permissionGrantor)
	{
		return permissionGrantor.getFirstName() + " " + permissionGrantor.getLastName() + " ";
	}
	
	private static String abilityDescription(PermissionLevel permissionLevel)
	{
		switch(permissionLevel)
		{
			//case CREATOR: should not be granted
			case FULL:
				return "see and fully adminster all administrative privilieges, dates, details & online registraiton settings for "; 
			case UPDATE:
				return "see and update conference dates, details & online registration settings, along with the ability to see and update registration data for "; 
			case VIEW:
				return "view registration data for ";
			//case NONE: should not be granted, it's implied for all users with no other level set.
			default:
				return "";
		}
	}
}
