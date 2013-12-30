package org.cru.crs.utils;

import org.ccci.util.time.Clock;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * User: lee.braddock
 */
public class ClockImpl extends Clock
{
	@Override
	public DateTime currentDateTime()
	{
		return new DateTime(DateTimeZone.UTC);
	}
}
