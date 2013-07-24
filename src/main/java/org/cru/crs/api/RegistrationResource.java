package org.cru.crs.api;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.util.JSONPObject;
import org.cru.crs.api.model.Answer;
import org.cru.crs.api.model.Registration;
import org.cru.crs.model.AnswerEntity;
import org.cru.crs.model.ConferenceEntity;
import org.cru.crs.model.RegistrationEntity;
import org.cru.crs.service.ConferenceService;
import org.cru.crs.service.RegistrationService;

import com.google.common.base.Preconditions;
import org.jboss.logging.Logger;

@Stateless
@Path("/registrations/{registrationId}")
public class RegistrationResource
{
    @Inject EntityManager em;

	private Logger logger = Logger.getLogger(RegistrationResource.class);

	@GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRegistration(@PathParam(value="registrationId") UUID registrationId)
    {
        RegistrationEntity requestedRegistration = new RegistrationService(em).getRegistrationBy(registrationId);

		logObject(requestedRegistration, logger);

		if(requestedRegistration == null) return Response.status(Status.NOT_FOUND).build();

        Registration registration = Registration.fromJpa(requestedRegistration);

        logObject(registration, logger);

        return Response.ok(registration).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateRegistration(Registration registration, @PathParam(value="registrationId") UUID registrationId)
    {
        Preconditions.checkNotNull(registrationId);

		logObject(registration, logger);

		RegistrationService registrationService = new RegistrationService(em);

        RegistrationEntity currentRegistrationEntity = registrationService.getRegistrationBy(registrationId);

        if(currentRegistrationEntity == null)
            return Response.status(Status.BAD_REQUEST).build();

        RegistrationEntity registrationEntity = registration.toJpaRegistrationEntity(currentRegistrationEntity.getConference());

		logObject(registrationEntity, logger);

		registrationService.updateRegistration(registrationEntity);

        return Response.noContent().build();
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteRegistration(Registration registration, @PathParam(value="registrationId") UUID registrationId)
    {
        Preconditions.checkNotNull(registration.getId());

		RegistrationService registrationService = new RegistrationService(em);

        RegistrationEntity registrationEntity = registrationService.getRegistrationBy(registrationId);

		logObject(registrationEntity, logger);

        if(registrationEntity == null)
            return Response.status(Status.BAD_REQUEST).build();

        new RegistrationService(em).deleteRegistration(registrationEntity);

        return Response.ok().build();
    }

    @POST
    @Path("/answers")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createAnswer(Answer newAnswer, @PathParam(value="registrationId") UUID registrationId) throws URISyntaxException
    {
        if(newAnswer.getId() == null) newAnswer.setId(UUID.randomUUID());

		logObject(newAnswer, logger);

        RegistrationEntity registration = new RegistrationService(em).getRegistrationBy(registrationId);

        if(registration == null) return Response.status(Status.BAD_REQUEST).build();

        registration.getAnswers().add(newAnswer.toJpaAnswerEntity());

        return Response.created(new URI("/answers/" + newAnswer.getId())).build();
    }

    private void logObject(Object object, Logger logger)
    {
		try
		{
			logger.info(new ObjectMapper().defaultPrettyPrintingWriter().writeValueAsString(object));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
