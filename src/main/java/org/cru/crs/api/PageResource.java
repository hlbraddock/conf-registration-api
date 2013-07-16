package org.cru.crs.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

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

import org.cru.crs.api.model.Block;
import org.cru.crs.api.model.Page;
import org.cru.crs.model.ConferenceEntity;
import org.cru.crs.model.PageEntity;
import org.cru.crs.service.ConferenceService;
import org.cru.crs.service.PageService;

import com.google.common.base.Preconditions;

@Stateless
@Path("/pages/{pageId}")
public class PageResource
{
	@Inject PageService pageService;
    @Inject ConferenceService conferenceService;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPage(@PathParam(value="pageId") UUID pageId)
	{
		PageEntity requestedPage = pageService.fetchPageBy(pageId);

		if(requestedPage == null) return Response.status(Status.NOT_FOUND).build();

		return Response.ok(Page.fromJpa(requestedPage)).build();
	}

	/**
	 * Update the page or create it under the following conditions:
	 * -If the page exists
	 * 
	 * @param page
	 * @param pageId
	 * @return
	 */
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updatePage(Page page, @PathParam(value="pageId") UUID pageId)
	{		
		/**
		 * If the Path pageId does not match the pageId in the body of the JSON object,
		 * then fail fast and return a 400.  
		 */
		if(pageId != null && page.getId() != null && !pageId.equals(page.getId()))
		{
			return Response.status(Status.BAD_REQUEST).build();
		}
		
		UUID canonicalPageId = pageId != null ? pageId : page.getId(); 
		
		if(pageService.fetchPageBy(canonicalPageId) == null)
		{
			if(page.getConferenceId() == null || conferenceService.fetchConferenceBy(page.getConferenceId()) == null)
			{
				return Response.status(Status.BAD_REQUEST).build();
			}
		
			/**
			 * If there is no ID, then create a random one.
			 */
			if(canonicalPageId == null) canonicalPageId = UUID.randomUUID();
			
			/**
			 * Make sure the Page ID is set in the object, it could be that it was only
			 * specified as a path parameter.
			 */
			ConferenceEntity conference = conferenceService.fetchConferenceBy(page.getConferenceId());
			conference.getPages().add(page.setId(canonicalPageId).toJpaPageEntity());
		}
		else
		{
			pageService.updatePage(page.toJpaPageEntity().setId(canonicalPageId));
		}
		
		return Response.noContent().build();
		
	}

	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deletePage(Page page, @PathParam(value="pageId") UUID pageId)
	{
		Preconditions.checkNotNull(page.getId());

		pageService.deletePage(page.toJpaPageEntity());

		return Response.ok().build();
	}

	@POST
	@Path("/blocks/")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createBlock(Block newBlock, @PathParam(value="pageId") UUID pageId) throws URISyntaxException
	{
		if(newBlock.getId() == null) newBlock.setId(UUID.randomUUID());

		PageEntity page = pageService.fetchPageBy(pageId);

		if(page == null) return Response.status(Status.BAD_REQUEST).build();

		page.getBlocks().add(null);

		return Response.created(new URI("/blocks/" + newBlock.getId())).build();
	}
}
