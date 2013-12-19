package org.cru.crs.model.queries;

public class BlockQueries implements BasicQueries
{

	@Override
	public String selectById()
	{
		return "SELECT * FROM blocks WHERE id = :id";
	}

	public String selectAllForPage()
	{
		return "SELECT * FROM blocks WHERE page_id = :pageId";
	}
	
	@Override
	public String update()
	{
		return "UPDATE blocks SET " +
				 "page_id = :pageId, " +
				 "position = :position, " +
				 "block_type = :blockType, " +
				 "admin_only = :adminOnly, " +
				 "required = :required, " +
				 "title = :title, " +
				 "content = :content, " +
				 "profile_type = :profileType" +
				 " WHERE " +
				 "id = :id";
	}

	@Override
	public String insert()
	{
		return "INSERT INTO blocks(id, page_id, position, block_type, admin_only, required, title, content, profile_type) " +
				"VALUES (:id, :pageId, :position, :blockType, :adminOnly, :required, :title, :content, :profileType)";
	}

	@Override
	public String delete()
	{
		return "DELETE FROM blocks WHERE id = :id";
	}

}
