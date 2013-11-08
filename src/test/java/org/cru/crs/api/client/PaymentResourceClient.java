package org.cru.crs.api.client;

import java.net.URISyntaxException;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.cru.crs.api.model.Payment;
import org.jboss.resteasy.client.ClientResponse;

@Path("/payments/{paymentId}")
public interface PaymentResourceClient
{
	@GET
	@Produces(MediaType.APPLICATION_JSON)
    public ClientResponse<Payment> getPayment(@PathParam(value = "paymentId") UUID paymentId, @HeaderParam(value = "Authorization") String authCode) throws URISyntaxException;
    
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public ClientResponse updatePayment(@PathParam(value = "paymentId") UUID paymentId, Payment payment, @HeaderParam(value="Authorization") String authCode);
}
