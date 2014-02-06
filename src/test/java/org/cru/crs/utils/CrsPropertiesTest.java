package org.cru.crs.utils;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created with IntelliJ IDEA.
 * User: ryancarlson
 * Date: 9/23/13
 * Time: 4:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class CrsPropertiesTest
{

    @Test(groups="unittest")
    public void testGetDefaultProperty()
    {
      CrsProperties props = new CrsPropertiesFactory().get();

      Assert.assertEquals(props.get("clientUrl"), "http://localhost:9000/#");
    }

}
