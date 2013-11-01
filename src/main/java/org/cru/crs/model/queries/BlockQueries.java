package org.cru.crs.model.queries;

public class BlockQueries implements BasicQueries
{

	@Override
	public String selectById()
	{
		StringBuilder query = new StringBuilder();
		query.append("SELECT * FROM blocks WHERE id = :id");
		
		return query.toString();
	}

	public String selectAllForPage()
	{
		StringBuilder query = new StringBuilder();
		query.append("SELECT * FROM blocks WHERE page_id = :pageId");
		
		return query.toString();
	}
	
	@Override
	public String update()
	{
		StringBuilder query = new StringBuilder();
		query.append("UPDATE blocks SET ")
				.append("page_id = :pageId,")
				.append("position = :position,")
				.append("block_type = :blockType,")
				.append("admin_only = :adminOnly,")
				.append("required = :required,")
				.append("title = :title,")
				.append("content = :content")
				.append(" WHERE " )
				.append("id = :id");
		
		return query.toString();
	}

	@Override
	public String insert()
	{
		StringBuilder query = new StringBuilder();
		query.append("INSERT INTO blocks(")
				.append("id,")
				.append("page_id,")
				.append("position,")
				.append("block_type,")
				.append("admin_only,")
				.append("required,")
				.append("title,")
				.append("content")
				.append(") VALUES (")
				.append(":id,")
				.append(":pageId,")
				.append(":position,")
				.append(":blockType,")
				.append(":adminOnly,")
				.append(":required,")
				.append(":title,")
				.append(":content")
				.append(")");
		
		return query.toString();
		
		
	}

	@Override
	public String delete()
	{
		StringBuilder query = new StringBuilder();
		query.append("DELETE FROM blocks WHERE id = :id");
		
		return query.toString();
	}

}
