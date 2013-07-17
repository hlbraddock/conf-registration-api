package org.cru.crs.api.optimizer;

import org.cru.crs.model.ConferenceEntity;

public class ConferenceOptimizer
{
	/**
	 * Due to JPA/hibernates's lameness.. it either has to eagerly fetch all the pages or it fails.
	 * Sometimes we don't want to return them though so strip them out.
	 * 
	 * If only I could just write the query for what I want w/o worrying about JPA magic....
	 * @return
	 */
	public static ConferenceEntity removePagesFromConference(ConferenceEntity conference)
	{
        conference.setPages(null);

		return conference;
	}
}
