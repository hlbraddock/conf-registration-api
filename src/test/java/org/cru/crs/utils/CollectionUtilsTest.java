package org.cru.crs.utils;

import junit.framework.Assert;
import org.cru.crs.api.model.Block;
import org.cru.crs.model.BlockEntity;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CollectionUtilsTest
{
	private static Block getBlock(UUID uuid)
	{
		BlockEntity blockEntity = new BlockEntity();
		blockEntity.setId(uuid);
		return Block.fromJpa(blockEntity);
	}

	@Test(groups="unittest")
	public void testNotFoundBlock()
	{
		UUID uuid0 = UUID.randomUUID();
		UUID uuid1 = UUID.randomUUID();
		UUID uuid2 = UUID.randomUUID();
		UUID uuid3 = UUID.randomUUID();
		UUID uuid4 = UUID.randomUUID();

		Set<Block> first = new HashSet<Block>(Arrays.asList(getBlock(uuid0), getBlock(uuid1), getBlock(uuid2)));
		Set<Block> second = new HashSet<Block>(Arrays.asList(getBlock(uuid2), getBlock(uuid3), getBlock(uuid4)));

		Set <Block> notFound = (Set<Block>)CollectionUtils.firstNotFoundInSecond(first, second);

		for(Block block : notFound)
			Assert.assertTrue(block.getId().equals(uuid1) || block.getId().equals(uuid0));
	}

	@Test(groups="unittest")
	public void testNotFound()
	{
		Set<String> first = new HashSet<String>(Arrays.asList("one", "two", "twohalf", "three"));
		Set<String> second = new HashSet<String>(Arrays.asList("two", "three", "four"));

		Set <String> notFound = (Set<String>)CollectionUtils.firstNotFoundInSecond(first, second);

		for(String string : notFound)
			Assert.assertTrue(string.equals("one") || string.equals("twohalf"));
	}

	@Test(groups="unittest")
	public void testNotFound2()
	{
		Set<String> first = new HashSet<String>(Arrays.asList("one", "two", "twohalf", "three"));
		Set<String> second = new HashSet<String>(Arrays.asList("one", "two", "twohalf", "three"));

		Set <String> notFound = (Set<String>)CollectionUtils.firstNotFoundInSecond(first, second);

		Assert.assertTrue(notFound.size() == 0);
	}
}
