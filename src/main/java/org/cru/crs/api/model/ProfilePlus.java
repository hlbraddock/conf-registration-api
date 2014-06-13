package org.cru.crs.api.model;

import org.cru.crs.auth.AuthenticationProviderType;
import org.joda.time.DateTime;

import java.util.UUID;

public class ProfilePlus extends Profile
{
	private AuthenticationProviderType authProviderType;

	public ProfilePlus()
	{
	}

	public ProfilePlus(UUID id, UUID userId, String address1, String address2, DateTime birthDate, String campus, String city, String dormitory, String email, String firstName, String gender, String lastName, String phone, String state, String yearInSchool, String zip, AuthenticationProviderType authProviderType)
	{
		super(id, userId, address1, address2, birthDate, campus, city, dormitory, email, firstName, gender, lastName, phone, state, yearInSchool, zip);

		this.authProviderType = authProviderType;
	}

	public ProfilePlus(Profile profile, AuthenticationProviderType authenticationProviderType)
	{
		this(profile.getId(),
			profile.getUserId(),
			profile.getAddress1(),
			profile.getAddress2(),
			profile.getBirthDate(),
			profile.getCampus(),
			profile.getCity(),
			profile.getDormitory(),
			profile.getEmail(),
			profile.getFirstName(),
			profile.getGender(),
			profile.getLastName(),
			profile.getPhone(),
			profile.getState(),
			profile.getYearInSchool(),
			profile.getZip(),
			authenticationProviderType);
	}

	public AuthenticationProviderType getAuthProviderType()
	{
		return authProviderType;
	}

	public void setAuthProviderType(AuthenticationProviderType authProviderType)
	{
		this.authProviderType = authProviderType;
	}
}
