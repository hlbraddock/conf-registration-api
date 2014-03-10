package org.cru.crs.api.utils;

import org.cru.crs.api.TransactionalResource;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/services")
public class ServiceResource extends TransactionalResource
{

	/**
	 * Service endpoint to produce content disposition header in a response
	 */
	@POST
	@Path("/download/{a:registrations|payments}/{filename}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.TEXT_PLAIN)
	public Response download(@PathParam(value = "filename") String filename, String view)
	{
		String vanillaView = "";

		String[] viewArray = view.split(System.getProperty("line.separator"));

		for(String line : viewArray)
		{
			if(line.startsWith("----"))
				continue;
			if(line.startsWith("Content-Disposition:"))
				continue;
			if(line.trim().length() <= 0)
				continue;

			vanillaView += line + System.getProperty("line.separator");
		}

		return Response.ok(vanillaView).header("Content-Disposition", "attachment; filename=" + filename).build();
	}
}
