package org.cru.crs.api.client;

import org.cru.crs.api.model.Permission;
import org.jboss.resteasy.client.ClientResponse;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.UUID;

@Path("/permissions")
public interface PermissionResourceClient {

	@GET
	@Path("/{permissionId}")
	@Produces(MediaType.APPLICATION_JSON)
	public ClientResponse<Permission> getPermission(@PathParam(value = "permissionId") UUID permissionId,
			 @HeaderParam(value = "Authorization") String authCode);
	
	@PUT
	@Path("/{permissionId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public ClientResponse<Permission> updatePermission(@PathParam(value = "permissionId") UUID permissionId,
			 @HeaderParam(value = "Authorization") String authCode,
			 Permission permission);
	
	@PUT
	@Path("/{activationCode}/accept")
	@Consumes(MediaType.APPLICATION_JSON)
	public ClientResponse acceptPermission(@PathParam(value = "activationCode") String activationCode,
			 @HeaderParam(value = "Authorization") String authCode);
	
	@DELETE
	@Path("/{permissionId}")
	public ClientResponse revokePermission(@PathParam(value = "permissionId") UUID permissionId,
									 @HeaderParam(value = "Authorization") String authCode);
	
}
