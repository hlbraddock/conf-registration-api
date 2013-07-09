package org.cru.crs.service;

import java.util.UUID;

import javax.persistence.EntityManager;

import org.cru.crs.model.BlockEntity;

public class BlockService
{
	EntityManager em;
	
	public BlockService(EntityManager em)
	{
		this.em = em;
	}
	
	public BlockEntity getBlockBy(UUID blockId)
	{
		return em.find(BlockEntity.class, blockId);
	}
	
	public void createNewBlock(BlockEntity newBlock)
	{
		em.persist(newBlock);
	}
	
	public void updateBlock(BlockEntity blockToUpdate)
	{
		em.merge(blockToUpdate);
	}
	
	public void deleteBlock(BlockEntity blockToDelete)
	{
		em.remove(blockToDelete);
	}
}
