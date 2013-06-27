package org.cru.crs.utils;
import java.io.Serializable;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.UUID;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;

/*
 * Custom UserType implemented to store java array (ie; String[]) types
 */
public class CustomArrayType implements UserType
{
	@Override
	public int[] sqlTypes()
	{
		return new int[] {Types.VARCHAR};
	}

	@Override
	public Class<UUID[]> returnedClass()
	{
		return UUID[].class;
		
	}

	@Override
	public Object nullSafeGet(ResultSet resultSet, String[] names, SessionImplementor arg2, Object arg3) throws HibernateException,SQLException
	{
		Array result = null;
		Array array = (Array) resultSet.getArray(names[0]);
		if (!resultSet.wasNull())
		{
			result = array;
		}
		return result;
	}

	@Override
	public void nullSafeSet(PreparedStatement statement, Object value, int index, SessionImplementor arg3) throws HibernateException, SQLException
	{
		if (value == null)
		{
			statement.setNull(index, 0);
		}
		else
		{
			statement.setArray(index, (Array)value);
		}
		
	}

	public Object deepCopy(Object value) throws HibernateException
	{
		return value;
	}

	public boolean isMutable()
	{
		return false;
	}

	@Override
	public Object assemble(Serializable arg0, Object arg1) throws HibernateException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Serializable disassemble(Object arg0) throws HibernateException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean equals(Object arg0, Object arg1) throws HibernateException
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int hashCode(Object arg0) throws HibernateException
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object replace(Object arg0, Object arg1, Object arg2) throws HibernateException
	{
		// TODO Auto-generated method stub
		return null;
	}

}