package org.cru.crs.payment.authnet;

import java.io.IOException;
import java.util.Map;

public interface HttpProvider
{
	public String getContentFromPost(String url, Map<String, String> parameters) throws IOException;

}
