package org.cru.crs.api;

import com.google.common.base.Preconditions;
import org.codehaus.jackson.map.ObjectMapper;
import org.cru.crs.api.model.Answer;
import org.cru.crs.model.AnswerEntity;
import org.cru.crs.service.AnswerService;
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
import java.util.UUID;

/**
 * User: lee.braddock
 */
@Stateless
@Path("/answers/{answerId}")
public class AnswerResource {

	@Inject	AnswerService answerService;

	private Logger logger = Logger.getLogger(AnswerResource.class);

	@GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAnswer(@PathParam(value="answerId") UUID answerId)
    {
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
    public Response updateAnswer(Answer answer, @PathParam(value="answerId") UUID answerId)
    {
        Preconditions.checkNotNull(answer.getId());

		logger.info("update answer");
		logObject(answer, logger);

        AnswerEntity currentAnswerEntity = answerService.getAnswerBy(answer.getId());
        if(currentAnswerEntity == null)
            return Response.status(Status.BAD_REQUEST).build();

		logger.info("update current answer entity");
		logObject(currentAnswerEntity, logger);

		AnswerEntity answerEntity = answer.toJpaAnswerEntity();

		logger.info("update to answer entity");
		logObject(answerEntity, logger);

        answerService.updateAnswer(answerEntity);

        return Response.noContent().build();
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteAnswer(Answer answer, @PathParam(value="answerId") UUID answerId)
    {
        Preconditions.checkNotNull(answer.getId());

        AnswerEntity answerEntity = answerService.getAnswerBy(answer.getId());

        if(answerEntity == null)
            return Response.status(Status.BAD_REQUEST).build();

		logger.info("delete answer entity");
		logObject(answerEntity, logger);

        answerService.deleteAnswer(answerEntity);

        return Response.ok().build();
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
