package org.cru.crs.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;

import org.cru.crs.auth.UnauthorizedException;
import org.cru.crs.auth.model.CrsApplicationUser;
import org.cru.crs.model.RegistrationEntity;
import org.cru.crs.model.queries.RegistrationQueries;
import org.jboss.logging.Logger;
import org.sql2o.Sql2o;

/**
 * User: lee.braddock
 */
public class RegistrationService
{

	Sql2o sql;
	
	AnswerService answerService;
	PaymentService paymentService;
	
	RegistrationQueries registrationQueries = new RegistrationQueries();
	
	private Logger logger = Logger.getLogger(RegistrationService.class);

	@Inject
    public RegistrationService(Sql2o sql, AnswerService answerService, PaymentService paymentService)
    {
		this.sql = sql;
		this.answerService = answerService;
		this.paymentService = paymentService;
    }

	public Set<RegistrationEntity> fetchAllRegistrations(UUID conferenceId, CrsApplicationUser crsApplicationUser)
	{
		List<RegistrationEntity> registrations = sql.createQuery(registrationQueries.selectAllForConference())
														.addParameter("conferenceId", conferenceId)
														.setAutoDeriveColumnNames(true)
														.executeAndFetch(RegistrationEntity.class);

		return new HashSet<RegistrationEntity>(registrations);		
	}

	public RegistrationEntity getRegistrationByConferenceIdUserId(UUID conferenceId, UUID userId, CrsApplicationUser crsApplicationUser)
	{

		return sql.createQuery(registrationQueries.selectByUserIdConferenceId())
													.addParameter("conferenceId", conferenceId)
													.addParameter("userId", userId)
													.setAutoDeriveColumnNames(true)
													.executeAndFetchFirst(RegistrationEntity.class);
	}

	public RegistrationEntity getRegistrationBy(UUID registrationId)
	{
		return sql.createQuery(registrationQueries.selectById())
												.addParameter("id", registrationId)
												.setAutoDeriveColumnNames(true)
												.executeAndFetchFirst(RegistrationEntity.class);
    }

    public void createNewRegistration(RegistrationEntity registrationEntity, CrsApplicationUser crsApplicationUser) throws UnauthorizedException
	{
		if(isUserRegisteredForConference(crsApplicationUser, registrationEntity.getConferenceId()))
		{
			throw new UnauthorizedException();
		}

        registrationEntity.setCompleted(false); //they're just starting, so clearly it's not complete
		if(registrationEntity.getId() == null) registrationEntity.setId(UUID.randomUUID());
			
		sql.createQuery(registrationQueries.insert())
				.addParameter("id", registrationEntity.getId())
				.addParameter("conferenceId", registrationEntity.getConferenceId())
				.addParameter("userId", registrationEntity.getUserId())
				.addParameter("completed", registrationEntity.getCompleted())
				.executeUpdate();
    }

	private boolean isUserRegisteredForConference(CrsApplicationUser crsApplicationUser, UUID conferenceId)
	{
		return (getRegistrationByConferenceIdUserId(conferenceId, crsApplicationUser.getId(), crsApplicationUser) != null);
	}

	public void updateRegistration(RegistrationEntity registrationEntity)
	{
		sql.createQuery(registrationQueries.update())
					.addParameter("id", registrationEntity.getId())
					.addParameter("conferenceId", registrationEntity.getConferenceId())
					.addParameter("userId", registrationEntity.getUserId())
					.addParameter("completed", registrationEntity.getCompleted())
					.executeUpdate();
    }

    public void deleteRegistration(RegistrationEntity registrationEntity)
	{
    	answerService.deleteAnswersByRegistrationId(registrationEntity.getId());
    	paymentService.disassociatePaymentsFromRegistration(registrationEntity.getId());
    	
		sql.createQuery(registrationQueries.delete())
				.addParameter("id", registrationEntity.getId())
				.executeUpdate();
    }
}
