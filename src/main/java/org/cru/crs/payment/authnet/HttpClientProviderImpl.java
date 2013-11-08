package org.cru.crs.payment.authnet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.jboss.logging.Logger;

import com.google.common.base.Preconditions;

public class HttpClientProviderImpl implements HttpProvider
{
	Logger log = Logger.getLogger(this.getClass());
	
	private int retries = 3;
	private CloseableHttpClient httpClient = HttpClients.createDefault();
	
	public String getContentFromGet(String url) throws IOException
	{
		return getContent(new HttpGet(url));
	}

	public String getContentFromPost(String url, Map<String, String> parameters) throws IOException
	{
		HttpPost post = new HttpPost(url);
		List<NameValuePair> postParams = new ArrayList<NameValuePair>();
		for (String key : parameters.keySet())
		{
			Preconditions.checkNotNull(parameters.get(key), "parameter referenced by: " + key + " was null");
			postParams.add(new BasicNameValuePair(key, parameters.get(key)));
		}
		
		post.setEntity(new UrlEncodedFormEntity(postParams));
		
		return getContent(post);
	}
	
	// adapted from httpClient tutorial
	public String getContent(HttpRequestBase requestBase) throws IOException
	{
		CloseableHttpResponse response = null;
		String content = null;

		try
		{
			// Execute the method.
			response = httpClient.execute(requestBase);

			if (response.getStatusLine().getStatusCode() != 200)
			{
				log.warn("Method failed: #0" + response.getStatusLine());

			}
			content = new Scanner(response.getEntity().getContent()).useDelimiter("\\A").next();

		}
		catch (IOException e)
		{
			log.warn("Fatal transport error getting content");
			throw e;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(response != null) response.close();
		}
		return content;
	}
}