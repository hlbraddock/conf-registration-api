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

/**
 * User: lee.braddock
 */
@Stateless
@Path("/answer/{answerId}")
public class AnswerResource {

    @Inject EntityManager em;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAnswer(@PathParam(value="answerId") UUID answerId)
    {
        AnswerEntity requestedAnswer = new AnswerService(em).getAnswerBy(answerId);

        if(requestedAnswer == null) return Response.status(Status.NOT_FOUND).build();

        return Response.ok(Answer.fromJpa(requestedAnswer)).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateAnswer(Answer answer, @PathParam(value="answerId") UUID answerId)
    {
        Preconditions.checkNotNull(answer.getId());

        AnswerService answerService = new AnswerService(em);

        if(answerService.getAnswerBy(answer.getId()) == null)
            return Response.status(Status.BAD_REQUEST).build();

        answerService.updateAnswer(answer.toJpaAnswerEntity());

        return Response.noContent().build();
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteAnswer(Answer answer, @PathParam(value="answerId") UUID answerId)
    {
        Preconditions.checkNotNull(answer.getId());

        AnswerService answerService = new AnswerService(em);

        if(answerService.getAnswerBy(answer.getId()) == null)
            return Response.status(Status.BAD_REQUEST).build();

        answerService.deleteAnswer(answer.toJpaAnswerEntity());

        return Response.ok().build();
    }
}
