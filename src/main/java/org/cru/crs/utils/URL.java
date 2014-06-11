package org.cru.crs.utils;

import com.google.common.base.Strings;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.net.URLStreamHandlerFactory;

public class URL
{
	private java.net.URL url;

	public URL(String url) throws MalformedURLException
	{
		this.url = new java.net.URL(url);
	}

	public URL(java.net.URL url) throws MalformedURLException
	{
		this.url = url;
	}

	private static final String protocolSuffix = "://";
	
	public URL withProtocol(String protocol) throws MalformedURLException
	{
		if(Strings.isNullOrEmpty(protocol))
		{
			return this;
		}

		return new URL(protocol + protocolSuffix + url.getHost() + portPrefix() + portString() + url.getPath());
	}

	public URL withPort(String port) throws MalformedURLException
	{
		if(Strings.isNullOrEmpty(port))
		{
			return this;
		}

		URL tempUrl = new URL("http://server:" + port);

		return new URL(url.getProtocol() + protocolSuffix + url.getHost() + tempUrl.portPrefix() + tempUrl.portString() + url.getPath());
	}

	public URL withHost(String host) throws MalformedURLException
	{
		if(Strings.isNullOrEmpty(host))
		{
			return this;
		}

		return new URL(url.getProtocol() + protocolSuffix + host + portPrefix() + portString() + url.getPath());
	}

	private String portPrefix()
	{
		return url.getPort() == -1 ? "" : ":";
	}

	private String portString()
	{
		return url.getPort() == -1 ? "" : "" + url.getPort();
	}

	// delegation

	public String getQuery()
	{
		return url.getQuery();
	}

	public String getRef()
	{
		return url.getRef();
	}

	public boolean sameFile(java.net.URL other)
	{
		return url.sameFile(other);
	}

	public InputStream openStream() throws IOException
	{
		return url.openStream();
	}

	public int getPort()
	{
		return url.getPort();
	}

	public URI toURI() throws URISyntaxException
	{
		return url.toURI();
	}

	public String getUserInfo()
	{
		return url.getUserInfo();
	}

	public static void setURLStreamHandlerFactory(URLStreamHandlerFactory fac)
	{
		java.net.URL.setURLStreamHandlerFactory(fac);
	}

	public String toExternalForm()
	{
		return url.toExternalForm();
	}

	public int getDefaultPort()
	{
		return url.getDefaultPort();
	}

	public String getPath()
	{
		return url.getPath();
	}

	public String getAuthority()
	{
		return url.getAuthority();
	}

	public Object getContent() throws IOException
	{
		return url.getContent();
	}

	public URLConnection openConnection(Proxy proxy) throws IOException
	{
		return url.openConnection(proxy);
	}

	public String getProtocol()
	{
		return url.getProtocol();
	}

	public String getFile()
	{
		return url.getFile();
	}

	public URLConnection openConnection() throws IOException
	{
		return url.openConnection();
	}

	public String getHost()
	{
		return url.getHost();
	}

	public Object getContent(Class[] classes) throws IOException
	{
		return url.getContent(classes);
	}
}
