package org.cru.crs.cdi;

import java.sql.SQLException;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.cru.crs.domain.ProfileType;
import org.cru.crs.utils.CrsProperties;
import org.jboss.logging.Logger;
import org.postgresql.util.PGobject;
import org.sql2o.QuirksMode;
import org.sql2o.Sql2o;

import com.google.common.base.Throwables;
import org.sql2o.converters.Convert;
import org.sql2o.converters.ConverterException;
import org.sql2o.converters.EnumConverter;

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

			// register with sql2o a converter for reading ProfileType
			Convert.registerConverter(ProfileType.class, new PostgresPGObjectToEnumConverter(ProfileType.class));
		}
		return sqlConnection;
	}

	private static class PostgresPGObjectToEnumConverter extends EnumConverter
	{
		private Logger logger = Logger.getLogger(PostgresPGObjectToEnumConverter.class);

		public PostgresPGObjectToEnumConverter(Class enumType)
		{
			super(enumType);
		}

		@Override
		public Enum convert(Object o) throws ConverterException
		{
			if (o instanceof PGobject)
			{
				try
				{
					return ProfileType.valueOf(((PGobject) o).getValue().toUpperCase());
				}
				catch (Exception e)
				{
					logger.error("Could not convert from " + o.getClass() + " to ProfileType enum ", e);
				}
			}

			// fallback to default impl
			return super.convert(o);
		}
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
