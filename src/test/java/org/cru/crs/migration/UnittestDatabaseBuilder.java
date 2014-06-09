package org.cru.crs.migration;

import com.googlecode.flyway.core.Flyway;
import com.googlecode.flyway.core.api.MigrationVersion;
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
		flyway.setTarget("0.1.5");
		flyway.setInitOnMigrate(true);

		flyway.clean();

		flyway.migrate();
	}
}
