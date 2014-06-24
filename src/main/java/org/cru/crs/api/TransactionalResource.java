package org.cru.crs.api;

import com.google.common.base.Throwables;
import org.sql2o.Connection;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import java.lang.reflect.InvocationTargetException;

@RequestScoped
public class TransactionalResource
{
	@Inject Connection sqlConnection;

	@AroundInvoke
	public Object transactionInterceptor(InvocationContext ctx) throws Exception
	{
		sqlConnection.getJdbcConnection().setAutoCommit(false);
		Object returnedObjectFromMethod = null;
		
		try
		{
			returnedObjectFromMethod = ctx.getMethod().invoke(this, ctx.getParameters());
			sqlConnection.commit();
		}
		catch(Exception e)
		{
			sqlConnection.rollback();
			
			if(e instanceof InvocationTargetException) Throwables.propagate(unwrapInvocationTargetException((InvocationTargetException)e));
			
			else throw e;
		}

		return returnedObjectFromMethod;
	}
	
	private Throwable unwrapInvocationTargetException(InvocationTargetException invocationTargetException)
	{
		return invocationTargetException.getTargetException();
	}
}