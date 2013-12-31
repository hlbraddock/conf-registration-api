package org.cru.crs.service;

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

	ProfileQueries profileQueries = new ProfileQueries();

	/*Weld requires a default no args constructor to proxy this object*/
	public ProfileService(){ }

    @Inject
    public ProfileService(Connection sqlConnection)
    {
    	this.sqlConnection = sqlConnection;
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
    	if(profileEntity.getId() == null)
    	{
    		profileEntity.setId(UUID.randomUUID());
    	}
    	
    	sqlConnection.createQuery(profileQueries.insert())
						.addParameter("id", profileEntity.getId())
						.addParameter("userId", profileEntity.getUserId())
						.addParameter("email", profileEntity.getEmail())
						.addParameter("name", profileEntity.getName())
						.addParameter("phone", profileEntity.getPhone() )
						.addParameter("address", profileEntity.getAddress())
						.addParameter("birthDate", profileEntity.getBirthDate())
						.addParameter("gender", profileEntity.getGender())
						.addParameter("campus", profileEntity.getCampus())
						.addParameter("graduation", profileEntity.getGraduation())
						.addParameter("dormitory", profileEntity.getDormitory())
    					.executeUpdate();
    }

	public void updateProfile(ProfileEntity profileEntity)
	{
		sqlConnection.createQuery(profileQueries.update())
				.addParameter("id", profileEntity.getId())
				.addParameter("email", profileEntity.getEmail())
				.addParameter("name", profileEntity.getName())
				.addParameter("phone", profileEntity.getPhone() )
				.addParameter("address", profileEntity.getAddress())
				.addParameter("birthDate", profileEntity.getBirthDate())
				.addParameter("gender", profileEntity.getGender())
				.addParameter("campus", profileEntity.getCampus())
				.addParameter("graduation", profileEntity.getGraduation())
				.addParameter("dormitory", profileEntity.getDormitory())
				.executeUpdate();
	}
}
