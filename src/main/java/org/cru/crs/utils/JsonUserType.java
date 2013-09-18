package org.cru.crs.utils;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;

import com.google.common.base.Throwables;

public class JsonUserType implements UserType
{

	@Override
	public Object assemble(Serializable cached, Object owner) throws HibernateException
	{
		return deepCopy(cached);
	}

	@Override
	public Object deepCopy(Object value) throws HibernateException
	{
		if(value instanceof String)
		{
			String jsonString = (String)value;
			ObjectMapper mapper = new ObjectMapper();
			JsonFactory factory = mapper.getJsonFactory(); // since 2.1 use mapper.getFactory() instead

			try
			{
				JsonParser parser = factory.createJsonParser(jsonString);
				return mapper.readTree(parser);
			}
			catch(Throwable t)
			{
				Throwables.propagate(t);
				return null;
			}
		}
		else return value;
	}

	@Override
	public Serializable disassemble(Object value) throws HibernateException
	{
		return (String)deepCopy(value).toString();
	}

	@Override
	public boolean equals(Object arg0, Object arg1) throws HibernateException
	{
		if(arg0 == null)
		{
			return arg1 == null;
		}
		
		return arg0.equals(arg1);
	}

	@Override
	public int hashCode(Object arg0) throws HibernateException
	{
		return arg0.hashCode();
	}

	@Override
	public boolean isMutable()
	{
		return true;
	}

	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException,SQLException
	{
        if(rs.getString(names[0]) == null)
		{
            return null;
		}
		else
		{
			ObjectMapper mapper = new ObjectMapper();
			JsonFactory factory = mapper.getJsonFactory(); // since 2.1 use mapper.getFactory() instead

			try
			{
				JsonParser parser = factory.createJsonParser(rs.getString(names[0]));
				return mapper.readTree(parser);
			}
			catch(Throwable t)
			{
				Throwables.propagate(t);
				return null;
			}
		}
	}

	@Override
	public void nullSafeSet(PreparedStatement statement, Object value, int index, SessionImplementor session) throws HibernateException, SQLException
	{
		if(value == null)
		{
			statement.setNull(index, Types.OTHER);
		}
		else
		{
			statement.setObject(index,  value, Types.OTHER);
		}
	}

	@Override
	public Object replace(Object original, Object target, Object ownder) throws HibernateException
	{
		return original;
	}

	@Override
	public Class<?> returnedClass()
	{
		return JsonNode.class;
	}

	@Override
	public int[] sqlTypes()
	{
		return new int[] {Types.JAVA_OBJECT};
	}

}
