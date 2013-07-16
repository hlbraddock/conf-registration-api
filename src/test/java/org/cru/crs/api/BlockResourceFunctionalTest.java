package org.cru.crs.api;

import java.util.UUID;

import org.cru.crs.api.client.BlockResourceClient;
import org.cru.crs.api.model.Block;
import org.cru.crs.utils.Environment;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test(groups="functional-tests")
public class BlockResourceFunctionalTest
{

	static final String RESOURCE_PREFIX = "rest";
	static final String PERSISTENCE_UNIT_NAME = "crsLocalTest";
	
	Environment environment = Environment.LOCAL;
	BlockResourceClient blockClient;
	
	@BeforeMethod
	public void createClient()
	{
        String restApiBaseUrl = environment.getUrlAndContext() + "/" + RESOURCE_PREFIX;
        blockClient = ProxyFactory.create(BlockResourceClient.class, restApiBaseUrl);
	}

	@Test(groups="functional-tests")
	public void getBlock()
	{
		ClientResponse<Block> response = blockClient.getBlock(UUID.fromString("af60d878-4741-4f21-9d25-231db86e43ee"));
		
		Assert.assertEquals(response.getStatus(), 200);
		
		Block foundBlock = response.getEntity();
		
		Assert.assertNotNull(foundBlock);
		Assert.assertEquals(foundBlock.getId(), UUID.fromString(    "af60d878-4741-4f21-9d25-231db86e43ee"));
		Assert.assertEquals(foundBlock.getPageId(), UUID.fromString("7a52af36-2f3c-5e45-9f76-0af10ff50bb8"));
		Assert.assertEquals(foundBlock.getType(), "text");
	}
	
	@Test(groups="functional-tests")
	public void getBlockNotFound()
	{
		ClientResponse<Block> response = blockClient.getBlock(UUID.fromString("af60d878-4741-4f21-9d25-231db86e21ff"));
		
		Assert.assertEquals(response.getStatus(), 404);
	}
}
