package org.cru.crs;


import org.cru.crs.utils.CrsProperties;
import org.cru.crs.utils.CrsPropertiesFactory;
import org.sql2o.Connection;

/**
 * Created by ryancarlson on 4/2/14.
 */
public class AbstractServiceTest
{
	protected UnittestDatabaseBuilder builder;
	protected Connection sqlConnection;

	private CrsPropertiesFactory propertiesFactory = new CrsPropertiesFactory();

	public AbstractServiceTest()
	{
		CrsProperties properties = propertiesFactory.get();

		builder = new UnittestDatabaseBuilder();
		builder.build(properties);

		sqlConnection = new org.cru.crs.cdi.SqlConnectionProducer().getTestSqlConnection(properties);
	}
}
