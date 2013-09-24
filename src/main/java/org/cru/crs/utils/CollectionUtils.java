package org.cru.crs.utils;

import java.util.Set;

public class CollectionUtils
{
	/*
	 * remove from first all those in second (thereby leaving in first those not found in second)
	 */
	public static <T> Set<T> firstNotFoundInSecond(Set<T> first, Set<T> second)
	{
		first.removeAll(second);

		return first;
	}

	public static <T> T getAnyOne(Set<T> elements)
	{
		for(T t : elements)
			return t;

		return null;
	}
}
