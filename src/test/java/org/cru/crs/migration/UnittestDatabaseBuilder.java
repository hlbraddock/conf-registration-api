package org.cru.crs.migration;

import com.googlecode.flyway.core.Flyway;
import org.cru.crs.utils.CrsProperties;
import org.cru.crs.utils.CrsPropertiesFactory;

/**
 * Created by ryancarlson on 4/1/14.
 */
public class UnittestDatabaseBuilder
{

	public static void main(String[] args)
	{
		CrsProperties properties = new CrsPropertiesFactory().get();

		Flyway flyway = new Flyway();

		flyway.setDataSource(properties.getProperty("unittestDatabaseUrl"), properties.getProperty("unittestDatabaseUsername"), properties.getProperty("unittestDatabasePassword"));
		flyway.setTarget("1.1.1");
		flyway.setInitOnMigrate(true);

		flyway.setLocations("db.migration.structural", "db.migration.seed");

		flyway.clean();

		flyway.migrate();
	}
}
