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

import org.cru.crs.model.BlockEntity;
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

		return Response.ok(requestedPage).build();
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updatePage(PageEntity page, @PathParam(value="pageId") UUID pageId)
	{
		Preconditions.checkNotNull(pageId);

		if(pageService.fetchPageBy(pageId) == null)
		{
			ConferenceEntity conference = conferenceService.fetchConferenceBy(page.getConferenceId());

			if(conference != null)
			{
				conference.getPages().add(page);
			}
			else
			{
				return Response.status(Status.BAD_REQUEST).build();
			}
		}
		else
		{
			pageService.updatePage(page);
		}

		return Response.noContent().build();
	}

	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deletePage(PageEntity page, @PathParam(value="pageId") UUID pageId)
	{
		Preconditions.checkNotNull(page.getId());

		pageService.deletePage(page);

		return Response.ok().build();
	}

	@POST
	@Path("/blocks/")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createBlock(BlockEntity newBlock, @PathParam(value="pageId") UUID pageId) throws URISyntaxException
	{
		if(newBlock.getId() == null) newBlock.setId(UUID.randomUUID());

		PageEntity page = pageService.fetchPageBy(pageId);

		if(page == null) return Response.status(Status.BAD_REQUEST).build();

		page.getBlocks().add(newBlock);

		return Response.created(new URI("/blocks/" + newBlock.getId())).build();
	}
}
