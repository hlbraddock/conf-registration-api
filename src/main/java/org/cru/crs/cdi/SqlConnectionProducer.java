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
 * This producer will now live the length of the request, and return the same Sql2o Connection object for each request.  This allows
 * the connection be transaction enabled across multiple calls to the database.
 * 
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
