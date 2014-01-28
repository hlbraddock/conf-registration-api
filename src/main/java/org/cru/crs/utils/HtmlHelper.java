package org.cru.crs.utils;

public class HtmlHelper
{
	public static String BR = "<br>";

	public static String WS = "&nbsp;";

	public static String paragraph(String text)
	{
		return wrap(text, "p");
	}

	public static String bold(String text)
	{
		return wrap(text, "b");
	}

	public static String underline(String text)
	{
		return wrap(text, "u");
	}

	public static String wrap(String text, String tag)
	{
		return "<" + tag + ">" + text + "</" + tag + ">";
	}

	public static String href(String url, String text)
	{
		return "<a href=\"" + url + "\">" + text + "</a>";
	}
}
