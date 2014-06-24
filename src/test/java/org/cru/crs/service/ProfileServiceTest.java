package org.cru.crs.service;

import org.cru.crs.AbstractTestWithDatabaseConnectivity;
import org.cru.crs.model.ProfileEntity;
import org.cru.crs.utils.ClockFactory;
import org.cru.crs.utils.ClockImpl;
import org.joda.time.DateTime;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.UUID;

public class ProfileServiceTest extends AbstractTestWithDatabaseConnectivity
{
	ProfileService profileService;

	ProfileEntity profileEntity;

	@BeforeMethod(alwaysRun=true)
	private void setupConnectionAndService()
	{
		refreshConnection();
		profileService = new ProfileService(sqlConnection, new ClockImpl());

		UUID profileId = UUID.fromString("abcdc217-f918-4503-b3b3-85016f9883c1");
		UUID userId = UUID.fromString("abcdca08-d7bc-4d92-967c-d82d9d312898");

		DateTime birthDate = new DateTime("1898-11-29");
		String campus = "Oxford";
		String city = "Orlando";
		String dormitory = "Hall";
		String email = "c.s.lewis@cru.org";
		String firstName = "Clive";
		String gender = "M";
		String yearInSchool = "Freshman";

		String lastName = "Lewis";
		String phone = "407-826-2000";
		String state = "FL";
		String address1 = "100 Lake Hart Drive";
		String address2 = "MailStop 2400";
		String zip = "32832";

		profileEntity = new ProfileEntity(profileId, userId, birthDate, campus, city, dormitory, email, firstName, gender, yearInSchool, lastName, phone, state, address1, address2, zip);
	}

	@Test(groups = "dbtest")
	public void testCreateGetProfileById()
	{
		try
		{
			profileService.createProfile(profileEntity);
			ProfileEntity getProfileEntity = profileService.getProfileBy(profileEntity.getId());
			Assert.assertNotNull(getProfileEntity);
			assertEquals(getProfileEntity, profileEntity);
		}
		finally
		{
			sqlConnection.rollback();
		}
	}

	@Test(groups = "dbtest")
	public void testCreateGetProfileByUserId()
	{
		try
		{
			profileService.createProfile(profileEntity);

			ProfileEntity getProfileEntity = profileService.getProfileByUser(profileEntity.getUserId());

			Assert.assertNotNull(getProfileEntity);
			assertEquals(getProfileEntity, profileEntity);

			DateTime now = ClockFactory.getInstance().currentDateTime();
			Assert.assertTrue((now.getMillis() - getProfileEntity.getCreatedTimestamp().getMillis()) < 10000) ;
		}
		finally
		{
			sqlConnection.rollback();
		}
	}

	@Test(groups = "dbtest")
	public void testUpdateProfile()
	{
		try
		{
			profileService.createProfile(profileEntity);

			// get profile and assert equals
			ProfileEntity updateProfileEntity = profileService.getProfileBy(profileEntity.getId());
			Assert.assertEquals(updateProfileEntity.getFirstName(), profileEntity.getFirstName());

			// update profile and assert not equals
			updateProfileEntity.setFirstName("Staples");
			profileService.updateProfile(updateProfileEntity);
			updateProfileEntity = profileService.getProfileBy(profileEntity.getId());
			Assert.assertNotEquals(updateProfileEntity.getFirstName(), profileEntity.getFirstName());

			DateTime now = ClockFactory.getInstance().currentDateTime();
			Assert.assertTrue((now.getMillis() - updateProfileEntity.getUpdatedTimestamp().getMillis()) < 10000) ;
		}
		finally
		{
			sqlConnection.rollback();
		}
	}

	@Test(groups = "dbtest")
	public void testDeleteProfile()
	{
		try
		{
			profileService.createProfile(profileEntity);

			// get profile and assert equals
			ProfileEntity deleteProfileEntity = profileService.getProfileBy(profileEntity.getId());
			Assert.assertEquals(deleteProfileEntity.getFirstName(), profileEntity.getFirstName());

			// delete profile and assert null
			profileService.deleteProfile(profileEntity.getId());
			deleteProfileEntity = profileService.getProfileBy(profileEntity.getId());
			Assert.assertNull(deleteProfileEntity);
		}
		finally
		{
			sqlConnection.rollback();
		}
	}

	private void assertEquals(ProfileEntity profileEntity, ProfileEntity profileEntity2)
	{
		Assert.assertEquals(profileEntity.getId(), profileEntity2.getId());
		Assert.assertEquals(profileEntity.getUserId(), profileEntity2.getUserId());

		Assert.assertEquals(profileEntity.getBirthDate().toLocalDate(), profileEntity2.getBirthDate().toLocalDate());
		Assert.assertEquals(profileEntity.getCity(), profileEntity2.getCity());
		Assert.assertEquals(profileEntity.getCampus(), profileEntity2.getCampus());
		Assert.assertEquals(profileEntity.getDormitory(), profileEntity2.getDormitory());
		Assert.assertEquals(profileEntity.getEmail(), profileEntity2.getEmail());
		Assert.assertEquals(profileEntity.getFirstName(), profileEntity2.getFirstName());
		Assert.assertEquals(profileEntity.getGender(), profileEntity2.getGender());
		Assert.assertEquals(profileEntity.getYearInSchool(), profileEntity2.getYearInSchool());
		Assert.assertEquals(profileEntity.getPhone(), profileEntity2.getPhone());
		Assert.assertEquals(profileEntity.getLastName(), profileEntity2.getLastName());
		Assert.assertEquals(profileEntity.getState(), profileEntity2.getState());
		Assert.assertEquals(profileEntity.getAddress1(), profileEntity2.getAddress1());
		Assert.assertEquals(profileEntity.getAddress2(), profileEntity2.getAddress2());
		Assert.assertEquals(profileEntity.getZip(), profileEntity2.getZip());
	}
}
