package org.cru.crs.service;

import org.cru.crs.cdi.SqlConnectionProducer;
import org.cru.crs.model.ProfileEntity;
import org.cru.crs.utils.DateTimeCreaterHelper;
import org.joda.time.DateTime;
import org.sql2o.Connection;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.UUID;

public class ProfileServiceTest
{
	Connection sqlConnection;
	ProfileService profileService;

	ProfileEntity profileEntity;

	@BeforeMethod(alwaysRun = true)
	private void setupConnectionAndService()
	{
		sqlConnection = new SqlConnectionProducer().getTestSqlConnection();

		profileService = new ProfileService(sqlConnection);

		UUID profileId = UUID.fromString("abcdc217-f918-4503-b3b3-85016f9883c1");
		UUID userId = UUID.fromString("abcdca08-d7bc-4d92-967c-d82d9d312898");

		DateTime birthDate = DateTimeCreaterHelper.createDateTime(1898, 11, 29, 00, 00, 00);
		String campus = "Oxford";
		String city = "Orlando";
		String dormitory = "Hall";
		String email = "c.s.lewis@cru.org";
		String firstName = "Clive";
		String gender = "M";
		DateTime graduation = DateTimeCreaterHelper.createDateTime(1923, 5, 15, 00, 00, 00);

		String lastName = "Lewis";
		String phone = "407-826-2000";
		String state = "FL";
		String street = "100 Lake Hart Dr";
		String zip = "32832";

		profileEntity = new ProfileEntity(profileId, userId, birthDate, campus, city, dormitory, email, firstName, gender, graduation, lastName, phone, state, street, zip);
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

		Assert.assertEquals(profileEntity.getBirthDate(), profileEntity2.getBirthDate());
		Assert.assertEquals(profileEntity.getCity(), profileEntity2.getCity());
		Assert.assertEquals(profileEntity.getCampus(), profileEntity2.getCampus());
		Assert.assertEquals(profileEntity.getDormitory(), profileEntity2.getDormitory());
		Assert.assertEquals(profileEntity.getEmail(), profileEntity2.getEmail());
		Assert.assertEquals(profileEntity.getFirstName(), profileEntity2.getFirstName());
		Assert.assertEquals(profileEntity.getGender(), profileEntity2.getGender());
		Assert.assertEquals(profileEntity.getGraduation(), profileEntity2.getGraduation());
		Assert.assertEquals(profileEntity.getPhone(), profileEntity2.getPhone());
		Assert.assertEquals(profileEntity.getLastName(), profileEntity2.getLastName());
		Assert.assertEquals(profileEntity.getState(), profileEntity2.getState());
		Assert.assertEquals(profileEntity.getStreet(), profileEntity2.getStreet());
		Assert.assertEquals(profileEntity.getZip(), profileEntity2.getZip());
	}
}
