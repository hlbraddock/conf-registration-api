package org.cru.crs.api;

import java.util.List;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.cru.crs.api.optimizer.ConferenceOptimizer;
import org.cru.crs.model.ConferenceEntity;
import org.cru.crs.service.ConferenceService;

@Stateless
@Path("/conferences")
public class ConferenceResource
{
	@Inject EntityManager em;
	
	@GET
	@Path("")
	@Produces(MediaType.APPLICATION_JSON)
	public List<ConferenceEntity> getConferences()
	{
		return new ConferenceService(em).fetchAllConferences();
	}
	
}
