package org.cru.crs.api;

import org.codehaus.jackson.map.ObjectMapper;
import org.cru.crs.api.model.Answer;
import org.cru.crs.api.model.Registration;
import org.cru.crs.model.AnswerEntity;
import org.cru.crs.model.RegistrationEntity;
import org.cru.crs.service.AnswerService;
import org.cru.crs.service.RegistrationService;
import org.cru.crs.utils.IdComparer;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

/**
 * User: lee.braddock
 */
@Stateless
@Path("/answers/{answerId}")
public class AnswerResource {

	@Inject	AnswerService answerService;
	@Inject	RegistrationService registrationService;

	private Logger logger = Logger.getLogger(AnswerResource.class);

	@GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAnswer(@PathParam(value="answerId") UUID answerId)
    {
		logger.info("get answer by id " + answerId);

		AnswerEntity requestedAnswer = answerService.getAnswerBy(answerId);

		if(requestedAnswer == null) return Response.status(Status.NOT_FOUND).build();

		logger.info("get answer entity");
		logObject(requestedAnswer, logger);

		Answer answer = Answer.fromJpa(requestedAnswer);

		logger.info("get answer");
		logObject(answer, logger);

        return Response.ok(answer).build();
    }

    @PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
    public Response updateAnswer(Answer answer, @PathParam(value="answerId") UUID answerId) throws URISyntaxException
    {
		if(IdComparer.idsAreNotNullAndDifferent(answerId, answer.getId()))
			return Response.status(Status.BAD_REQUEST).build();

		if(answer.getId() == null || answerId == null)
			return Response.status(Status.BAD_REQUEST).build();

		logger.info("update answer");
		logObject(answer, logger);

		AnswerEntity currentAnswerEntity = answerService.getAnswerBy(answerId);

		// create the answer if none yet exists for the given answer id
        if(currentAnswerEntity == null)
		{
			RegistrationEntity registrationEntity = registrationService.getRegistrationBy(answer.getRegistrationId());

			if(registrationEntity == null) return Response.status(Status.BAD_REQUEST).build();

			logger.info("create answer with registration entity");
			logObject(Registration.fromJpa(registrationEntity), logger);

			registrationEntity.getAnswers().add(answer.toJpaAnswerEntity());

			return Response.status(Status.CREATED).entity(answer).header("location", new URI("/answers/" + answer.getId())).build();
		}

		logger.info("update current answer entity");
		logObject(currentAnswerEntity, logger);

        answerService.updateAnswer(answer.toJpaAnswerEntity());

        return Response.noContent().build();
    }

    @DELETE
    public Response deleteAnswer(@PathParam(value="answerId") UUID answerId)
    {
        AnswerEntity answerEntity = answerService.getAnswerBy(answerId);

        if(answerEntity == null)
            return Response.status(Status.BAD_REQUEST).build();

		logger.info("delete answer entity");
		logObject(answerEntity, logger);

        answerService.deleteAnswer(answerEntity);

		return Response.noContent().build();
    }

	private void logObject(Object object, Logger logger)
	{
		if(object == null)
			return;

		try
		{
			logger.info(new ObjectMapper().defaultPrettyPrintingWriter().writeValueAsString(object));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
