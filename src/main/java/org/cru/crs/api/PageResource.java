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
import org.cru.crs.utils.IdComparer;

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
	 * Update the page or create it.
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
			 * If there is no conference ID, then this is a bad request.  We must know
			 * which conference to associate the page with.
			 */
			if(page.getConferenceId() == null || conferenceService.fetchConferenceBy(page.getConferenceId()) == null)
			{
				return Response.status(Status.BAD_REQUEST).build();
			}
			/**
			 * If the client didn't specify an ID for the new page, we'll create one
			 */
			if(officialPageId == null) officialPageId = UUID.randomUUID();
			
			/**
			 * Make sure the Page ID is set in the object, it could be that it was only
			 * specified as a path parameter.
			 */
			conferenceService.fetchConferenceBy(page.getConferenceId())
								.getPages()
								.add(page.setId(officialPageId).toJpaPageEntity());
		}
		else
		{
			pageService.updatePage(page.toJpaPageEntity().setId(officialPageId));
		}
		
		return Response.noContent().build();
		
	}

	/**
	 * This method will delete a page specified by ID.  If the ID in the path
	 * doesn't match the one in the body, a 400-Bad Request is returned to ensure
	 * data integrity.
	 * 
	 * @param page
	 * @param pageId
	 * @return
	 */
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deletePage(Page page, @PathParam(value="pageId") UUID pageId)
	{
		/**
		 * Again, if the IDs are different in the path and body, fail fast. We
		 * want to be sure we know what we're doing is correct on delete operations
		 */
		if(IdComparer.idsAreNotNullAndDifferent(pageId, page.getId()))
		{
			return Response.status(Status.BAD_REQUEST).build();
		}
		
		/**
		 * Matt drees to fill in this method :)
		 */

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
