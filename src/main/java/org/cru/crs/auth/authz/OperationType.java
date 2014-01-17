package org.cru.crs.auth.authz;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum OperationType
{
	CREATE("create"),
	READ("read"),
	UPDATE("update"),
	DELETE("delete"),
	ADMIN("admin");

	OperationType(String operationType)
	{
		this.operationType = operationType;
	}

	final String operationType;

	public static final Set<OperationType> CUD =
			new HashSet<OperationType>(Arrays.asList(OperationType.CREATE, OperationType.UPDATE, OperationType.DELETE));
}
