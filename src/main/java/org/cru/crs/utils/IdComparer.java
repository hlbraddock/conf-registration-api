package org.cru.crs.utils;

import java.util.UUID;

/**
 * This class encapsulates the logic where two UUIDs are compared for equality.
 * 
 * Specifically, if the two IDs are both not-null, and have different values, then
 * they are considered different.  If one is null, then they are not considered
 * different.
 * @author ryancarlson
 *
 */
public class IdComparer
{
	/**
	 * If the two IDs are both not-null, and have different values, then
	 * they are considered different, return true.
	 * 
	 * If either one (or both) is null, then they are not considered different. return false.
	 * 
	 * If both have the same non-null value, of course return false.
	 */
	public static boolean idsAreNotNullAndDifferent(UUID id1, UUID id2)
	{
		return id1 != null && id2 != null && !id1.equals(id2);
	}
}
