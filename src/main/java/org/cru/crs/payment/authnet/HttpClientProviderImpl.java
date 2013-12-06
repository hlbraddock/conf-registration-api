package org.cru.crs.payment.authnet;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.jboss.logging.Logger;

import com.google.common.base.Preconditions;

public class HttpClientProviderImpl implements HttpProvider
{
	Logger log = Logger.getLogger(this.getClass());

	private HttpClient httpClient = new DefaultHttpClient();

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
		String content = null;

		InputStream inputStream = null;

		try
		{
			// Execute the method.
			HttpResponse response = httpClient.execute(requestBase);

			if (response.getStatusLine().getStatusCode() != 200)
			{
				log.warn("Method failed: #0" + response.getStatusLine());
			}

			inputStream = response.getEntity().getContent();

			content = new Scanner(inputStream).useDelimiter("\\A").next();
		}
		catch (IOException e)
		{
			log.warn("Fatal transport error getting content");
			throw e;
		}
		finally
		{
			try
			{
				if(inputStream != null)
					inputStream.close();
			}
			catch (IOException ioException)
			{
				log.warn("Could not close input stream");
			}
		}

		return content;
	}
}