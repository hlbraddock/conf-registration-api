package org.cru.crs.api.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.codehaus.jackson.JsonNode;
import org.cru.crs.model.BlockEntity;

public class Block implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;
	
	private UUID id;
	private UUID pageId;
	private String title;
	private String type;
	private JsonNode content;
	
	public static Block fromJpa(BlockEntity jpaBlock)
	{
		Block block = new Block();
		
		block.id = jpaBlock.getId();
		block.title = jpaBlock.getTitle();
		block.type = jpaBlock.getBlockType();
		block.content = jpaBlock.getContent();
		block.pageId = jpaBlock.getPageId();
		
		return block;
	}
	
	public static List<Block> fromJpa(List<BlockEntity> jpaBlocks)
	{
		List<Block> webBlocks = new ArrayList<Block>();
		
		if(jpaBlocks == null) return webBlocks;
		
		for(BlockEntity jpaBlock : jpaBlocks)
		{
			webBlocks.add(fromJpa(jpaBlock));
		}
		
		return webBlocks;
	}
	
	public BlockEntity toJpaBlockEntity()
	{
		BlockEntity jpaBlock = new BlockEntity();
		
		jpaBlock.setId(id);
		jpaBlock.setPageId(pageId);
		jpaBlock.setBlockType(type);
		jpaBlock.setContent(content);
		jpaBlock.setTitle(title);
		
		return jpaBlock;
	}
	
	public UUID getId()
	{
		return id;
	}
	public Block setId(UUID id)
	{
		this.id = id;
		return this;
	}
	public String getTitle()
	{
		return title;
	}
	public void setTitle(String title)
	{
		this.title = title;
	}
	public String getType()
	{
		return type;
	}
	public void setType(String type)
	{
		this.type = type;
	}
	public JsonNode getContent()
	{
		return content;
	}
	public void setContent(JsonNode content)
	{
		this.content = content;
	}

	public UUID getPageId()
	{
		return pageId;
	}

	public void setPageId(UUID pageId)
	{
		this.pageId = pageId;
	}
}
