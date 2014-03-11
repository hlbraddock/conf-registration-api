package org.cru.crs.api.client;

import org.jboss.resteasy.client.ClientResponse;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/services")
public interface ServiceResourceClient
{
	@Path("/download/registrations/{filename}")
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public ClientResponse<String> postRegistrationsDownload(@PathParam(value = "filename") String filename, String view);

	@Path("/download/payments/{filename}")
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public ClientResponse<String> postPaymentsDownload(@PathParam(value = "filename") String filename, String view);
}
