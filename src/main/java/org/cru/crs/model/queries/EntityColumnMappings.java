package org.cru.crs.model.queries;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.cru.crs.utils.StringUtils;

public class EntityColumnMappings
{
	public static Map<String,String> get(Class<?> entityClazz)
	{
		Map<String, String> columnMappings = new HashMap<String,String>();
		
		for(Field f : entityClazz.getDeclaredFields())
		{
			if("serialVersionUID".equalsIgnoreCase(f.getName())) continue;
			columnMappings.put(StringUtils.camelCaseToUnderscore(f.getName()),f.getName());
		}
		
		return columnMappings;
	}
}
