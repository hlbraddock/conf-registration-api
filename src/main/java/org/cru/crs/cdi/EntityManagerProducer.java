package org.cru.crs.cdi;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class EntityManagerProducer
{
	private static final String PERSISTENCE_UNIT_NAME = "crsLocalTest";

	@Produces @PersistenceContext(unitName="crsLocal")
	EntityManager em;
	
//	public EntityManager getEntityManager()
//	{
//		EntityManagerFactory emFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
//		
//		return emFactory.createEntityManager();
//	}
}
