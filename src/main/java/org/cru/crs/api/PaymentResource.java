package org.cru.crs.api;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.cru.crs.api.model.Payment;
import org.cru.crs.api.process.PaymentProcessor;
import org.cru.crs.auth.CrsUserService;
import org.cru.crs.auth.authz.AuthorizationService;
import org.cru.crs.auth.authz.OperationType;
import org.cru.crs.auth.model.CrsApplicationUser;
import org.cru.crs.model.PaymentEntity;
import org.cru.crs.model.RegistrationEntity;
import org.cru.crs.service.ConferenceService;
import org.cru.crs.service.PaymentService;
import org.cru.crs.service.RegistrationService;
import org.cru.crs.utils.IdComparer;
import org.cru.crs.utils.Simply;
import org.jboss.logging.Logger;

@Path("/payments")
public class PaymentResource extends TransactionalResource
{
	@Inject CrsUserService crsUserService;
	
    @Inject AuthorizationService authorizationService;
	@Inject PaymentService paymentService;
	@Inject RegistrationService registrationService;
	@Inject ConferenceService conferenceService;
	
	@Inject PaymentProcessor paymentProcessor;
	
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
	@Path("/{paymentId}")
	@Produces(MediaType.APPLICATION_JSON)
    public Response getPayment(@PathParam(value = "paymentId") UUID paymentId, @HeaderParam(value = "Authorization") String authCode) throws URISyntaxException
    {
		logger.info("get payment entity " + paymentId + " and auth code " + authCode);

		CrsApplicationUser crsLoggedInUser = crsUserService.getLoggedInUser(authCode);

		PaymentEntity paymentEntity = paymentService.fetchPaymentBy(paymentId);

		if(paymentEntity == null)
		{
			throw new NotFoundException("Payment: " + paymentId + " was not found");
		}

		RegistrationEntity registrationEntityForRequestedPayment = registrationService.getRegistrationBy(paymentEntity.getRegistrationId());

		/*If the user has read access to the registration this payment goes with, then they also have read access to the payment*/
		authorizationService.authorizeRegistration(registrationEntityForRequestedPayment,
				conferenceService.fetchConferenceBy(registrationEntityForRequestedPayment.getConferenceId()),
				OperationType.READ,
				crsLoggedInUser);

		Simply.logObject(Payment.fromDb(paymentEntity), this.getClass());

		return Response.ok(Payment.fromDb(paymentEntity)).build();
    }
	
	/**
	 * Creates payment resource.  If the payment is marked as readyToProcess = true, the server will attempt to process the payment
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
	 * @throws URISyntaxException 
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createPayment(Payment payment, @HeaderParam(value="Authorization") String authCode) throws URISyntaxException
	{
		logger.info("create payment entity with auth code " + authCode);

		CrsApplicationUser loggedInUser = crsUserService.getLoggedInUser(authCode);

		if(payment.getId() == null) payment.setId(UUID.randomUUID());

		if(payment.getRegistrationId() == null)
		{
			throw new BadRequestException("Payment's registration id was null");
		}

		RegistrationEntity registrationEntity = registrationService.getRegistrationBy(payment.getRegistrationId());

		authorizationService.authorizeRegistration(registrationEntity, 
				conferenceService.fetchConferenceBy(registrationEntity.getConferenceId()),
				OperationType.UPDATE,
				loggedInUser);

		if(paymentService.fetchPaymentBy(payment.getId()) == null)
		{
			if(payment.getId() == null) payment.setId(UUID.randomUUID());
			paymentService.createPaymentRecord(payment.toDbPaymentEntity());
		}
		else
		{
			throw new BadRequestException("Payment with id: " + payment.getId() + " already exists.");
		}

		if(payment.isReadyToProcess())
		{
			paymentProcessor.process(payment, loggedInUser);
		}

		return Response.status(Status.CREATED)
				.location(new URI("/conferences/" + payment.getId()))
				.entity(Payment.fromDb(paymentService.fetchPaymentBy(payment.getId()))).build();
	}
	
	/**
	 * Updates the payment resource specified by @param paymentId.  However the only "valid updating" of a payment is processing it.  Otherwise the client
	 * has no business updating a stored payment.
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
	 * @throws IOException 
	 */
	@PUT
	@Path("/{paymentId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updatePayment(Payment payment, @PathParam("paymentId") UUID paymentId, @HeaderParam(value="Authorization") String authCode)
	{
		logger.info("update payment entity " + paymentId + " with auth code " + authCode);

		CrsApplicationUser loggedInUser = crsUserService.getLoggedInUser(authCode);

		if(IdComparer.idsAreNotNullAndDifferent(paymentId, payment.getId()))
		{
			throw new BadRequestException("Path id " + paymentId + " and entity payment id " + payment.getId() + " do not match.");
		}

		if(payment.getRegistrationId() == null)
		{
			throw new BadRequestException("Payment's registration id was null");
		}

		RegistrationEntity registrationEntity = registrationService.getRegistrationBy(payment.getRegistrationId());

		authorizationService.authorizeRegistration(registrationEntity, 
				conferenceService.fetchConferenceBy(registrationEntity.getConferenceId()),
				OperationType.UPDATE,
				loggedInUser);

		if(paymentService.fetchPaymentBy(payment.getId()) == null)
		{
			if(payment.getId() == null) payment.setId(UUID.randomUUID());
			paymentService.createPaymentRecord(payment.toDbPaymentEntity());
		}

		if(payment.isReadyToProcess())
		{
			paymentProcessor.process(payment, loggedInUser);
		}

		return Response.noContent().build();
	}
}
