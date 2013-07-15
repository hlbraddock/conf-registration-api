package org.cru.crs.api.model;

import java.util.ArrayList;
import java.util.List;

import org.cru.crs.model.PageEntity;

public class Page implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;

	public static Page fromJpa(PageEntity jpaPage)
	{
		return new Page();
	}
	
	public static List<Page> fromJpa(List<PageEntity> jpaPages)
	{
		return new ArrayList<Page>();
	}
}
