package org.cru.crs.service;

import org.cru.crs.auth.model.CrsApplicationUser;
import org.cru.crs.model.RegistrationEntity;
import org.cru.crs.model.queries.RegistrationQueries;
import org.sql2o.Connection;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * User: lee.braddock
 */

@RequestScoped
public class RegistrationService
{
	org.sql2o.Connection sqlConnection;
	
	AnswerService answerService;
	PaymentService paymentService;
	
	RegistrationQueries registrationQueries = new RegistrationQueries();
	
	/*Weld requires a default no args constructor to proxy this object*/
	public RegistrationService(){ }

	@Inject
    public RegistrationService(Connection sqlConnection, AnswerService answerService, PaymentService paymentService)
    {
		this.sqlConnection = sqlConnection;
		this.answerService = answerService;
		this.paymentService = paymentService;
    }

	public Set<RegistrationEntity> fetchAllRegistrations(UUID conferenceId)
	{
		List<RegistrationEntity> registrations = sqlConnection.createQuery(registrationQueries.selectAllForConference())
														.addParameter("conferenceId", conferenceId)
														.setAutoDeriveColumnNames(true)
														.executeAndFetch(RegistrationEntity.class);

		return new HashSet<RegistrationEntity>(registrations);
	}

	public Integer fetchRegistrationCount(UUID conferenceId)
	{
		return sqlConnection.createQuery(registrationQueries.selectCountForConference())
				.addParameter("conferenceId", conferenceId)
				.setAutoDeriveColumnNames(true)
				.executeScalar(Integer.class);
	}

	public Integer fetchCompletedRegistrationCount(UUID conferenceId)
	{
		return sqlConnection.createQuery(registrationQueries.selectCompletedCountForConference())
				.addParameter("conferenceId", conferenceId)
				.setAutoDeriveColumnNames(true)
				.executeScalar(Integer.class);
	}

	public RegistrationEntity getRegistrationByConferenceIdUserId(UUID conferenceId, UUID userId)
	{

		return sqlConnection.createQuery(registrationQueries.selectByUserIdConferenceId())
													.addParameter("conferenceId", conferenceId)
													.addParameter("userId", userId)
													.setAutoDeriveColumnNames(true)
													.executeAndFetchFirst(RegistrationEntity.class);
	}

	public RegistrationEntity getRegistrationBy(UUID registrationId)
	{
		return sqlConnection.createQuery(registrationQueries.selectById())
												.addParameter("id", registrationId)
												.setAutoDeriveColumnNames(true)
												.executeAndFetchFirst(RegistrationEntity.class);
    }

    public void createNewRegistration(RegistrationEntity registrationEntity)
	{
		if(registrationEntity.getId() == null) registrationEntity.setId(UUID.randomUUID());
			
		sqlConnection.createQuery(registrationQueries.insert())
						.addParameter("id", registrationEntity.getId())
						.addParameter("conferenceId", registrationEntity.getConferenceId())
						.addParameter("userId", registrationEntity.getUserId())
						.addParameter("totalDue", registrationEntity.getTotalDue())
						.addParameter("completed", registrationEntity.getCompleted())
						.addParameter("completedTimestamp", registrationEntity.getCompletedTimestamp())
						.executeUpdate();
    }

	public void updateRegistration(RegistrationEntity registrationEntity)
	{
		sqlConnection.createQuery(registrationQueries.update())
						.addParameter("id", registrationEntity.getId())
						.addParameter("conferenceId", registrationEntity.getConferenceId())
						.addParameter("userId", registrationEntity.getUserId())
						.addParameter("totalDue", registrationEntity.getTotalDue())
						.addParameter("completed", registrationEntity.getCompleted())
						.addParameter("completedTimestamp", registrationEntity.getCompletedTimestamp())
						.executeUpdate();
    }

    public void deleteRegistration(UUID registrationId, CrsApplicationUser loggedInAdmin)
	{
    	answerService.deleteAnswersByRegistrationId(registrationId);
    	paymentService.disassociatePaymentsFromRegistration(registrationId, loggedInAdmin);
    	
    	sqlConnection.createQuery(registrationQueries.delete())
						.addParameter("id", registrationId)
						.executeUpdate();
    }

	public boolean isUserRegistered(UUID conferenceId, UUID userId)
	{
		return getRegistrationByConferenceIdUserId(conferenceId, userId) != null;
	}
}
