package org.cru.crs.api;

import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.cru.crs.api.model.Payment;
import org.cru.crs.api.model.errors.BadRequest;
import org.cru.crs.api.model.errors.ServerError;
import org.cru.crs.api.model.errors.Unauthorized;
import org.cru.crs.api.process.PaymentProcessor;
import org.cru.crs.auth.CrsUserService;
import org.cru.crs.auth.UnauthorizedException;
import org.cru.crs.auth.authz.AuthorizationService;
import org.cru.crs.auth.authz.OperationType;
import org.cru.crs.auth.model.CrsApplicationUser;
import org.cru.crs.model.RegistrationEntity;
import org.cru.crs.payment.authnet.Authnet;
import org.cru.crs.service.ConferenceService;
import org.cru.crs.service.PaymentService;
import org.cru.crs.service.RegistrationService;

@Path("/payments/{paymentId}")
public class PaymentResource
{
	@Inject CrsUserService crsUserService;
	
    @Inject AuthorizationService authorizationService;
	@Inject PaymentService paymentService;
	@Inject RegistrationService registrationService;
	@Inject ConferenceService conferenceService;
	
	@Inject @Authnet PaymentProcessor paymentProcessor;
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updatePayment(Payment payment, @HeaderParam(value="Authorization") String authCode)
	{
		try
		{
			CrsApplicationUser loggedInUser = crsUserService.getLoggedInUser(authCode);
			
			if(payment.getRegistrationId() == null)
			{
				return Response.ok(new BadRequest().setCustomErrorMessage("Payment must have a registration ID.")).build();
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
