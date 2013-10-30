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
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.codehaus.jackson.map.ObjectMapper;
import org.cru.crs.api.model.Answer;
import org.cru.crs.api.model.Registration;
import org.cru.crs.auth.CrsUserService;
import org.cru.crs.auth.UnauthorizedException;
import org.cru.crs.auth.authz.AuthorizationService;
import org.cru.crs.auth.authz.OperationType;
import org.cru.crs.auth.model.CrsApplicationUser;
import org.cru.crs.model.AnswerEntity;
import org.cru.crs.model.RegistrationEntity;
import org.cru.crs.service.AnswerService;
import org.cru.crs.service.BlockService;
import org.cru.crs.service.ConferenceService;
import org.cru.crs.service.RegistrationService;
import org.cru.crs.utils.IdComparer;
import org.jboss.logging.Logger;

/**
 * User: lee.braddock
 */
@Stateless
@Path("/answers/{answerId}")
public class AnswerResource
{
	@Inject AnswerService answerService;
	@Inject RegistrationService registrationService;
    @Inject BlockService blockService;
	@Inject CrsUserService userService;
	@Inject AuthorizationService authorizationService;
	@Inject ConferenceService conferenceService; 
	
	@Context HttpServletRequest request;

	private Logger logger = Logger.getLogger(AnswerResource.class);

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAnswer(@PathParam(value = "answerId") UUID answerId, @HeaderParam(value = "Authorization") String authCode)
	{
		try
		{
			logger.info("get answer by id " + answerId);

			CrsApplicationUser crsLoggedInUser = userService.getLoggedInUser(authCode);

			AnswerEntity answerEntity = answerService.getAnswerBy(answerId);

			if(answerEntity == null) return Response.status(Status.NOT_FOUND).build();

			logger.info("get answer entity");

			logObject(answerEntity, logger);

			RegistrationEntity registrationEntity = registrationService.getRegistrationBy(answerEntity.getRegistrationId(), crsLoggedInUser);

			if(registrationEntity == null) return Response.status(Status.BAD_REQUEST).build();

			authorizationService.authorize(registrationEntity, conferenceService.fetchConferenceBy(registrationEntity.getConferenceId()), OperationType.READ, crsLoggedInUser);

			Answer answer = Answer.fromJpa(answerEntity);

			logger.info("get answer");

			logObject(answer, logger);

			return Response.ok(answer).build();
		}
		catch(UnauthorizedException e)
		{
			return Response.status(Status.UNAUTHORIZED).build();
		}
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateAnswer(Answer answer, @PathParam(value = "answerId") UUID answerId, @HeaderParam(value = "Authorization") String authCode) throws URISyntaxException
	{
		try
		{
			CrsApplicationUser crsLoggedInUser = userService.getLoggedInUser(authCode);

			if(IdComparer.idsAreNotNullAndDifferent(answerId, answer.getId()))
				return Response.status(Status.BAD_REQUEST).build();

			if(answer.getId() == null || answerId == null)
				return Response.status(Status.BAD_REQUEST).build();

            /*if the block for which this answer is related to has been deleted, then return
            an appropriate error client error message
             */
            if(blockService.fetchBlockBy(answer.getBlockId()) == null)
            {
                return Response.status(Status.GONE).build();
            }

			logger.info("update answer");

			logObject(answer, logger);

			AnswerEntity currentAnswerEntity = answerService.getAnswerBy(answerId);

			// create the answer if none yet exists for the given answer id
			if(currentAnswerEntity == null)
			{
				RegistrationEntity registrationEntity = registrationService.getRegistrationBy(answer.getRegistrationId(), crsLoggedInUser);

				if(registrationEntity == null) return Response.status(Status.BAD_REQUEST).build();

				authorizationService.authorize(registrationEntity, conferenceService.fetchConferenceBy(registrationEntity.getConferenceId()), OperationType.CREATE, crsLoggedInUser);

				logger.info("create answer with registration entity");

				logObject(Registration.fromDb(registrationEntity), logger);

//				registrationEntity.getAnswers().add(answer.toJpaAnswerEntity());

				return Response.status(Status.CREATED).entity(answer).header("location", new URI("/answers/" + answer.getId())).build();
			}

			logger.info("update current answer entity");

			logObject(currentAnswerEntity, logger);

			RegistrationEntity registrationEntity = registrationService.getRegistrationBy(currentAnswerEntity.getRegistrationId(), crsLoggedInUser);

			if(registrationEntity == null)
				return Response.status(Status.BAD_REQUEST).build();

			authorizationService.authorize(registrationEntity, conferenceService.fetchConferenceBy(registrationEntity.getConferenceId()), OperationType.UPDATE, crsLoggedInUser);

			answerService.updateAnswer(answer.toJpaAnswerEntity());

			return Response.noContent().build();
		}
		catch(UnauthorizedException e)
		{
			return Response.status(Status.UNAUTHORIZED).build();
		}
	}

	@DELETE
	public Response deleteAnswer(@PathParam(value = "answerId") UUID answerId, @HeaderParam(value = "Authorization") String authCode)
	{
		try
		{
			logger.info("delete answer entity");

			CrsApplicationUser crsLoggedInUser = userService.getLoggedInUser(authCode);

			AnswerEntity answerEntity = answerService.getAnswerBy(answerId);

			if(answerEntity == null)
				return Response.status(Status.BAD_REQUEST).build();

			logObject(answerEntity, logger);

			RegistrationEntity registrationEntity = registrationService.getRegistrationBy(answerEntity.getRegistrationId(), crsLoggedInUser);

			if(registrationEntity == null)
				return Response.status(Status.BAD_REQUEST).build();

			authorizationService.authorize(registrationEntity, conferenceService.fetchConferenceBy(registrationEntity.getConferenceId()), OperationType.DELETE, crsLoggedInUser);

			answerService.deleteAnswer(answerEntity);

			return Response.noContent().build();
		}
		catch(UnauthorizedException e)
		{
			return Response.status(Status.UNAUTHORIZED).build();
		}
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
