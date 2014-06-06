package org.cru.crs;


import org.cru.crs.cdi.SqlConnectionProducer;
import org.cru.crs.utils.CrsPropertiesFactory;
import org.sql2o.Connection;

/**
 * Any test class which has direct access to the database via an sql2o connection should extend this class.
 * This class will ensure that at the beginning of the test suite, the unittest database is reset to its known
 * starting state.
 *
 * Refresh connection will provide a fresh connection in case the last connection was rolled back in a method call.
 */
public class AbstractTestWithDatabaseConnectivity
{
	protected Connection sqlConnection;

	private CrsPropertiesFactory propertiesFactory = new CrsPropertiesFactory();

	public AbstractTestWithDatabaseConnectivity()
	{

	}

	protected void refreshConnection()
	{
		sqlConnection = new SqlConnectionProducer().getTestSqlConnection(propertiesFactory.get());
	}
}
