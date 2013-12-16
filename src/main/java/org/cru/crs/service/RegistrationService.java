package org.cru.crs.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.cru.crs.model.RegistrationEntity;
import org.cru.crs.model.queries.RegistrationQueries;
import org.jboss.logging.Logger;
import org.sql2o.Connection;

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
	
	private Logger logger = Logger.getLogger(RegistrationService.class);

	/*required for Weld*/
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
        registrationEntity.setCompleted(false); //they're just starting, so clearly it's not complete
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

    public void deleteRegistration(UUID registrationId)
	{
    	answerService.deleteAnswersByRegistrationId(registrationId);
    	paymentService.disassociatePaymentsFromRegistration(registrationId);
    	
    	sqlConnection.createQuery(registrationQueries.delete())
						.addParameter("id", registrationId)
						.executeUpdate();
    }

	public boolean isUserRegistered(UUID conferenceId, UUID userId)
	{
		return getRegistrationByConferenceIdUserId(conferenceId, userId) != null;
	}
}
