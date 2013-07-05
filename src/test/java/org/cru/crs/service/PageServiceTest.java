package org.cru.crs.service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

public class PageServiceTest
{
	private static final String PERSISTENCE_UNIT_NAME = "crsLocalTest";
	private EntityManagerFactory emFactory;
	private EntityManager em;
	
	private PageService pageService;
	
	@BeforeClass
	public void setup()
	{
		emFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		em = emFactory.createEntityManager();
		
		pageService = new PageService(em);
	}
	
	@AfterClass
	public void cleanup()
	{
		em.close();
		emFactory.close();
	}
	
}
