package org.cru.crs.api;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.ccci.util.time.Clock;
import org.cru.crs.api.model.Answer;
import org.cru.crs.api.model.Conference;
import org.cru.crs.api.model.Payment;
import org.cru.crs.api.model.Registration;
import org.cru.crs.auth.CrsUserService;
import org.cru.crs.auth.UnauthorizedException;
import org.cru.crs.auth.model.CrsApplicationUser;
import org.cru.crs.model.ConferenceEntity;
import org.cru.crs.model.PaymentEntity;
import org.cru.crs.model.RegistrationEntity;
import org.cru.crs.payment.authnet.AuthnetPaymentProcess;
import org.cru.crs.service.ConferenceService;
import org.cru.crs.service.PaymentService;
import org.cru.crs.service.RegistrationService;
import org.cru.crs.utils.IdComparer;
import org.cru.crs.utils.Simply;
import org.jboss.logging.Logger;

@Stateless
@Path("/registrations/{registrationId}")
public class RegistrationResource
{
	@Inject RegistrationService registrationService;
	@Inject ConferenceService conferenceService;
	@Inject CrsUserService userService;
    @Inject PaymentService paymentService;
    @Inject Clock clock; 
    
    @Inject AuthnetPaymentProcess paymentProcess;
    
	@Context HttpServletRequest request;

