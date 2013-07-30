package org.cru.crs.api;

import org.codehaus.jackson.map.ObjectMapper;
import org.cru.crs.api.model.Answer;
import org.cru.crs.api.model.Registration;
import org.cru.crs.model.ConferenceEntity;
import org.cru.crs.model.RegistrationEntity;
import org.cru.crs.service.ConferenceService;
import org.cru.crs.service.RegistrationService;
import org.cru.crs.utils.IdComparer;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
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
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

@Stateless
@Path("/registrations/{registrationId}")
public class RegistrationResource
{
	@Inject	RegistrationService registrationService;
	@Inject ConferenceService conferenceService;

	private Logger logger = Logger.getLogger(RegistrationResource.class);

	@GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRegistration(@PathParam(value="registrationId") UUID registrationId)
    {
        RegistrationEntity requestedRegistration = registrationService.getRegistrationBy(registrationId);

		if(requestedRegistration == null) return Response.status(Status.NOT_FOUND).build();

		logger.info("get registration entity");
		logObject(Registration.fromJpa(requestedRegistration), logger);

		Registration registration = Registration.fromJpa(requestedRegistration);

		logger.info("get registration");
        logObject(registration, logger);

        return Response.ok(registration).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateRegistration(Registration registration, @PathParam(value="registrationId") UUID registrationId)
    {
		if(IdComparer.idsAreNotNullAndDifferent(registrationId, registration.getId()))
			return Response.status(Status.BAD_REQUEST).build();

		logger.info("update registration");
		logObject(registration, logger);

		ConferenceEntity conferenceEntity = conferenceService.fetchConferenceBy(registration.getConferenceId());

		if(conferenceEntity == null)
			return Response.status(Status.BAD_REQUEST).build();

        RegistrationEntity currentRegistrationEntity = registrationService.getRegistrationBy(registrationId);

        if(currentRegistrationEntity == null)
            return Response.status(Status.BAD_REQUEST).build();

		logger.info("update current registration entity");
		logObject(Registration.fromJpa(currentRegistrationEntity), logger);

		RegistrationEntity registrationEntity = registration.toJpaRegistrationEntity(conferenceEntity);

		logger.info("update registration entity");
		logObject(Registration.fromJpa(registrationEntity), logger);

		registrationService.updateRegistration(registrationEntity);

        return Response.noContent().build();
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteRegistration(Registration registration, @PathParam(value="registrationId") UUID registrationId)
    {
        RegistrationEntity registrationEntity = registrationService.getRegistrationBy(registrationId);

        if(registrationEntity == null)
            return Response.status(Status.BAD_REQUEST).build();

		logger.info("delete registration entity");
		logObject(Registration.fromJpa(registrationEntity), logger);

		registrationService.deleteRegistration(registrationEntity);

        return Response.ok().build();
    }

    @POST
    @Path("/answers")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
    public Response createAnswer(Answer newAnswer, @PathParam(value="registrationId") UUID registrationId) throws URISyntaxException
    {
		if(IdComparer.idsAreNotNullAndDifferent(registrationId, newAnswer.getRegistrationId()))
			return Response.status(Status.BAD_REQUEST).build();

		if(newAnswer.getId() == null) newAnswer.setId(UUID.randomUUID());

		logger.info("create answer");
		logObject(newAnswer, logger);

		RegistrationEntity registrationEntity = registrationService.getRegistrationBy(registrationId);

		if(registrationEntity == null) return Response.status(Status.BAD_REQUEST).build();

		logger.info("create answer with registration entity");
		logObject(Registration.fromJpa(registrationEntity), logger);

		registrationEntity.getAnswers().add(newAnswer.toJpaAnswerEntity());

		return Response.status(Status.CREATED).entity(newAnswer).header("location", new URI("/answers/" + newAnswer.getId())).build();
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
