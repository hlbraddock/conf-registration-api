package org.cru.crs.cdi;

import javax.enterprise.inject.Produces;

import org.sql2o.Sql2o;

public class SqlConnectionProducer
{

	@Produces 
	public Sql2o getSqlConnection()
	{
		return new Sql2o("jdbc:postgresql://localhost/crsdb", "crsuser", "crsuser");
	}
}
