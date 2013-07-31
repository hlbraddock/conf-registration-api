package org.cru.crs.servlet;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.common.base.Preconditions;

import edu.yale.its.tp.cas.client.CASReceipt;
import edu.yale.its.tp.cas.client.filter.CASFilter;

public class CrsLoginServlet implements Servlet
{

	ServletConfig config;
	
	@Override
	public void destroy()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public ServletConfig getServletConfig()
	{
		return config;
	}

	@Override
	public String getServletInfo()
	{
		return null;
	}

	@Override
	public void init(ServletConfig config) throws ServletException
	{
		this.config = config;
	}

	@Override
	public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException
	{
		Preconditions.checkArgument(request instanceof HttpServletRequest, "Request MUST be of type HttpServletRequest");
		Preconditions.checkArgument(response instanceof HttpServletResponse, "Response MUST be of type HttpServletResponse");
		
		HttpServletRequest httpRequest = (HttpServletRequest)request;
		HttpServletResponse httpResponse = (HttpServletResponse)response;
		
		/*Get the session*/
		HttpSession session = httpRequest.getSession();
		
		/*redirect to Relay if session is null*/
		if(session == null) httpResponse.sendRedirect("https://signin.cru.org/cas/login?service=" + 
														config.getInitParameter("redirectServiceUrl"));
		
		/*if no GUID, check for attributes from CAS*/
		CASReceipt casReceipt = (CASReceipt)session.getAttribute(CASFilter.CAS_FILTER_RECEIPT);
		
		/*if no attributes, redirect to CAS Server for auth*/
		if(casReceipt == null) httpResponse.sendRedirect("https://signin.cru.org/cas/login?service=" + 
															config.getInitParameter("redirectServiceUrl"));
		
		/*with attributes, fetch GUID and store in session*/
		session.setAttribute("relaySsoGuid", UUID.fromString((String)casReceipt.getAttributes().get("ssoGuid")));
		
		/*redirect to... */
		((HttpServletResponse)response).sendRedirect(config.getInitParameter("destinationUri"));
		
	}

}
