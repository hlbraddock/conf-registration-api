package org.cru.crs.api;

import java.net.URISyntaxException;
import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.cru.crs.api.model.Payment;
import org.cru.crs.api.model.errors.BadRequest;
import org.cru.crs.api.model.errors.NotFound;
import org.cru.crs.api.model.errors.ServerError;
import org.cru.crs.api.model.errors.Unauthorized;
import org.cru.crs.api.process.PaymentProcessor;
import org.cru.crs.auth.CrsUserService;
import org.cru.crs.auth.UnauthorizedException;
import org.cru.crs.auth.authz.AuthorizationService;
import org.cru.crs.auth.authz.OperationType;
import org.cru.crs.auth.model.CrsApplicationUser;
import org.cru.crs.model.PaymentEntity;
import org.cru.crs.model.RegistrationEntity;
import org.cru.crs.payment.authnet.Authnet;
import org.cru.crs.service.ConferenceService;
import org.cru.crs.service.PaymentService;
import org.cru.crs.service.RegistrationService;
import org.cru.crs.utils.IdComparer;
import org.cru.crs.utils.Simply;
import org.jboss.logging.Logger;

@Path("/payments/{paymentId}")
public class PaymentResource
{
	@Inject CrsUserService crsUserService;
	
    @Inject AuthorizationService authorizationService;
	@Inject PaymentService paymentService;
	@Inject RegistrationService registrationService;
	@Inject ConferenceService conferenceService;
	
	@Inject @Authnet PaymentProcessor paymentProcessor;
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	/**
	 * Returns a payment resource specified by @param paymentId
	 * 
	 * Possible Outcomes:
	 * 	200 Ok - found payment specified by @param paymentId and the user specified by @param authCode has read access to the registration specified by @param registrationId
	 *  404 Not Found - no payment resource specified by @param paymentId
	 *  401 Unauthorized - user specified by @param authCode is expired, doesn't exist or doesn't have read access to the registration specifed by @param registrationId.
	 *  
	 * @param authCode
	 * @return
	 */
	@GET
    @Path("/payment/{paymentId}")
	@Produces(MediaType.APPLICATION_JSON)
    public Response getPayment(@PathParam(value = "paymentId") UUID paymentId, @HeaderParam(value = "Authorization") String authCode) throws URISyntaxException
    {
		try
		{
			logger.info("get payment entity " + paymentId + " and auth code " + authCode);
			
			CrsApplicationUser crsLoggedInUser = crsUserService.getLoggedInUser(authCode);

			PaymentEntity paymentEntity = paymentService.fetchPaymentBy(paymentId);
			
			if(paymentEntity == null)
			{
				return Response.ok(new NotFound()).build();
			}

			RegistrationEntity registrationEntityForRequestedPayment = registrationService.getRegistrationBy(paymentEntity.getRegistrationId());
			
			/*If the user has read access to the registration this payment goes with, then they also have read access to the payment*/
			authorizationService.authorize(registrationEntityForRequestedPayment,
											conferenceService.fetchConferenceBy(registrationEntityForRequestedPayment.getConferenceId()),
											OperationType.READ,
											crsLoggedInUser);
											
			Simply.logObject(Payment.fromJpa(paymentEntity), this.getClass());

			return Response.ok(Payment.fromJpa(paymentEntity)).build();
		}
		catch(UnauthorizedException e)
		{
			return Response.ok(new Unauthorized()).build();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return Response.ok(new ServerError(e)).build();
		}
    }
	
	/**
	 * Updates the payment resource specified by @param paymentId.  If the payment is marked as readyToProcess = true, the server will attempt to process the payment
	 * against upstream payment processor (authorize.net or other).
	 * 
	 * Possible Outcomes:
	 * 	200 Ok - found payment specified by @param paymentId and the user specified by @param authCode has read access to the registration specified by @param registrationId
	 *  204 No Content - payment was created/updated successfully
	 *  400 Bad Request - the payment does not have a registration ID, the registration ID does not map to a registration in the system, or the path and entity
	 *                    have different payment ID's and proceeding could be dangerous.
	 *  401 Unauthorized - user specified by @param authCode is expired, doesn't exist or doesn't have update access to the registration that owns the specified payment
	 *  500 Server Error - something, unrelated to payment processing, went wrong
	 *  502 Bad Gateway - something related to talking to the upstream payment server went wrong.  payment was not processed.
	 * @param authCode
	 * @return
	 */
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updatePayment(@PathParam(value = "paymentId") UUID paymentId, Payment payment, @HeaderParam(value="Authorization") String authCode)
	{
		try
		{
			logger.info("update payment entity " + paymentId + " and auth code " + authCode);
			
			CrsApplicationUser loggedInUser = crsUserService.getLoggedInUser(authCode);
			
			if(payment.getRegistrationId() == null)
			{
				return Response.ok(new BadRequest().setCustomErrorMessage("Payment must have a registration ID.")).build();
			}
			
			if(IdComparer.idsAreNotNullAndDifferent(paymentId, payment.getId()))
			{
				return Response.ok(new BadRequest().setCustomErrorMessage("Path and entity payment IDs did not match.  Cannot proceed.")).build();
			}
			
			RegistrationEntity registrationEntity = registrationService.getRegistrationBy(payment.getRegistrationId());
			
			authorizationService.authorize(registrationEntity, 
											conferenceService.fetchConferenceBy(registrationEntity.getConferenceId()),
											OperationType.UPDATE,
											loggedInUser);
			
			if(paymentService.fetchPaymentBy(payment.getId()) == null)
			{
				if(payment.getId() == null) payment.setId(UUID.randomUUID());
				paymentService.createPaymentRecord(payment.toJpaPaymentEntity());
			}
			else
			{
				paymentService.updatePayment(payment.toJpaPaymentEntity());
			}
			
			if(payment.isReadyToProcess())
			{
				org.cru.crs.api.model.Error processingError = paymentProcessor.process(payment, loggedInUser);
				if(processingError != null) return Response.ok(processingError).build();
			}
			
			return Response.noContent().build();
		}
		catch(UnauthorizedException e)
		{
			return Response.ok(new Unauthorized()).build();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return Response.ok(new ServerError()).build();
		}
	}
}
