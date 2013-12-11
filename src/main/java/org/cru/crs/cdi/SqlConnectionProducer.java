package org.cru.crs.cdi;

import java.sql.SQLException;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.cru.crs.utils.CrsProperties;
import org.sql2o.QuirksMode;
import org.sql2o.Sql2o;

import com.google.common.base.Throwables;

/**
 * So this class presents an interesting little 'hack' using the CDI session scope to enable database transactions to work.
 * Normally, I found that injecting an instance of Sql2o for every service that needs one makes a new connection each time.
 * Ideally it would create one and reuse it throughout the request, but that's not the case.  Because a new one gets created
 * each time, that makes transactions impossible across multiple services.
 * 
 * The workaround is to make the producer method a @SessionScoped bean.  That ensures that the same producers is used each time
 * and returns the same instance of Sql2o, thus allowing transactions.  B/c this app does not actually uses sessions, this session
 * scoped bean is effectively a "Request Scoped" object since the next request that comes in will not be able to access it.
 * 
 * Testing should be done to ensure that this doesn't completely jack the system in terms of memory usage and garbage collection.
 * @author ryancarlson
 *
 */
@RequestScoped
public class SqlConnectionProducer
{

	@Inject CrsProperties properties;
	
	private org.sql2o.Connection sqlConnection;

	@Produces
	public org.sql2o.Connection getSqlConnection()
	{
		if(sqlConnection == null)
		{
			sqlConnection = new org.sql2o.Connection(new Sql2o(properties.getProperty("databaseUrl"), properties.getProperty("databaseUsername"), properties.getProperty("databasePassword"), QuirksMode.PostgreSQL));
		}
		return sqlConnection;
	}

	public org.sql2o.Connection getTestSqlConnection()
	{
		
		org.sql2o.Connection sqlConnection = new org.sql2o.Connection(new Sql2o("jdbc:postgresql://localhost/crsdb","crsuser","crsuser",QuirksMode.PostgreSQL));
		
		try {
			sqlConnection.getJdbcConnection().setAutoCommit(false);
		}
		catch(SQLException e) { /*come on... really*/
			Throwables.propagate(e);
		}
		
		return sqlConnection;
	}

}
