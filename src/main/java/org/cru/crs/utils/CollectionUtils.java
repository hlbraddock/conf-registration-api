package org.cru.crs.utils;

import java.util.Collection;

public class CollectionUtils
{
	/*
	 * remove from first all those in second (thereby leaving in first those not found in second)
	 */
	public static <T> Collection<T> firstNotFoundInSecond(Collection<T> first, Collection<T> second)
	{
		first.removeAll(second);

		return first;
	}

	public static <T> T getAnyOne(Collection<T> elements)
	{
		for(T t : elements)
			return t;

		return null;
	}
}
