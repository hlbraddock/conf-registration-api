package org.cru.crs.service;

import java.util.List;

import org.sql2o.Sql2o;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SqlBasedConferenceServiceTest
{

	@Test
	public void testBasicQuery()
	{
		Sql2o sql = new Sql2o("jdbc:postgresql://localhost/crsdb", "crsuser", "crsuser");

		List<SqlConference> conferences = sql.createQuery("SELECT name, description FROM conferences", false).executeAndFetch(SqlConference.class);
		
		Assert.assertTrue(conferences.size() >= 10);
	}
	
	public static class SqlConference
	{
		String name;
		String description;
		
		public SqlConference(){}
	}
}
