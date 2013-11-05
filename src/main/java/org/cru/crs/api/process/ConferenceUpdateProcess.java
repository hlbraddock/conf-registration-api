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
import org.cru.crs.service.ConferenceCostsService;
import org.cru.crs.service.ConferenceService;
import org.cru.crs.service.PageService;
import org.cru.crs.service.UserService;
import org.cru.crs.utils.CollectionUtils;
import org.testng.collections.Lists;
import org.testng.collections.Maps;
import org.testng.internal.annotations.Sets;

public class ConferenceUpdateProcess
{
	ConferenceService conferenceService;
	ConferenceCostsService conferenceCostsService;
	PageService pageService;
	BlockService blockService;
	AnswerService answerService;
	UserService userService;
	
	ConferenceEntity originalConferenceEntity;
	List<PageEntity> originalPageEntityList;
	Map<UUID,List<BlockEntity>> originalBlockEntityMap;
	Map<UUID,List<AnswerEntity>> originalAnswerEntityMap;
	
	public ConferenceUpdateProcess(ConferenceService conferenceService, ConferenceCostsService conferenceCostsService, 
								PageService pageService, BlockService blockService, 
								AnswerService answerService, UserService userService)
	{
		this.conferenceService = conferenceService;
		this.conferenceCostsService = conferenceCostsService;
		this.pageService = pageService;
		this.blockService = blockService;
		this.answerService = answerService;
		this.userService = userService;
	}

	/**
	 * Performs a "deep" update of a web model conference object.  The basic algorithm is as follows:
	 *  - load up original Conference, Pages, Blocks and Answers from the DB and store them in memory.  they are used for comparison purposes with the updated
	 *    conference to see what was moved, added or taken away
	 *  - identify any pages that were removed from the conference by comparing updated to original, delete them (and all corresponding blocks and answers)
	 *  - loop through updated pages, adding if it's new, updating if it's not
	 *  - while on each page, if it's existing see if any of its blocks are not anywhere on the conference. if any are missing delete them (and answers)
	 *  - while still on the same page, update its existing blocks (this update might actually move a block from another page, this is ok! (hibernate can't do this... hehehehe) )
	 *    and add any new blocks
	 *  - go to the next page and repeat.
	 *  - when done with pages, update the conference and conference costs fields themselves.
	 * @param conference
	 */
	public void performDeepUpdate(Conference conference)
	{
		originalConferenceEntity = conferenceService.fetchConferenceBy(conference.getId());
		originalPageEntityList = getPageEntityListFromDb(conference);
		originalBlockEntityMap = getBlockEntityMapFromDb(conference);
		originalAnswerEntityMap = getAnswerEntityMapFromDb(conference);
		
		handleMissingPages(conference);
		
		int pagePosition = 0;
		for(Page page : conference.getRegistrationPages())
		{
			page.setConferenceId(originalConferenceEntity.getId());
			page.setPosition(pagePosition);
			
			if(!maybeAddNewPage(page))
			{
				handleMissingBlocks(conference, page);
				pageService.updatePage(page.toDbPageEntity());
			}
			
			int blockPosition = 0;
			if(page.getBlocks() != null)
			{
				for(Block block : page.getBlocks())
				{
					block.setPageId(page.getId());
					block.setPosition(blockPosition);
					
					if(!maybeAddNewBlock(block))
					{
						blockService.updateBlock(block.toDbBlockEntity());
					}
					blockPosition++;
				}

			}
			pagePosition++;
		}
		
		conferenceCostsService.update(conference.toDbConferenceCostsEntity());
		conferenceService.updateConference(conference.toDbConferenceEntity());
	}

	private boolean maybeAddNewPage(Page page)
	{
		if(page.getId() == null) page.setId(UUID.randomUUID());
		
		Set<PageEntity> possiblyNewPages = Sets.newHashSet();
		possiblyNewPages.add(page.toDbPageEntity());
		Collection<PageEntity> definitelyNewPages = CollectionUtils.firstNotFoundInSecond(possiblyNewPages, Lists.newArrayList(originalPageEntityList));
		
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
						
			Collection<BlockEntity> definitelyNewBlocks = CollectionUtils.firstNotFoundInSecond(possiblyNewBlocks, getOriginalMasterBlockSet());
			
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
		
		Collection<PageEntity> deletedPageEntities = CollectionUtils.firstNotFoundInSecond(Lists.newArrayList(originalPageEntityList), Lists.newArrayList(updatedPageEntityList));
				
		for(PageEntity pageToDelete : deletedPageEntities)
		{
			pageService.deletePage(pageToDelete.getId());
		}
	}
	
	private void handleMissingBlocks(Conference conference, Page page)
	{
		Collection<BlockEntity> deletedBlockEntites = CollectionUtils.firstNotFoundInSecond(Lists.newArrayList(originalBlockEntityMap.get(page.getId())), Lists.newArrayList(getUpdatedMasterBlockSet(conference)));
		
		for(BlockEntity blockToDelete : deletedBlockEntites)
		{
			blockService.deleteBlock(blockToDelete.getId());
		}
	}

	private Set<BlockEntity> getOriginalMasterBlockSet()
	{
		/*create a master set so we can detect if a block has moved from one page to another*/
		Set<BlockEntity> masterBlockSet = Sets.newHashSet();
		for(PageEntity existingPage : originalPageEntityList)
		{
			masterBlockSet.addAll(originalBlockEntityMap.get(existingPage.getId()));
		}
		return masterBlockSet;
	}
	
	private Set<BlockEntity> getUpdatedMasterBlockSet(Conference updatedConference)
	{
		Set<BlockEntity> masterBlockSet = Sets.newHashSet();
		for(Page updatedPage : updatedConference.getRegistrationPages())
		{
			masterBlockSet.addAll(convertWebBlocksToBlockEntities(updatedPage.getBlocks()));
		}
		return masterBlockSet;
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
