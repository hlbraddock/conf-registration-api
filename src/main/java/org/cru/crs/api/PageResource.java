package org.cru.crs.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.cru.crs.api.model.Block;
import org.cru.crs.api.model.Page;
import org.cru.crs.auth.CrsUserService;
import org.cru.crs.auth.UnauthorizedException;
import org.cru.crs.auth.model.CrsApplicationUser;
import org.cru.crs.model.ConferenceEntity;
import org.cru.crs.model.PageEntity;
import org.cru.crs.service.ConferenceService;
import org.cru.crs.service.PageService;
import org.cru.crs.utils.IdComparer;

@Stateless
@Path("/pages/{pageId}")
public class PageResource
{
	@Inject PageService pageService;
    @Inject ConferenceService conferenceService;
    
    @Context HttpServletRequest request;
    @Inject CrsUserService userService;
    
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPage(@PathParam(value="pageId") UUID pageId)
	{
		PageEntity requestedPage = pageService.fetchPageBy(pageId);

		if(requestedPage == null) return Response.status(Status.NOT_FOUND).build();

		return Response.ok(Page.fromJpa(requestedPage)).build();
	}

	/**
	 * Update the page or create it.
	 * 
	 * @param page
	 * @param pageId
	 * @return
	 */
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updatePage(Page page, 
			@PathParam(value="pageId") UUID pageId,
			@HeaderParam(value="Authorization") String authCode)
	{
		try
		{
			CrsApplicationUser loggedInUser = userService.getUserFromSession(request.getSession(), authCode);
			ConferenceEntity conferencePageBelongsTo = conferenceService.fetchConferenceBy(page.getConferenceId());

			/**
			 * If the Path pageId does not match the pageId in the body of the JSON object,
			 * then fail fast and return a 400.  
			 */
			if(IdComparer.idsAreNotNullAndDifferent(pageId, page.getId()))
			{
				return Response.status(Status.BAD_REQUEST).build();
			}

			/**
			 * Now that we know that the pageIds are not different.. take the first not-null
			 * one we find and treat it as the "official" page ID.  Note in this case it still
			 * could be null at this point, and we would need to create a new page.
			 */
			UUID officialPageId = pageId != null ? pageId : page.getId(); 

			if(officialPageId == null || pageService.fetchPageBy(officialPageId) == null)
			{
				/**
				 * If the client didn't specify an ID for the new page, we'll create one
				 */
				if(officialPageId == null) officialPageId = UUID.randomUUID();

				/**
				 * Make sure the Page ID is set in the object, it could be that it was only
				 * specified as a path parameter.
				 */
				conferenceService.addPageToConference(conferencePageBelongsTo, page.toJpaPageEntity(), loggedInUser);
			}
			else
			{
				pageService.updatePage(page.toJpaPageEntity().setId(officialPageId), loggedInUser);
			}

			return Response.noContent().build();
		}
		catch(UnauthorizedException e)
		{
			return Response.status(Status.UNAUTHORIZED).build();
		}
	}

	/**
	 * This method will delete a page specified by ID.
	 * 
	 * @param page
	 * @param pageId
	 * @return
	 */
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deletePage(@PathParam(value="pageId") UUID pageId, @HeaderParam(value="Authorization") String authCode)
	{
		try
		{
			CrsApplicationUser loggedInUser = userService.getUserFromSession(request.getSession(), authCode);

			if(pageId == null)
			{
				return Response.status(Status.BAD_REQUEST).build();
			}

			pageService.deletePage(pageId, loggedInUser);

			return Response.ok().build();
		}
		catch (UnauthorizedException e)
		{
			return Response.status(Status.UNAUTHORIZED).build();
		}
	}

	@POST
	@Path("/blocks/")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createBlock(Block newBlock, 
			@PathParam(value="pageId") UUID pageId,
			@HeaderParam(value="Authorization") String authCode) throws URISyntaxException
	{
		try
		{
			CrsApplicationUser loggedInUser = userService.getUserFromSession(request.getSession(), authCode);
			PageEntity pageBlockBelongsTo = pageService.fetchPageBy(pageId);

			/*if the page id, specified in the incoming block doesn't map to a page,
			 * then this is a bad request*/
			if(pageBlockBelongsTo == null)
			{
				return Response.status(Status.BAD_REQUEST).build();
			}

			pageService.addBlockToPage(pageBlockBelongsTo, newBlock.toJpaBlockEntity(), loggedInUser);

			return Response.status(Status.CREATED)
					.location(new URI("/blocks/" + newBlock.getId()))
					.entity(newBlock)
					.build();
		} 
		catch (UnauthorizedException e)
		{
			return Response.status(Status.UNAUTHORIZED).build();
		}
	}
}
