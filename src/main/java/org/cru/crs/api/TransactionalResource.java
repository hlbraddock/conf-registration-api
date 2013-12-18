package org.cru.crs.api;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.sql2o.Connection;

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
		}

		return returnedObjectFromMethod;
	}
}