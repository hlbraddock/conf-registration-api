package org.cru.crs.cdi;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class EntityManagerProducer
{

	@Produces @PersistenceContext(unitName="crsDev")
	EntityManager em;
}
