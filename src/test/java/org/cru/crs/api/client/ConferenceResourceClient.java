package org.cru.crs.api.client;

import org.cru.crs.api.model.Conference;
import org.cru.crs.api.model.Page;
import org.cru.crs.api.model.Permission;
import org.cru.crs.api.model.Registration;
import org.cru.crs.api.model.RegistrationView;
import org.jboss.resteasy.client.ClientResponse;

import javax.mail.MessagingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

@Path("/conferences")
public interface ConferenceResourceClient
{
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ClientResponse<List<Conference>> getConferences(@HeaderParam(value = "Authorization") String authCode);
	
	@GET
	@Path("/{conferenceId}")
	@Produces(MediaType.APPLICATION_JSON)
	public ClientResponse<Conference> getConference(@PathParam(value = "conferenceId") UUID conferenceId);
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ClientResponse<Conference> createConference(Conference conference, @HeaderParam(value = "Authorization") String authCode)throws URISyntaxException;
	
	@SuppressWarnings(value="rawtypes")
	@PUT
	@Path("/{conferenceId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public ClientResponse updateConference(Conference conference, @PathParam(value = "conferenceId") UUID conferenceId, @HeaderParam(value = "Authorization") String authCode);

    @SuppressWarnings(value="rawtypes")
    @DELETE
    @Path("/{conferenceId}/delete")
    @Produces(MediaType.APPLICATION_JSON)
    public ClientResponse deleteConference(@PathParam(value = "conferenceId") UUID conferenceId, @HeaderParam(value = "Authorization") String authCode);

	@POST
	@Path("/{conferenceId}/pages")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ClientResponse<Page> createPage(Page newPage, @PathParam(value = "conferenceId") UUID conferenceId, @HeaderParam(value = "Authorization") String authCode) throws URISyntaxException;

	@POST
	@Path("/{conferenceId}/registrations")
	@Consumes(MediaType.APPLICATION_JSON)
	public ClientResponse<Registration> createRegistration(Registration newRegistration, @PathParam(value = "conferenceId") UUID conferenceId, @HeaderParam(value = "Authorization") String authCode);

	@POST
	@Path("/{conferenceId}/registrations")
	@Consumes(MediaType.APPLICATION_JSON)
	public ClientResponse<Registration> createRegistrationType(Registration newRegistration, @PathParam(value = "conferenceId") UUID conferenceId, @HeaderParam(value = "Authorization") String authCode, @HeaderParam(value = "Registration-Type") String registrationType);

	@GET
	@Path("/{conferenceId}/registrations")
	@Consumes(MediaType.APPLICATION_JSON)
	public ClientResponse<List<Registration>> getRegistrations(@PathParam(value = "conferenceId") UUID conferenceId, @HeaderParam(value = "Authorization") String authCode);

	@GET
	@Path("/{conferenceId}/registrations/current")
	@Consumes(MediaType.APPLICATION_JSON)
	public ClientResponse<Registration> getCurrentRegistration(@PathParam(value = "conferenceId") UUID conferenceId, @HeaderParam(value = "Authorization") String authCode, @HeaderParam(value = "PreviousAuthorization") String previousAuthCode);

	@POST
	@Path("/{conferenceId}/permissions")
	@Consumes(MediaType.APPLICATION_JSON)
	public ClientResponse<Permission> grantPermission(@PathParam(value = "conferenceId") UUID conferenceId,
														@HeaderParam(value = "Authorization") String authCode,
														Permission newPermission) throws URISyntaxException, MalformedURLException, MessagingException;
	
	@GET
	@Path("/{conferenceId}/registration-views")
	@Produces(MediaType.APPLICATION_JSON)
	public ClientResponse<List<RegistrationView>>  getRegistrationViews(@PathParam(value = "conferenceId") UUID conferenceId,
									@HeaderParam(value = "Authorization") String authCode);
	
	@POST
	@Path("/{conferenceId}/registration-views")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ClientResponse createRegistrationView(@PathParam(value = "conferenceId") UUID conferenceId,
											@HeaderParam(value = "Authorization") String authCode,
											RegistrationView newDataView) throws URISyntaxException;
	
	@GET
	@Path("/{conferenceId}/permissions/current")
	@Produces(MediaType.APPLICATION_JSON)
	public ClientResponse<Permission> getPermissionsForCurrentUserOnConference(@PathParam(value = "conferenceId") UUID conferenceId,
																@HeaderParam(value = "Authorization") String authCode);
}
