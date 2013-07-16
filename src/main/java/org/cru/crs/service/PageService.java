package org.cru.crs.service;

import java.util.UUID;

import javax.persistence.EntityManager;

import org.cru.crs.model.PageEntity;

public class PageService
{
	EntityManager em;
	
	public PageService(EntityManager em)
	{
		this.em = em;
	}
	
	public PageEntity fetchPageBy(UUID id)
	{
		return em.find(PageEntity.class, id);
	}
	
	public void updatePage(PageEntity pageToUpdate)
	{
		em.merge(pageToUpdate);
	}
	
	public void deletePage(PageEntity pageToDelete)
	{
		em.remove(pageToDelete);
	}
}