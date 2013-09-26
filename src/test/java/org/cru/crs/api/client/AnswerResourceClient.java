package org.cru.crs.api.client;

import org.cru.crs.api.model.Answer;
import org.jboss.resteasy.client.ClientResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.UUID;

@Path("/answers/{answerId}")
public interface AnswerResourceClient
{
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ClientResponse<Answer> getAnswer(@PathParam(value = "answerId") UUID answerId, @HeaderParam(value = "Authorization") String authCode);
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ClientResponse<Answer> updateAnswer(Answer answer, @PathParam(value = "answerId") UUID answerId, @HeaderParam(value = "Authorization") String authCode);
	
	@DELETE
	public ClientResponse<Answer> deleteAnswer(@PathParam(value = "answerId") UUID answerId, @HeaderParam(value = "Authorization") String authCode);
}
