package org.cru.crs.api;

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

        log("get:", requestedRegistration);

        if(requestedRegistration == null) return Response.status(Status.NOT_FOUND).build();

        Registration registration = Registration.fromJpa(requestedRegistration);

        log("get:", registration);

        return Response.ok(registration).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateRegistration(Registration registration, @PathParam(value="registrationId") UUID registrationId)
    {
        Preconditions.checkNotNull(registrationId);

		log("update(provided)", registration);

        RegistrationService registrationService = new RegistrationService(em);

        RegistrationEntity currentRegistrationEntity = registrationService.getRegistrationBy(registrationId);

        if(currentRegistrationEntity == null)
            return Response.status(Status.BAD_REQUEST).build();

        log("update(existing):", currentRegistrationEntity);

        RegistrationEntity registrationEntity = registration.toJpaRegistrationEntity(currentRegistrationEntity.getConference());

        log("update:", registrationEntity);

        registrationService.updateRegistration(registrationEntity);

        return Response.noContent().build();
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteRegistration(Registration registration, @PathParam(value="registrationId") UUID registrationId)
    {
        Preconditions.checkNotNull(registration.getId());

        log("delete:", registration);

        RegistrationService registrationService = new RegistrationService(em);

        RegistrationEntity currentRegistrationEntity = registrationService.getRegistrationBy(registrationId);

        log("delete:", currentRegistrationEntity);

        if(currentRegistrationEntity == null)
            return Response.status(Status.BAD_REQUEST).build();

        new RegistrationService(em).deleteRegistration(currentRegistrationEntity);

        return Response.ok().build();
    }

    @POST
    @Path("/answers")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createAnswer(Answer newAnswer, @PathParam(value="registrationId") UUID registrationId) throws URISyntaxException
    {
        if(newAnswer.getId() == null) newAnswer.setId(UUID.randomUUID());

		AnswerResource.log("create:", newAnswer, logger);

        RegistrationEntity registration = new RegistrationService(em).getRegistrationBy(registrationId);

        if(registration == null) return Response.status(Status.BAD_REQUEST).build();

        registration.getAnswers().add(newAnswer.toJpaAnswerEntity());

        return Response.created(new URI("/answers/" + newAnswer.getId())).build();
    }

    private void log(String message, RegistrationEntity registration)
    {
        if(registration == null)
        {
            logger.info(message + registration);
            return;
        }

        logger.info(message + registration.getId());
        logger.info(message + registration.getUserId());

        if(registration.getConference() == null)
            logger.info(message + registration.getConference());
        else
            logger.info(message + registration.getConference().getId());

        logger.info(message + fromAnswerEntity(registration.getAnswers()));
    }

    private void log(String message, Registration registration)
    {
        if(registration == null)
        {
            logger.info(message + registration);
            return;
        }

        logger.info(message + " entity: " + registration.getId());
        logger.info(message + " entity: " + registration.getUserId());
        logger.info(message + " entity: " + fromAnswer(registration.getAnswers()));
    }

    private Set<String> fromAnswer(Set<Answer> answers)
    {
        Set<String> strings = new HashSet<String>();

        for(Answer answer : answers)
            strings.add(answer.getValue());

        return strings;
    }

    private Set<String> fromAnswerEntity(Set<AnswerEntity> answers)
    {
        Set<String> strings = new HashSet<String>();

        for(AnswerEntity answer : answers)
            strings.add(answer.getAnswer());

        return strings;
    }

    private void log(String message)
    {
        logger.info(message);
    }
}
