package org.cru.crs.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.cru.crs.api.model.Conference;
import org.cru.crs.api.model.Page;
import org.cru.crs.api.model.Registration;
import org.cru.crs.api.process.ConferenceFetchProcess;
import org.cru.crs.api.process.ConferenceUpdateProcess;
import org.cru.crs.api.process.RegistrationFetchProcess;
import org.cru.crs.api.process.RegistrationUpdateProcess;
import org.cru.crs.auth.CrsUserService;
import org.cru.crs.auth.authz.AuthorizationService;
import org.cru.crs.auth.authz.OperationType;
import org.cru.crs.auth.model.CrsApplicationUser;
import org.cru.crs.model.ConferenceEntity;
import org.cru.crs.model.PageEntity;
import org.cru.crs.model.RegistrationEntity;
import org.cru.crs.model.UserEntity;
import org.cru.crs.service.BlockService;
import org.cru.crs.service.ConferenceService;
import org.cru.crs.service.PageService;
import org.cru.crs.service.RegistrationService;
import org.cru.crs.service.UserService;
import org.cru.crs.utils.IdComparer;
import org.cru.crs.utils.Simply;
import org.jboss.logging.Logger;
import org.jboss.resteasy.spi.BadRequestException;
import org.jboss.resteasy.spi.InternalServerErrorException;
import org.jboss.resteasy.spi.NotFoundException;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.testng.collections.Lists;

@Path("/conferences")
public class ConferenceResource
{
	@Inject ConferenceService conferenceService;
	@Inject RegistrationService registrationService;
	@Inject PageService pageService;
	@Inject BlockService blockService;
	@Inject UserService userService;
	
	@Inject AuthorizationService authorizationService;
	
	@Inject ConferenceFetchProcess conferenceFetchProcess;
	@Inject ConferenceUpdateProcess conferenceUpdateProcess;
	@Inject RegistrationFetchProcess registrationFetchProcess;
	@Inject RegistrationUpdateProcess registrationUpdateProcess;
	
	@Inject CrsUserService crsUserService;

	Logger logger = Logger.getLogger(ConferenceResource.class);

