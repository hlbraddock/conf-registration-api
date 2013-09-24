package org.cru.crs.utils;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: ryancarlson
 * Date: 9/23/13
 * Time: 4:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class IdComparerTest
{
    @Test(groups="unittest")
    public void testMatchingIds()
    {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.fromString(id1.toString());

        Assert.assertFalse(IdComparer.idsAreNotNullAndDifferent(id1, id2));
    }

    @Test(groups="unittest")
    public void testNonMatchingIds()
    {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        Assert.assertTrue(IdComparer.idsAreNotNullAndDifferent(id1, id2));
    }

    @Test(groups="unittest")
    public void testOneNullIdAgainstOneNonNullId()
    {
        UUID id1 = null;
        UUID id2 = UUID.randomUUID();

        Assert.assertFalse(IdComparer.idsAreNotNullAndDifferent(id1, id2));
    }

    @Test(groups="unittest")
    public void testBothNullIds()
    {
        UUID id1 = null;
        UUID id2 = null;

        Assert.assertFalse(IdComparer.idsAreNotNullAndDifferent(id1, id2));
    }


}
