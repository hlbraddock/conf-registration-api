package org.cru.crs.api;

import org.codehaus.jackson.map.ObjectMapper;
import org.cru.crs.api.model.Answer;
import org.cru.crs.api.model.Payment;
import org.cru.crs.api.model.Registration;
import org.cru.crs.auth.CrsUserService;
import org.cru.crs.auth.UnauthorizedException;
import org.cru.crs.auth.model.CrsApplicationUser;
import org.cru.crs.model.ConferenceEntity;
import org.cru.crs.model.PaymentEntity;
import org.cru.crs.model.RegistrationEntity;
import org.cru.crs.service.ConferenceService;
import org.cru.crs.service.PaymentService;
import org.cru.crs.service.RegistrationService;
import org.cru.crs.utils.IdComparer;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

@Stateless
@Path("/registrations/{registrationId}")
public class RegistrationResource
{
	@Inject RegistrationService registrationService;
	@Inject ConferenceService conferenceService;
	@Inject CrsUserService userService;
    @Inject PaymentService paymentService;

	@Context HttpServletRequest request;

	private Logger logger = Logger.getLogger(RegistrationResource.class);

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRegistration(@PathParam(value = "registrationId") UUID registrationId, @HeaderParam(value = "Authorization") String authCode)
	{
		try
		{
			CrsApplicationUser crsLoggedInUser = userService.getUserFromSession(request.getSession(), authCode);

			RegistrationEntity requestedRegistration = registrationService.getRegistrationBy(registrationId, crsLoggedInUser);

			if(requestedRegistration == null) return Response.status(Status.NOT_FOUND).build();

			logger.info("get registration entity");
			logObject(Registration.fromJpa(requestedRegistration), logger);

			Registration registration = Registration.fromJpa(requestedRegistration);

			logger.info("get registration");
			logObject(registration, logger);

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
			CrsApplicationUser crsLoggedInUser = userService.getUserFromSession(request.getSession(), authCode);

			if(IdComparer.idsAreNotNullAndDifferent(registrationId, registration.getId()))
				return Response.status(Status.BAD_REQUEST).build();

			if(registration.getId() == null || registrationId == null)
				return Response.status(Status.BAD_REQUEST).build();

			logger.info("update registration");
			logObject(registration, logger);

			ConferenceEntity conferenceEntity = conferenceService.fetchConferenceBy(registration.getConferenceId());

			if(conferenceEntity == null)
				return Response.status(Status.BAD_REQUEST).build();

			RegistrationEntity currentRegistrationEntity = registrationService.getRegistrationBy(registrationId, crsLoggedInUser);

			// create the entity if none exists
			if(currentRegistrationEntity == null)
			{
				RegistrationEntity registrationEntity = registration.toJpaRegistrationEntity(conferenceEntity);

				logger.info("update registration creating");
				logObject(registrationEntity, logger);

				registrationService.createNewRegistration(registrationEntity, crsLoggedInUser);

				return Response.status(Status.CREATED)
						.location(new URI("/registrations/" + registration.getId()))
						.entity(registration)
						.build();
			}

			logger.info("update current registration entity");
			logObject(Registration.fromJpa(currentRegistrationEntity), logger);

			RegistrationEntity registrationEntity = registration.toJpaRegistrationEntity(conferenceEntity);

			logger.info("update registration entity");
			logObject(Registration.fromJpa(registrationEntity), logger);

			registrationService.updateRegistration(registrationEntity, crsLoggedInUser);

			return Response.noContent().build();
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
			CrsApplicationUser crsLoggedInUser = userService.getUserFromSession(request.getSession(), authCode);

			RegistrationEntity registrationEntity = registrationService.getRegistrationBy(registrationId, crsLoggedInUser);

			if(registrationEntity == null)
				return Response.status(Status.BAD_REQUEST).build();

			logger.info("delete registration entity");
			logObject(Registration.fromJpa(registrationEntity), logger);

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
			CrsApplicationUser crsLoggedInUser = userService.getUserFromSession(request.getSession(), authCode);

			if(IdComparer.idsAreNotNullAndDifferent(registrationId, newAnswer.getRegistrationId()))
				return Response.status(Status.BAD_REQUEST).build();

			if(newAnswer.getId() == null) newAnswer.setId(UUID.randomUUID());

			logger.info("create answer");
			logObject(newAnswer, logger);

			RegistrationEntity registrationEntity = registrationService.getRegistrationBy(registrationId, crsLoggedInUser);

			if(registrationEntity == null) return Response.status(Status.BAD_REQUEST).build();

			logger.info("create answer with registration entity");
			logObject(Registration.fromJpa(registrationEntity), logger);

			registrationEntity.getAnswers().add(newAnswer.toJpaAnswerEntity());

			return Response.status(Status.CREATED).entity(newAnswer).header("location", new URI("/answers/" + newAnswer.getId())).build();
		}
		catch(UnauthorizedException e)
		{
			return Response.status(Status.UNAUTHORIZED).build();
		}
	}

    @POST
    @Path("/payment")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postPayment(Payment payment, @PathParam(value = "registrationId") UUID registrationId, @HeaderParam(value = "Authorization") String authCode) throws URISyntaxException
    {
        logObject(payment, logger);

        if(payment.getId() == null)
        {
            payment.setId(UUID.randomUUID());
        }
        
        payment.setRegistrationId(registrationId);
        paymentService.createPaymentRecord(payment.toJpaPaymentEntity());

        return Response.status(Status.CREATED)
        				.location(new URI("/registrations/" + registrationId + "/payment/" + payment.getId()))
        				.entity(Payment.fromJpa(paymentService.fetchPaymentBy(payment.getId())))
        				.build();
    }

	private void logObject(Object object, Logger logger)
	{
		if(object == null)
			return;

		try
		{
			logger.info(new ObjectMapper().defaultPrettyPrintingWriter().writeValueAsString(object));
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
