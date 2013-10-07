package org.cru.crs.payment.authnet;

import java.io.IOException;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.jboss.logging.Logger;

import com.google.common.base.Preconditions;

public class HttpClientProviderImpl implements HttpProvider
{
	Logger log = Logger.getLogger(this.getClass());
	
	private int retries = 3;

	@Inject private HttpClient httpClient;

	public String getContentFromGet(String url) throws IOException
	{
		HttpMethod method = new GetMethod(url);
		return getContent(method);
	}

	// adapted from httpClient tutorial
	public String getContent(HttpMethod method) throws IOException
	{
		Preconditions.checkNotNull(httpClient, "httpClient was null");

		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler(retries, false));

		String content = null;
		try
		{
			// Execute the method.
			int statusCode = httpClient.executeMethod(method);

			if (statusCode != HttpStatus.SC_OK)
			{
				log.warn("Method failed: #0" + method.getStatusLine());
				
			}
			content = method.getResponseBodyAsString();

		}
		catch (HttpException e)
		{
			log.warn("Fatal protocol violation getting content");
			throw e;
		}
		catch (IOException e)
		{
			log.warn("Fatal transport error getting content");
			throw e;
		}
		finally
		{
			method.releaseConnection();
		}
		return content;
	}

	public String getContentFromPost(String url, Map<String, String> parameters) throws IOException
	{
		PostMethod method = new PostMethod(url);
		for (String key : parameters.keySet())
		{
			Preconditions.checkNotNull(parameters.get(key), "parameter referenced by: " + key + " was null");
			method.addParameter(key, parameters.get(key));
		}
		
		String content = getContent(method);
		if (method.getStatusCode() != HttpStatus.SC_OK)
		{
			return null;
		}
		else
		{
			return content;
		}
	}

	public HttpClient getHttpClient()
	{
		return httpClient;
	}

	public void setHttpClient(HttpClient client)
	{
		this.httpClient = client;
	}

	public Logger getLog()
	{
		return log;
	}

	public void setLog(Logger log)
	{
		this.log = log;
	}

	public int getRetries()
	{
		return retries;
	}

	public void setRetries(int retries)
	{
		this.retries = retries;
	}

}