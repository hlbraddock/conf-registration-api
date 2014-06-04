package org.cru.crs;


import org.cru.crs.utils.CrsPropertiesFactory;
import org.sql2o.Connection;
import org.testng.annotations.BeforeMethod;

/**
 * Created by ryancarlson on 4/2/14.
 */
public class AbstractServiceTest
{
	protected UnittestDatabaseBuilder builder;
	protected Connection sqlConnection;

	private CrsPropertiesFactory propertiesFactory = new CrsPropertiesFactory();
	private static boolean initialized = false;

	public AbstractServiceTest()
	{
		if (!initialized)
		{
			builder = new UnittestDatabaseBuilder();
			builder.build(propertiesFactory.get());

			initialized = true;
		}
	}

	protected void refreshConnection()
	{
		sqlConnection = new org.cru.crs.cdi.SqlConnectionProducer().getTestSqlConnection(propertiesFactory.get());
	}
}
