package org.cru.crs.api.process;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import org.ccci.util.time.Clock;
import org.cru.crs.api.model.Conference;
import org.cru.crs.api.utils.RegistrationWindowCalculator;
import org.cru.crs.model.BlockEntity;
import org.cru.crs.model.ConferenceCostsEntity;
import org.cru.crs.model.ConferenceEntity;
import org.cru.crs.model.PageEntity;
import org.cru.crs.service.BlockService;
import org.cru.crs.service.ConferenceCostsService;
import org.cru.crs.service.ConferenceService;
import org.cru.crs.service.PageService;
import org.testng.collections.Maps;

public class ConferenceFetchProcess
{
	ConferenceService conferenceService;
	ConferenceCostsService conferenceCostsService;
	PageService pageService;
	BlockService blockService;
	Clock clock;
	
	@Inject
	public ConferenceFetchProcess(ConferenceService conferenceService,
			ConferenceCostsService conferenceCostsService,
			PageService pageService, BlockService blockService, Clock clock)
	{
		this.conferenceService = conferenceService;
		this.conferenceCostsService = conferenceCostsService;
		this.pageService = pageService;
		this.blockService = blockService;
		this.clock = clock;
	}

	/**
	 * Takes in a conference ID and the following services and returns a fully built out conference
	 */
	public Conference get(UUID conferenceId)
	{
		ConferenceEntity databaseConference = conferenceService.fetchConferenceBy(conferenceId);
		ConferenceCostsEntity databaseConferenceCosts = conferenceCostsService.fetchBy(conferenceId);
		List<PageEntity> databasePages = orderPagesByPosition(pageService.fetchPagesForConference(conferenceId));
		Map<UUID,List<BlockEntity>> databaseBlocks = Maps.newHashMap();
		
		for(PageEntity page : databasePages)
		{
			databaseBlocks.put(page.getId(), orderBlocksByPosition(blockService.fetchBlocksForPage(page.getId())));
		}
		
		Conference apiConference =  Conference.fromDb(databaseConference, databaseConferenceCosts, databasePages, databaseBlocks);
		
		RegistrationWindowCalculator.setRegistrationOpenFieldOn(apiConference, clock);
        RegistrationWindowCalculator.setEarlyRegistrationOpenFieldOn(apiConference, clock);

        return apiConference;
	}
	
	private List<PageEntity> orderPagesByPosition(List<PageEntity> pages)
	{
		if(pages == null) return pages;
		
		Collections.sort(pages,new Comparator<PageEntity>(){

			@Override
			public int compare(PageEntity page1, PageEntity page2)
			{
				return new Integer(page1.getPosition()).compareTo(new Integer(page2.getPosition()));
			}});
		
		return pages;
	}
	
	private List<BlockEntity> orderBlocksByPosition(List<BlockEntity> blocks)
	{
		if(blocks == null) return blocks;
		
		Collections.sort(blocks,new Comparator<BlockEntity>(){

			@Override
			public int compare(BlockEntity block1, BlockEntity block2)
			{
				return new Integer(block1.getPosition()).compareTo(new Integer(block2.getPosition()));
			}});
		
		return blocks;
	}
}