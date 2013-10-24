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
}
