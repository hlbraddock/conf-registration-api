package org.cru.crs.utils;

import com.google.common.base.Strings;

public class StringUtils
{
    public static String camelCaseToUnderscore(String camelCase)
    {
        if(Strings.isNullOrEmpty(camelCase)) return camelCase;
        
        StringBuffer spaces = new StringBuffer();

        for(int i=0; i<camelCase.length(); i++)
        {
            char c = camelCase.charAt(i);
            if(Character.isUpperCase(c)||Character.isDigit(c)) spaces.append("_");
            spaces.append(c);
        }
        return spaces.toString().toLowerCase();
    }

	public static String repeat(String string, int num)
	{
		return new String(new char[num]).replace("\0", string);
	}

	public static String substringBeforeLast(String string, String separator)
	{
		if (Strings.isNullOrEmpty(string) || Strings.isNullOrEmpty(separator))
			return string;

		int lastIndexOf = string.lastIndexOf(separator);

		if (lastIndexOf == -1)
			return string;

		return string.substring(0, lastIndexOf);
	}
}
