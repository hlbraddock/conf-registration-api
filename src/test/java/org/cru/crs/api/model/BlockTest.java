package org.cru.crs.api.model;

import junit.framework.Assert;
import org.cru.crs.model.BlockEntity;
import org.testng.annotations.Test;

import java.util.UUID;

public class BlockTest
{
	private static Block getBlock(UUID uuid)
	{
		BlockEntity blockEntity = new BlockEntity();
		blockEntity.setId(uuid);
		return Block.fromJpa(blockEntity);
	}

	@Test(groups="db-integration-tests")
	public void testBlockEquality()
	{
		UUID uuid1 = UUID.randomUUID();
		Block block1 = getBlock(uuid1);
		Block block2 = getBlock(uuid1);

		Assert.assertEquals(block1, block2);
	}
}
