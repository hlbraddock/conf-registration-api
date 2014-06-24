package org.cru.crs.service;

import org.ccci.util.time.Clock;
import org.cru.crs.model.ProfileEntity;
import org.cru.crs.model.queries.ProfileQueries;
import org.sql2o.Connection;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.UUID;

@RequestScoped
public class ProfileService
{
	Connection sqlConnection;
	Clock clock;

	ProfileQueries profileQueries = new ProfileQueries();

	/*Weld requires a default no args constructor to proxy this object*/
	public ProfileService(){ }

    @Inject
    public ProfileService(Connection sqlConnection, Clock clock)
    {
    	this.sqlConnection = sqlConnection;
		this.clock = clock;
    }

	public ProfileEntity getProfileBy(UUID uuid)
	{
		return sqlConnection.createQuery(profileQueries.selectById())
				.addParameter("id", uuid)
				.setAutoDeriveColumnNames(true)
				.executeAndFetchFirst(ProfileEntity.class);
	}

	public ProfileEntity getProfileByUser(UUID uuid)
	{
		return sqlConnection.createQuery(profileQueries.selectByUserId())
				.addParameter("userId", uuid)
				.setAutoDeriveColumnNames(true)
				.executeAndFetchFirst(ProfileEntity.class);
	}

	public void createProfile(ProfileEntity profileEntity)
    {
		profileEntity.setCreatedTimestamp(clock.currentDateTime());

    	if(profileEntity.getId() == null)
    	{
    		profileEntity.setId(UUID.randomUUID());
    	}

		sqlConnection.createQuery(profileQueries.insert())
				.addParameter("id", profileEntity.getId())
				.addParameter("userId", profileEntity.getUserId())
				.addParameter("email", profileEntity.getEmail())
				.addParameter("firstName", profileEntity.getFirstName())
				.addParameter("lastName", profileEntity.getLastName())
				.addParameter("phone", profileEntity.getPhone())
				.addParameter("address1", profileEntity.getAddress1())
				.addParameter("address2", profileEntity.getAddress2())
				.addParameter("city", profileEntity.getCity())
				.addParameter("state", profileEntity.getState())
				.addParameter("zip", profileEntity.getZip())
				.addParameter("birthDate", profileEntity.getBirthDate() == null ? profileEntity.getBirthDate() : profileEntity.getBirthDate().toDate())
				.addParameter("gender", profileEntity.getGender())
				.addParameter("campus", profileEntity.getCampus())
				.addParameter("yearInSchool", profileEntity.getYearInSchool())
				.addParameter("dormitory", profileEntity.getDormitory())
				.addParameter("createdTimestamp", profileEntity.getCreatedTimestamp())
				.executeUpdate();
	}

	public void updateProfile(ProfileEntity profileEntity)
	{
		profileEntity.setUpdatedTimestamp(clock.currentDateTime());

		sqlConnection.createQuery(profileQueries.update())
				.addParameter("id", profileEntity.getId())
				.addParameter("email", profileEntity.getEmail())
				.addParameter("firstName", profileEntity.getFirstName())
				.addParameter("lastName", profileEntity.getLastName())
				.addParameter("phone", profileEntity.getPhone() )
				.addParameter("address1", profileEntity.getAddress1())
				.addParameter("address2", profileEntity.getAddress2())
				.addParameter("city", profileEntity.getCity())
				.addParameter("state", profileEntity.getState())
				.addParameter("zip", profileEntity.getZip())
				.addParameter("birthDate", profileEntity.getBirthDate() == null ? profileEntity.getBirthDate() : profileEntity.getBirthDate().toDate())
				.addParameter("gender", profileEntity.getGender())
				.addParameter("campus", profileEntity.getCampus())
				.addParameter("yearInSchool", profileEntity.getYearInSchool())
				.addParameter("dormitory", profileEntity.getDormitory())
				.addParameter("updatedTimestamp", profileEntity.getUpdatedTimestamp())
				.executeUpdate();
	}

	public void deleteProfile(UUID uuid)
	{
		sqlConnection.createQuery(profileQueries.delete())
				.addParameter("id", uuid)
				.executeUpdate();
	}

	public void deleteProfileByUserId(UUID userId)
	{
		deleteProfile(getProfileByUser(userId).getId());
	}
}
