package org.cru.crs.migration;

import com.googlecode.flyway.core.Flyway;
import org.cru.crs.utils.CrsProperties;
import org.cru.crs.utils.CrsPropertiesFactory;

/*
 * This class is meant to provide an easy means for (re) initializing and populating the, typically local, database with seed data.
 */
public class DatabaseSetup
{
	public static void main(String[] args)
	{
		Flyway flyway = new Flyway();

		CrsProperties properties = new CrsPropertiesFactory().get();

		flyway.setDataSource(properties.getProperty("databaseUrl"),
				properties.getProperty("databaseUsername"),
				properties.getProperty("databasePassword"));

		flyway.setLocations("db.migration.structural", "db.migration.seed");

		flyway.clean();
		flyway.setInitOnMigrate(true);
		flyway.migrate();
	}
}
