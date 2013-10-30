package org.cru.crs.api.model.utils;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.cru.crs.api.model.Conference;
import org.cru.crs.model.BlockEntity;
import org.cru.crs.model.ConferenceCostsEntity;
import org.cru.crs.model.ConferenceEntity;
import org.cru.crs.model.PageEntity;
import org.cru.crs.service.BlockService;
import org.cru.crs.service.ConferenceCostsService;
import org.cru.crs.service.ConferenceService;
import org.cru.crs.service.PageService;
import org.testng.collections.Maps;

public class ConferenceAssembler
{

	/**
	 * Takes in a conference ID and the following services and returns a fully built out conference
	 */
	public static Conference buildConference(UUID conferenceId, ConferenceService conferenceService, ConferenceCostsService conferenceCostsService, PageService pageService, BlockService blockService)
	{
		ConferenceEntity databaseConference = conferenceService.fetchConferenceBy(conferenceId);
		ConferenceCostsEntity databaseConferenceCosts = conferenceCostsService.fetchBy(conferenceId);
		List<PageEntity> databasePages = pageService.fetchPagesForConference(conferenceId);
		Map<UUID,List<BlockEntity>> databaseBlocks = Maps.newHashMap();
		
		for(PageEntity page : databasePages)
		{
			databaseBlocks.put(page.getId(), blockService.fetchBlocksForPage(page.getId()));
		}
		
		return Conference.fromDb(databaseConference, databaseConferenceCosts, databasePages, databaseBlocks);
	}
}
