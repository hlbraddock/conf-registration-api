package org.cru.crs;

import com.googlecode.flyway.core.Flyway;
import com.googlecode.flyway.core.api.MigrationVersion;
import org.cru.crs.utils.CrsProperties;

/**
 * Created by ryancarlson on 4/1/14.
 */
public class UnittestDatabaseBuilder
{

	static public void build(CrsProperties properties)
	{
		Flyway flyway = new Flyway();
		flyway.setDataSource(properties.getProperty("unittestDatabaseUrl"), properties.getProperty("unittestDatabaseUsername"), properties.getProperty("unittestDatabasePassword"));
		flyway.setInitVersion("0");
		flyway.setTarget(MigrationVersion.fromVersion("0.2"));
		flyway.clean();
		flyway.init();
		flyway.migrate();
	}
}