	/**
	 * Returns all conference resources that the user specified by @param registrationId has access to
	 * 
	 * Possible Outcomes:
	 * 	200 Ok - found some conferences and the user specified by @param authCode has read access to them
	 *  401 Unauthorized - user specified by @param authCode is expired or doesn't exist
	 *  
	 * @param authCode
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getConferences(@HeaderParam(value="Authorization") String authCode)
	{
		try
		{
			CrsApplicationUser loggedInUser = crsUserService.getLoggedInUser(authCode);

            List<Conference> conferences = Lists.newArrayList();
            
            for(ConferenceEntity databaseConference : conferenceService.fetchAllConferences(loggedInUser))
            {
            	conferences.add(conferenceFetchProcess.get(databaseConference.getId()));            													            	
            }

            return Response.ok(conferences).build();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new InternalServerErrorException(e);
		}
	}

	/**
	 * Returns a conference resource specified by @param conferenceId
	 * 
	 * Possible Outcomes:
	 * 	200 Ok - found conference and the user specified by @param authCode has read access
	 *  404 Not Found - no conference resource specified by @param conferenceId
	 *  
	 * @param authCode
	 * @return
	 */
	@GET
	@Path("/{conferenceId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getConference(@PathParam(value = "conferenceId") UUID conferenceId)
	{
		try
		{
			logger.info("get conference entity " + conferenceId);

			Conference requestedConference = conferenceFetchProcess.get(conferenceId);

			if(requestedConference == null) 
			{
				return Response.ok(new NotFound()).build();
			}

			Simply.logObject(requestedConference, ConferenceResource.class);

			return Response.ok(requestedConference).build();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new InternalServerErrorException(e);
		}
	}

	/**
	 * Creates a new conference resource
	 * 
	 * Possible Outcomes:
	 *  201 Created - @param conference was created
	 *  401 Unauthorized - user specified by @param authCode is expired, doesn't exist or doesn't have create conference privileges
	 * 
	 * @param conference
	 * @param authCode
	 * @return
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createConference(Conference conference, @HeaderParam(value="Authorization") String authCode) throws URISyntaxException
	{
		try
		{
			logger.info("create conference auth code" + authCode);
			
			CrsApplicationUser loggedInUser = crsUserService.getLoggedInUser(authCode);

			/*if there is no id in the conference, then create one. the client has the ability, but not 
			 * the obligation to create one*/
			if(conference.getId() == null)
			{
				conference.setId(UUID.randomUUID());
			}

			conference.setContactUser(loggedInUser.getId());
			setInitialContactPersonDetailsBasedOn(conference, loggedInUser);
			
			Simply.logObject(conference, ConferenceResource.class);
			
			/*persist the new conference*/
			conferenceService.createNewConference(conference.toDbConferenceEntity(),conference.toDbConferenceCostsEntity());
			
			/*perform a deep update to ensure all the fields are saved.*/
			conferenceUpdateProcess.performDeepUpdate(conference);
			
			/*fetch the created conference so a nice pretty conference object can be returned to client*/
			Conference createdConference = conferenceFetchProcess.get(conference.getId());
			
			/*return a response with status 201 - Created and a location header to fetch the conference.
			 * a copy of the entity is also returned.*/
			return Response.status(Status.CREATED)
							.location(new URI("/conferences/" + conference.getId()))
							.entity(createdConference).build();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new InternalServerErrorException(e);
		}
	}

	/**
	 * Updates the conference resource specified by @param conferenceId
	 * 
	 * Possible outcomes:
	 * 	201 Created - page resource was created and associated to conference specified by @param conferenceId 
	 *  400 Bad Request - if the conference specified by @param conferenceId, then bad request
	 *  401 Unauthorized - user specified by @param authCode is expired, doesn't exist
	 * 
	 * @param conference
	 * @return
	 */
	@PUT
	@Path("/{conferenceId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateConference(Conference conference, 
			@PathParam(value = "conferenceId") UUID conferenceId,
			@HeaderParam(value="Authorization") String authCode)
	{

		try
		{
			logger.info("update conference " + conferenceId + "auth code" + authCode);
			
			CrsApplicationUser loggedInUser = crsUserService.getLoggedInUser(authCode);

			/*Check if the conference IDs are both present, and are different.  If so then throw a 400 - Bad Request*/
			if(IdComparer.idsAreNotNullAndDifferent(conferenceId, conference.getId()))
			{
				throw new BadRequestException("Path conference id: " + conferenceId + " does not entity conference id: " + conference.getId());
			}

			if(conferenceId == null)
			{
				/* If a conference ID wasn't passed in, then create a new conference.*/
				conference.setId(UUID.randomUUID());
			}

			Simply.logObject(conference, ConferenceResource.class);

			authorizationService.authorizeConference(conference.toDbConferenceEntity(), OperationType.UPDATE, loggedInUser);
			
			conferenceUpdateProcess.performDeepUpdate(conference);

			return Response.noContent().build();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new InternalServerErrorException(e);
		}
	}

	/**
	 * Creates a new page resource and associates it to the conference specified by @param conferenceId
	 * 
	 * Possible outcomes:
	 * 	201 Created - page resource was created and associated to conference specified by @param conferenceId 
	 *  400 Bad Request - if the conference specified by @param conferenceId, then bad request
	 *  401 Unauthorized - user specified by @param authCode is expired, doesn't exist
	 * 
	 * @param conference
	 * @return
	 */
	@POST
	@Path("/{conferenceId}/pages")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createPage(Page newPage, 
			@PathParam(value = "conferenceId") UUID conferenceId,
			@HeaderParam(value = "Authorization") String authCode) throws URISyntaxException
	{
		try
		{
			logger.info("create page entity for conference " + conferenceId + "auth code" + authCode);
			
			CrsApplicationUser loggedInUser = crsUserService.getLoggedInUser(authCode);
			
			final ConferenceEntity conferencePageBelongsTo = conferenceService.fetchConferenceBy(conferenceId);

			/*if there is no conference identified by the passed in id, then return a 400 - bad request*/
			if(conferencePageBelongsTo == null)
			{
				throw new BadRequestException("Conference specified by: " + conferenceId + " does not exist.");
			}
			
			if(newPage.getId() == null)
			{
				newPage.setId(UUID.randomUUID());
			}
			
			newPage.setConferenceId(conferenceId);
			
			Simply.logObject(newPage, ConferenceResource.class);
			
			authorizationService.authorizeConference(conferencePageBelongsTo, OperationType.UPDATE, loggedInUser);
			pageService.savePage(newPage.toDbPageEntity());
			
			PageEntity createdPage = pageService.fetchPageBy(newPage.getId());
			
			return Response.status(Status.CREATED)
					.location(new URI("/pages/" + newPage.getId()))
					.entity(Page.fromDb(createdPage, blockService.fetchBlocksForPage(createdPage.getId())))
					.build();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new InternalServerErrorException(e);
		}
	}

	/**
	 * Creates a new registration resource and associates it to the conference specified by @param conferenceId and user specified by @param authCode
	 * 
	 * Possible outcomes:
	 * 	201 Created - registration resource was created and associated to conference specified by @param conferenceId and user specified by @param authCode. if the conference
	 *                accepts payments, then a payment resource was associated to the newly created conference
	 *  400 Bad Request - if the conference specified by @param conferenceId, then bad request
	 *  401 Unauthorized - user specified by @param authCode is expired or doesn't exist
	 * 
	 * @param conference
	 * @return
	 */
	@POST
	@Path("/{conferenceId}/registrations")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createRegistration(Registration newRegistration, 
			@PathParam(value = "conferenceId") UUID conferenceId,
			@HeaderParam(value = "Authorization") String authCode) throws URISyntaxException
	{
		try
		{
			logger.info("create registration entity for conference " + conferenceId + "auth code" + authCode);

			CrsApplicationUser crsLoggedInUser = crsUserService.getLoggedInUser(authCode);

			/*if the registration this conference is supposed to belong to doesn't exist, then this is a bad request*/
			if(conferenceService.fetchConferenceBy(conferenceId) == null)
			{
				throw new BadRequestException("Conference specified by: " + conferenceId + " does not exist.");
			}

            RegistrationEntity newRegistrationEntity = newRegistration.toDbRegistrationEntity();

            /*prep the new registration entity by making sure the IDs we need to know are set properly.*/
            if(newRegistrationEntity.getId() == null) newRegistrationEntity.setId(UUID.randomUUID());
            newRegistrationEntity.setUserId(crsLoggedInUser.getId());
            newRegistrationEntity.setConferenceId(conferenceId);
            
            Simply.logObject(newRegistration, ConferenceResource.class);

            registrationService.createNewRegistration(newRegistrationEntity);
            
            /*now perform a deep update to ensure that any answers or other payments are properly saved*/
            registrationUpdateProcess.performDeepUpdate(newRegistration);

            Registration freshCopyOfNewRegistraiton = registrationFetchProcess.get(newRegistrationEntity.getId());

			
            return Response.status(Status.CREATED)
								.location(new URI("/pages/" + freshCopyOfNewRegistraiton.getId()))
								.entity(freshCopyOfNewRegistraiton)
								.build();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new InternalServerErrorException(e);
		}
	}
	

	/**
	 * Gets all the registration resources associated to the conference specified by @param conferenceId
	 * 
	 * Possible outcomes:
	 * 	200 Ok - registration resources were found and returned
	 *  401 Unauthorized - user specified by @param authCode is expired or doesn't exist, or user doesn't have read access to conference or registrations
	 * 
	 * @param conference
	 * @return
	 */
	@GET
	@Path("/{conferenceId}/registrations")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRegistrations(@PathParam(value = "conferenceId") UUID conferenceId,
			@HeaderParam(value = "Authorization") String authCode) throws URISyntaxException
	{
		try
		{
			logger.info("get registration entities " + conferenceId + "auth code" + authCode);
			
			CrsApplicationUser crsLoggedInUser = crsUserService.getLoggedInUser(authCode);

			Set<RegistrationEntity> databaseRegistrationsForConferece = registrationService.fetchAllRegistrations(conferenceId);
			
			List<Registration> webRegistrationsForConference = Lists.newArrayList();
			
			for(RegistrationEntity databaseRegistration: databaseRegistrationsForConferece)
			{	
				webRegistrationsForConference.add(registrationFetchProcess.get(databaseRegistration.getId()));
			}
			
			/*if there are any registrations to return, check the first one to ensure the user has read access.  since they're all
			 * attached to the same conference, read access applies to one and all*/
			if(!webRegistrationsForConference.isEmpty())
			{
				authorizationService.authorize(webRegistrationsForConference.get(0).toDbRegistrationEntity(), 
												conferenceService.fetchConferenceBy(conferenceId), 
												OperationType.READ, 
												crsLoggedInUser);
			}
			
			Simply.logObject(webRegistrationsForConference, ConferenceResource.class);
			
			return Response.ok(webRegistrationsForConference).build();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new InternalServerErrorException(e);
		}
	}
	
	/**
	 * Gets all the registration resource associated to the conference specified by @param conferenceId and the user specifed by @param authCode.
	 * 
	 * Possible outcomes:
	 * 	200 Ok - registration resources were found and returned
	 *  401 Unauthorized - user specified by @param authCode is expired or doesn't exist.
	 *  404 Not Found - user specified by @param authCode doesn't have a registration for conference specifed by @param conferenceId
	 * 
	 * @param conference
	 * @return
	 */
	@GET
	@Path("/{conferenceId}/registrations/current")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCurrentRegistration(@PathParam(value = "conferenceId") UUID conferenceId,
			@HeaderParam(value = "Authorization") String authCode)
	{
		try
		{
			logger.info(conferenceId);

			CrsApplicationUser loggedInUser = crsUserService.getLoggedInUser(authCode);

			logger.info(loggedInUser);

			RegistrationEntity registrationEntity = registrationService.getRegistrationByConferenceIdUserId(conferenceId, loggedInUser.getId());

			if(registrationEntity == null)
			{
				throw new NotFoundException("registration not found");
			}
			
			Registration registration = registrationFetchProcess.get(registrationEntity.getId());
			
			Simply.logObject(registration, ConferenceResource.class);

			return Response.ok(registration).build();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new InternalServerErrorException(e);
		}
	}

	private Conference setInitialContactPersonDetailsBasedOn(Conference newConference, CrsApplicationUser loggedInUser)
	{
		UserEntity user = userService.fetchUserBy(loggedInUser.getId());
		if(user != null)
		{
			newConference.setContactUser(loggedInUser.getId());
			newConference.setContactPersonName(user.getFirstName() + " " + user.getLastName());
			newConference.setContactPersonEmail(user.getEmailAddress());
			newConference.setContactPersonPhone(user.getPhoneNumber());
		}
		return newConference;
	}
}
