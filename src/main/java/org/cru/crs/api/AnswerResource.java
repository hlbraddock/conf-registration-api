package org.cru.crs.api;

import org.cru.crs.api.model.Answer;
import org.cru.crs.api.model.Registration;
import org.cru.crs.auth.CrsUserService;
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
import org.cru.crs.utils.Simply;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

/**
 * User: lee.braddock
 */
@Path("/answers/{answerId}")
public class AnswerResource extends TransactionalResource
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
		logger.info("get answer by id " + answerId);

		CrsApplicationUser crsLoggedInUser = userService.getLoggedInUser(authCode);

		AnswerEntity answerEntity = answerService.getAnswerBy(answerId);

		if(answerEntity == null) throw new NotFoundException("Requested answer " + answerId + " not found");

		Simply.logObject(answerEntity, this.getClass());

		RegistrationEntity registrationEntity = registrationService.getRegistrationBy(answerEntity.getRegistrationId());

		if(registrationEntity == null) throw new BadRequestException("There is no registration for this answer");

		authorizationService.authorizeRegistration(registrationEntity,
				conferenceService.fetchConferenceBy(registrationEntity.getConferenceId()),
				OperationType.READ,
				crsLoggedInUser);

		Answer answer = Answer.fromDb(answerEntity);

		Simply.logObject(answer, this.getClass());

		return Response.ok(answer).build();
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateAnswer(Answer answer, @PathParam(value = "answerId") UUID answerId, @HeaderParam(value = "Authorization") String authCode) throws URISyntaxException
	{
		CrsApplicationUser crsLoggedInUser = userService.getLoggedInUser(authCode);

		if(answer.getId() == null || answerId == null || IdComparer.idsAreNotNullAndDifferent(answerId, answer.getId()))
		{
			throw new BadRequestException("The path answer id: " + answerId + " and entity answer id: " + answer.getId() + " were either null or don't match");
		}

		// if the block for which this answer is related to has been deleted, then return an appropriate error client error message
		if(blockService.getBlockById(answer.getBlockId()) == null)
		{
			return Response.status(Status.GONE).build();
		}

		logger.info("update answer");

		Simply.logObject(answer, this.getClass());

		AnswerEntity currentAnswerEntity = answerService.getAnswerBy(answerId);

		RegistrationEntity registrationEntity = registrationService.getRegistrationBy(answer.getRegistrationId());

		if(registrationEntity == null)
		{
			throw new BadRequestException("The answer being updated belongs to a registration that does not exist");
		}

		// create the answer if none yet exists for the given answer id
		if(currentAnswerEntity == null)
		{
			authorizationService.authorizeRegistration(registrationEntity,
					conferenceService.fetchConferenceBy(registrationEntity.getConferenceId()),
					OperationType.UPDATE,
					crsLoggedInUser);

			logger.info("create answer with registration entity");

			Simply.logObject(Registration.fromDb(registrationEntity), this.getClass());

			answerService.insertAnswer(answer.toDbAnswerEntity());

			return Response.status(Status.CREATED).entity(answer).header("location", new URI("/answers/" + answer.getId())).build();
		}

		logger.info("update current answer entity");

		Simply.logObject(currentAnswerEntity, this.getClass());

		authorizationService.authorizeRegistration(registrationEntity,
				conferenceService.fetchConferenceBy(registrationEntity.getConferenceId()),
				OperationType.UPDATE,
				crsLoggedInUser);

		answerService.updateAnswer(answer.toDbAnswerEntity());

		return Response.noContent().build();
	}

	@DELETE
	public Response deleteAnswer(@PathParam(value = "answerId") UUID answerId, @HeaderParam(value = "Authorization") String authCode)
	{
		logger.info("delete answer entity");

		CrsApplicationUser crsLoggedInUser = userService.getLoggedInUser(authCode);

		AnswerEntity answerEntity = answerService.getAnswerBy(answerId);

		if(answerEntity == null)
		{
			throw new BadRequestException("The answer specified by: " + answerId + " does not exist");
		}

		Simply.logObject(answerEntity, this.getClass());

		RegistrationEntity registrationEntity = registrationService.getRegistrationBy(answerEntity.getRegistrationId());

		if(registrationEntity == null)
		{
			throw new BadRequestException("The answer being deleted belongs to a registration which does not exist");
		}

		authorizationService.authorizeRegistration(registrationEntity,
				conferenceService.fetchConferenceBy(registrationEntity.getConferenceId()),
				OperationType.DELETE,
				crsLoggedInUser);

		answerService.deleteAnswer(answerEntity.getId());

		return Response.noContent().build();
	}
}
