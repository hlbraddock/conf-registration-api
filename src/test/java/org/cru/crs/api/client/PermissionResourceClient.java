package org.cru.crs.api.client;

import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.cru.crs.api.model.Permission;
import org.jboss.resteasy.client.ClientResponse;

@Path("/permissions/{permissionId}")
public interface PermissionResourceClient {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ClientResponse<Permission> getPermission(@PathParam(value = "permissionId") UUID permissionId,
			 @HeaderParam(value = "Authorization") String authCode);
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public ClientResponse<Permission> updatePermission(@PathParam(value = "permissionId") UUID permissionId,
			 @HeaderParam(value = "Authorization") String authCode,
			 Permission permission);
	
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	public ClientResponse revokePermission(@PathParam(value = "permissionId") UUID permissionId,
									 @HeaderParam(value = "Authorization") String authCode);
	
}
