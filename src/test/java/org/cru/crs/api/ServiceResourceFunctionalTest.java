package org.cru.crs.api;

import org.cru.crs.api.client.ServiceResourceClient;
import org.cru.crs.utils.Environment;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.util.List;

public class ServiceResourceFunctionalTest
{

	static final String RESOURCE_PREFIX = "rest";

	Environment environment = Environment.LOCAL;

	ServiceResourceClient serviceResourceClient;

	@BeforeMethod(alwaysRun = true)
	private void createClient()
	{
        String restApiBaseUrl = environment.getUrlAndContext() + "/" + RESOURCE_PREFIX;
		serviceResourceClient = ProxyFactory.create(ServiceResourceClient.class, restApiBaseUrl);
	}

	@Test(groups="functional-tests")
	public void testPostRegistrationsDownload() throws IOException
	{
		String view = "First,Last\nTest,User\n";
		String filename = "view.csv";

		ClientResponse<String> response = serviceResourceClient.postRegistrationsDownload(filename, view);

		validateResponse(view, filename, response);
	}

	@Test(groups="functional-tests")
	public void testPostPaymentsDownload() throws IOException
	{
		String view = "First,Last\nTest,User\n";
		String filename = "view.csv";

		ClientResponse<String> response = serviceResourceClient.postPaymentsDownload(filename, view);

		validateResponse(view, filename, response);
	}

	private void validateResponse(String view, String filename, ClientResponse<String> response)
	{
		Assert.assertEquals(response.getStatus(), 200);
		Assert.assertEquals(response.getEntity(), view);

		boolean foundContentDispositionHeader = false;
		MultivaluedMap<String ,String> map = response.getResponseHeaders();
		for (MultivaluedMap.Entry<String, List<String>> entry : map.entrySet())
		{
			String key = entry.getKey();
			List<String> values = entry.getValue();
			if(key.equals("Content-Disposition"))
			{
				foundContentDispositionHeader = true;
				for(String value : values)
				{
					Assert.assertEquals(value.replaceAll("\\s",""), "attachment;filename=" + filename);
				}
			}
		}

		Assert.assertTrue(foundContentDispositionHeader);
	}
}
