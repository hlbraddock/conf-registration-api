package org.cru.crs.api;

import java.net.URI;
import java.net.URISyntaxException;
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

@Stateless
@Path("/registrations/{registrationId}")
public class RegistrationResource
{
    @Inject EntityManager em;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRegistration(@PathParam(value="registrationId") UUID registrationId)
    {
        RegistrationEntity requestedRegistration = new RegistrationService(em).getRegistrationBy(registrationId);

        if(requestedRegistration == null) return Response.status(Status.NOT_FOUND).build();

        return Response.ok(Registration.fromJpa(requestedRegistration)).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateRegistration(Registration registration, @PathParam(value="registrationId") UUID registrationId)
    {
        Preconditions.checkNotNull(registrationId);

        RegistrationService registrationService = new RegistrationService(em);

        if(registrationService.getRegistrationBy(registrationId) == null)
            return Response.status(Status.BAD_REQUEST).build();

        registrationService.updateRegistration(registration.toJpaRegistrationEntity());

        return Response.noContent().build();
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteRegistration(Registration registration, @PathParam(value="registrationId") UUID registrationId)
    {
        Preconditions.checkNotNull(registration.getId());

        new RegistrationService(em).deleteRegistration(registration.toJpaRegistrationEntity());

        return Response.ok().build();
    }

    @POST
    @Path("/answers")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createAnswer(Answer newAnswer, @PathParam(value="registrationId") UUID registrationId) throws URISyntaxException
    {
        if(newAnswer.getId() == null) newAnswer.setId(UUID.randomUUID());

        RegistrationEntity registration = new RegistrationService(em).getRegistrationBy(registrationId);

        if(registration == null) return Response.status(Status.BAD_REQUEST).build();

        registration.getAnswers().add(newAnswer.toJpaAnswerEntity());

        return Response.created(new URI("/answers/" + newAnswer.getId())).build();
    }
}
