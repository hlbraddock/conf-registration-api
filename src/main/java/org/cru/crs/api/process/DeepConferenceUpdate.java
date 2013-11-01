package org.cru.crs.api.process;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.cru.crs.api.model.Block;
import org.cru.crs.api.model.Conference;
import org.cru.crs.api.model.Page;
import org.cru.crs.model.AnswerEntity;
import org.cru.crs.model.BlockEntity;
import org.cru.crs.model.ConferenceEntity;
import org.cru.crs.model.PageEntity;
import org.cru.crs.service.AnswerService;
import org.cru.crs.service.BlockService;
import org.cru.crs.service.ConferenceService;
import org.cru.crs.service.PageService;
import org.cru.crs.utils.CollectionUtils;
import org.testng.collections.Lists;
import org.testng.collections.Maps;
import org.testng.internal.annotations.Sets;

public class DeepConferenceUpdate
{
	ConferenceService conferenceService;
	PageService pageService;
	BlockService blockService;
	AnswerService answerService;
	
	ConferenceEntity originalConferenceEntity;
	List<PageEntity> originalPageEntityList;
	Map<UUID,List<BlockEntity>> originalBlockEntityMap;
	Map<UUID,List<AnswerEntity>> originalAnswerEntityMap;
	
	public DeepConferenceUpdate(ConferenceService conferenceService,PageService pageService, BlockService blockService, AnswerService answerService)
	{
		this.conferenceService = conferenceService;
		this.pageService = pageService;
		this.blockService = blockService;
		this.answerService = answerService;
	}

	public void performDeepUpdate(Conference conference)
	{
		originalConferenceEntity = conferenceService.fetchConferenceBy(conference.getId());
		originalPageEntityList = getPageEntityListFromDb(conference);
		originalBlockEntityMap = getBlockEntityMapFromDb(conference);
		originalAnswerEntityMap = getAnswerEntityMapFromDb(conference);
		
		handleMissingPages(conference);
		
		for(Page page : conference.getRegistrationPages())
		{
			page.setConferenceId(originalConferenceEntity.getId());
			
			if(!maybeAddNewPage(page))
			{
				handleMissingBlocks(page);
				pageService.updatePage(page.toDbPageEntity());
			}
			
			if(page.getBlocks() != null)
			{
				for(Block block : page.getBlocks())
				{
					block.setPageId(page.getId());

					if(!maybeAddNewBlock(block))
					{
						blockService.updateBlock(block.toDbBlockEntity());
					}
				}
			}
		}
		
		conferenceService.updateConference(conference.toJpaConferenceEntity());
	}

	private boolean maybeAddNewPage(Page page)
	{
		if(page.getId() == null) page.setId(UUID.randomUUID());
		
		Set<PageEntity> possiblyNewPages = Sets.newHashSet();
		possiblyNewPages.add(page.toDbPageEntity());
		Collection<PageEntity> definitelyNewPages = CollectionUtils.firstNotFoundInSecond(possiblyNewPages, originalPageEntityList);
		
		for(PageEntity newPage : definitelyNewPages)
		{
			pageService.savePage(newPage);
			return true;
		}
		return false;
	}
	

	private boolean maybeAddNewBlock(Block block)
	{
		if(block.getId() == null) block.setId(UUID.randomUUID());
		
		if(originalBlockEntityMap.get(block.getPageId()) != null)
		{
			Set<BlockEntity> possiblyNewBlocks = Sets.newHashSet();
			possiblyNewBlocks.add(block.toDbBlockEntity());
			Collection<BlockEntity> definitelyNewBlocks = CollectionUtils.firstNotFoundInSecond(possiblyNewBlocks, originalBlockEntityMap.get(block.getPageId()));
			
			for(BlockEntity newBlock : definitelyNewBlocks)
			{
				blockService.saveBlock(newBlock);
				return true;
			}
		}
		else
		{
			blockService.saveBlock(block.toDbBlockEntity());
			return true;
		}
		return false;
	}

	private void handleMissingPages(Conference conference)
	{
		List<PageEntity> updatedPageEntityList = convertWebPagesToPageEntities(conference.getRegistrationPages());
		
		Collection<PageEntity> deletedPageEntities = CollectionUtils.firstNotFoundInSecond(originalPageEntityList, updatedPageEntityList);
				
		for(PageEntity pageToDelete : deletedPageEntities)
		{
			pageService.deletePage(pageToDelete.getId());
		}
	}
	
	private void handleMissingBlocks(Page page)
	{
		List<BlockEntity> updatedBlockEntityList = convertWebBlocksToBlockEntities(page.getBlocks());
		
		Collection<BlockEntity> deletedBlockEntites = CollectionUtils.firstNotFoundInSecond(originalBlockEntityMap.get(page.getId()), updatedBlockEntityList);
		
		for(BlockEntity blockToDelete : deletedBlockEntites)
		{
			blockService.deleteBlock(blockToDelete.getId());
		}
	}

	private List<PageEntity> getPageEntityListFromDb(Conference conference)
	{
		return pageService.fetchPagesForConference(conference.getId());
	}
	
	private List<PageEntity> convertWebPagesToPageEntities(List<Page> webPages)
	{
		List<PageEntity> pageEntities = Lists.newArrayList();
		
		for(Page webPage : webPages)
		{
			pageEntities.add(webPage.toDbPageEntity());			
		}
		
		return pageEntities;
	}
	
	private List<BlockEntity> convertWebBlocksToBlockEntities(List<Block> blocks)
	{
		List<BlockEntity> blockEntites = Lists.newArrayList();
		
		for(Block webBlock : blocks)
		{
			blockEntites.add(webBlock.toDbBlockEntity());
		}
		
		return blockEntites;
	}

	private Map<UUID,List<BlockEntity>> getBlockEntityMapFromDb(Conference conference)
	{
		Map<UUID,List<BlockEntity>> blockMap = Maps.newHashMap();
		
		for(Page page : conference.getRegistrationPages())
		{
			if(page.getBlocks() == null) continue;
			blockMap.put(page.getId(), blockService.fetchBlocksForPage(page.getId()));
		}
		
		return blockMap;
	}
	
	private Map<UUID,List<AnswerEntity>> getAnswerEntityMapFromDb(Conference conference)
	{
		Map<UUID,List<AnswerEntity>> answerMap = Maps.newHashMap();
		
		for(Page page : conference.getRegistrationPages())
		{
			if(page.getBlocks() == null) continue;
			for(Block block : page.getBlocks())
			{
				answerMap.put(page.getId(), answerService.getAllAnswersForBlock(block.getId()));
			}
		}
		
		return answerMap;
	}
	
}