	private Logger logger = Logger.getLogger(RegistrationResource.class);

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRegistration(@PathParam(value = "registrationId") UUID registrationId, @HeaderParam(value = "Authorization") String authCode)
	{
		try
		{
			logger.info("get registration entity " + registrationId + " and auth code " + authCode);

			CrsApplicationUser crsLoggedInUser = userService.getLoggedInUser(authCode);

			RegistrationEntity requestedRegistration = registrationService.getRegistrationBy(registrationId, crsLoggedInUser);

			logger.info("get registration is " + requestedRegistration);

			if(requestedRegistration == null) return Response.status(Status.NOT_FOUND).build();

			logger.info("get registration entity");
			Simply.logObject(Registration.fromJpa(requestedRegistration), RegistrationResource.class);

			Registration registration = Registration.fromJpa(requestedRegistration);
			
			logger.info("get registration");
			Simply.logObject(registration, RegistrationResource.class);

			return Response.ok(registration).build();
		}
		catch(UnauthorizedException e)
		{
			return Response.status(Status.UNAUTHORIZED).build();
		}
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateRegistration(Registration registration, @PathParam(value = "registrationId") UUID registrationId, @HeaderParam(value = "Authorization") String authCode) throws URISyntaxException
	{
		try
		{
			CrsApplicationUser crsLoggedInUser = userService.getLoggedInUser(authCode);

			if(IdComparer.idsAreNotNullAndDifferent(registrationId, registration.getId()))
			{
				return Response.status(Status.BAD_REQUEST).build();
			}
			
			if(registration.getId() == null || registrationId == null)
			{
				return Response.status(Status.BAD_REQUEST).build();
			}
			
			logger.info("update registration");
			Simply.logObject(registration, RegistrationResource.class);

			ConferenceEntity conferenceEntity = conferenceService.fetchConferenceBy(registration.getConferenceId());

			if(conferenceEntity == null)
			{
				return Response.status(Status.BAD_REQUEST).build();
			}
			
			boolean createdNewRegistration = false;
			// create the entity if none exists
			if(registrationService.getRegistrationBy(registrationId, crsLoggedInUser) == null)
			{
				createdNewRegistration = true;
				
				RegistrationEntity registrationEntity = registration.toJpaRegistrationEntity(conferenceEntity);

				logger.info("update registration creating");

				registrationService.createNewRegistration(registrationEntity, crsLoggedInUser);


			}
			else
			{
				registrationService.updateRegistration(registration.toJpaRegistrationEntity(conferenceEntity), crsLoggedInUser);
			}
			
			if(registration.getCurrentPayment() != null && registration.getCurrentPayment().isReadyToProcess())
			{	
				try
				{
					processPayment(registration.getCurrentPayment(), crsLoggedInUser);
				}
				catch(IOException e)
				{
					return Response.status(502).build();
				}
			}

			if(createdNewRegistration)
			{
				return Response.status(Status.CREATED)
						.location(new URI("/registrations/" + registration.getId()))
						.entity(registration)
						.build();
			}
			else return Response.noContent().build();
		}
		catch(UnauthorizedException e)
		{
			return Response.status(Status.UNAUTHORIZED).build();
		}
	}

	@DELETE
	public Response deleteRegistration(@PathParam(value = "registrationId") UUID registrationId, @HeaderParam(value = "Authorization") String authCode)
	{
		try
		{
			CrsApplicationUser crsLoggedInUser = userService.getLoggedInUser(authCode);

			RegistrationEntity registrationEntity = registrationService.getRegistrationBy(registrationId, crsLoggedInUser);

			if(registrationEntity == null)
			{
				return Response.status(Status.BAD_REQUEST).build();
			}
			logger.info("delete registration entity");
			Simply.logObject(Registration.fromJpa(registrationEntity), RegistrationResource.class);

			/*FIXME: expect this to break at run time as the registration may have payments associated with it
			 * and that is enforced at the database level.  We need to decide if registrations should be allowed
			 * to be deleted, b/c if so, payments will also be deleted.  That could be a really bad idea.  It's
			 * probably better to set some status on the registration to "delete" it. 
			 */
			registrationService.deleteRegistration(registrationEntity, crsLoggedInUser);

			return Response.noContent().build();
		}
		catch(UnauthorizedException e)
		{
			return Response.status(Status.UNAUTHORIZED).build();
		}
	}

	@POST
	@Path("/answers")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createAnswer(Answer newAnswer, @PathParam(value = "registrationId") UUID registrationId, @HeaderParam(value = "Authorization") String authCode) throws URISyntaxException
	{
		try
		{
			CrsApplicationUser crsLoggedInUser = userService.getLoggedInUser(authCode);

			if(IdComparer.idsAreNotNullAndDifferent(registrationId, newAnswer.getRegistrationId()))
				return Response.status(Status.BAD_REQUEST).build();

			if(newAnswer.getId() == null) newAnswer.setId(UUID.randomUUID());

			logger.info("create answer");
			Simply.logObject(newAnswer, RegistrationResource.class);

			RegistrationEntity registrationEntity = registrationService.getRegistrationBy(registrationId, crsLoggedInUser);

			if(registrationEntity == null) return Response.status(Status.BAD_REQUEST).build();

			logger.info("create answer with registration entity");
			Simply.logObject(Registration.fromJpa(registrationEntity), RegistrationResource.class);

			registrationEntity.getAnswers().add(newAnswer.toJpaAnswerEntity());

			return Response.status(Status.CREATED).entity(newAnswer).header("location", new URI("/answers/" + newAnswer.getId())).build();
		}
		catch(UnauthorizedException e)
		{
			return Response.status(Status.UNAUTHORIZED).build();
		}
	}

	@GET
    @Path("/payment/{paymentId}")
	@Produces(MediaType.APPLICATION_JSON)
    public Response getPayment(@PathParam(value = "paymentId") UUID paymentId, @HeaderParam(value = "Authorization") String authCode) throws URISyntaxException
    {
		try
		{
			CrsApplicationUser crsLoggedInUser = userService.getLoggedInUser(authCode);

			PaymentEntity paymentEntity = paymentService.fetchPaymentBy(paymentId, crsLoggedInUser);

			if(paymentEntity == null)
			{
				return Response.status(Status.NOT_FOUND).build();
			}

			Simply.logObject(Payment.fromJpa(paymentEntity), this.getClass());

			return Response.ok(Payment.fromJpa(paymentEntity)).build();
		}
		catch(UnauthorizedException e)
    	{
    		return Response.status(Status.UNAUTHORIZED).build();
    	}
    }
	
    private void processPayment(Payment payment, CrsApplicationUser loggedInUser) throws IOException, UnauthorizedException
    {
    	if(payment.getTransactionDatetime() == null && payment.getAuthnetTransactionId() == null)
    	{
    		ConferenceEntity jpaConference = conferenceService.fetchConferenceBy(registrationService.getRegistrationBy(payment.getRegistrationId(), 
    																					loggedInUser).getConference().getId());
    		
    		Long transactionId = paymentProcess.processCreditCardTransaction(Conference.fromJpa(jpaConference), payment);

    		payment.setAuthnetTransactionId(transactionId);
    		payment.setTransactionDatetime(clock.currentDateTime());

    		paymentService.updatePayment(payment.toJpaPaymentEntity(), loggedInUser);
    	}
    	else
    	{
    		
    	}
    }
}
