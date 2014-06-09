package org.cru.crs.migration;

import com.google.common.base.Strings;
import com.googlecode.flyway.core.Flyway;
import org.cru.crs.utils.CrsProperties;
import org.cru.crs.utils.CrsPropertiesFactory;

/**
 * Created by ryancarlson on 6/6/14.
 */
public class DatabaseMigration
{

	public static void main(String[] args)
	{
		if(!shouldExecute(args)) return;

		String prefix = getPrefix(args);

		Flyway flyway = new Flyway();
		CrsProperties properties = new CrsPropertiesFactory().get();

		flyway.setDataSource(properties.getProperty(prefix + "databaseUrl"),
				properties.getProperty(prefix + "databaseUsername"),
				properties.getProperty(prefix + "databasePassword"));

		flyway.clean();
		flyway.setInitOnMigrate(true);
		flyway.migrate();
	}

	private static boolean shouldExecute(String[] args)
	{
		return args.length >= 2 && Boolean.parseBoolean(args[0]);
	}

	private static String getPrefix(String[] args)
	{
		if(Strings.isNullOrEmpty(args[0]))
		{
			return "";
		}
		else
		{
			return args[1] + "_";
		}
	}
}
