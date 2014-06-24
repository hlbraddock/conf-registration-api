package org.cru.crs.utils;

import org.ccci.util.time.Clock;

public class ClockFactory
{
	public static Clock getInstance()
	{
		return new ClockImpl();
	}
}
