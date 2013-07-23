package org.cru.crs.api;

import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
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

import org.cru.crs.api.model.Answer;
import org.cru.crs.model.AnswerEntity;
import org.cru.crs.model.PageEntity;
import org.cru.crs.service.AnswerService;
import org.cru.crs.service.PageService;

import com.google.common.base.Preconditions;
import org.cru.crs.service.AnswerService;
import org.jboss.logging.Logger;

/**
 * User: lee.braddock
 */
@Stateless
@Path("/answers/{answerId}")
public class AnswerResource {

    @Inject EntityManager em;

	Logger logger = Logger.getLogger(AnswerResource.class);

	@GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAnswer(@PathParam(value="answerId") UUID answerId)
    {
        AnswerEntity requestedAnswer = new AnswerService(em).getAnswerBy(answerId);

		log("get:", requestedAnswer);

		if(requestedAnswer == null) return Response.status(Status.NOT_FOUND).build();

        Answer answer = Answer.fromJpa(requestedAnswer);

        log("get:", answer);

        return Response.ok(answer).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateAnswer(Answer answer, @PathParam(value="answerId") UUID answerId)
    {
        Preconditions.checkNotNull(answer.getId());

		log("update(provided):", answer);

		AnswerService answerService = new AnswerService(em);

        AnswerEntity currentAnswerEntity = answerService.getAnswerBy(answer.getId());
        if(currentAnswerEntity == null)
            return Response.status(Status.BAD_REQUEST).build();

        log("update(existing):", currentAnswerEntity);

        AnswerEntity answerEntity = answer.toJpaAnswerEntity();

        log("update(existing):", answer);

        answerService.updateAnswer(answerEntity);

        return Response.noContent().build();
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteAnswer(Answer answer, @PathParam(value="answerId") UUID answerId)
    {
        Preconditions.checkNotNull(answer.getId());

        log("delete:", answer);

        AnswerService answerService = new AnswerService(em);

        AnswerEntity answerEntity = answerService.getAnswerBy(answer.getId());

        if(answerEntity == null)
            return Response.status(Status.BAD_REQUEST).build();

        log("delete:", answerEntity);

        answerService.deleteAnswer(answerEntity);

        return Response.ok().build();
    }

	private void log(String message, AnswerEntity answer)
    {
        if(answer == null)
        {
            logger.info(message + answer);
            return;
        }

        logger.info(message + " entity: " + answer.getId());
        logger.info(message + " entity: " + answer.getBlockId());
        logger.info(message + " entity: " + answer.getAnswer());
    }

	private void log(String message, Answer answer)
	{
		log(message, answer, logger);
	}

	public static void log(String message, Answer answer, Logger logger)
    {
        if(answer == null)
        {
            logger.info(message + answer);
            return;
        }

        logger.info(message + answer.getId());
        logger.info(message + answer.getBlockId());
        logger.info(message + answer.getValue());
    }

    private void log(String message)
    {
        logger.info(message);
    }
}
