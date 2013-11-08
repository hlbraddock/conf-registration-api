package org.cru.crs.cdi;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.cru.crs.utils.CrsProperties;
import org.sql2o.QuirksMode;
import org.sql2o.Sql2o;

public class SqlConnectionProducer
{

	@Inject CrsProperties properties;
	
	@Produces 
	public Sql2o getSqlConnection()
	{
		return new Sql2o(properties.getProperty("databaseUrl"), properties.getProperty("databaseUsername"), properties.getProperty("databasePassword"), QuirksMode.PostgreSQL);
	}
	
	public Sql2o getTestSqlConnection()
	{
		return new Sql2o("jdbc:postgresql://localhost/crsdb", "crsuser", "crsuser", QuirksMode.PostgreSQL);
	}
}
