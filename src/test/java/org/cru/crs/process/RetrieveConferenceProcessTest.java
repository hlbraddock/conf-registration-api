package org.cru.crs.process;

import org.cru.crs.AbstractTestWithDatabaseConnectivity;
import org.cru.crs.api.model.Block;
import org.cru.crs.api.model.Conference;
import org.cru.crs.api.model.Page;
import org.cru.crs.api.process.RetrieveConferenceProcess;
import org.cru.crs.service.AnswerService;
import org.cru.crs.service.BlockService;
import org.cru.crs.service.ConferenceCostsService;
import org.cru.crs.service.ConferenceService;
import org.cru.crs.service.PageService;
import org.cru.crs.service.PermissionService;
import org.cru.crs.service.RegistrationService;
import org.cru.crs.service.UserService;
import org.cru.crs.utils.ClockImpl;
import org.cru.crs.utils.ConferenceInfo;
import org.cru.crs.utils.DateTimeCreaterHelper;
import org.cru.crs.utils.ServiceFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class RetrieveConferenceProcessTest extends AbstractTestWithDatabaseConnectivity
{
	RetrieveConferenceProcess retrieveConferenceProcess;

	@BeforeMethod(alwaysRun=true)
	public void setup()
	{
		refreshConnection();

		BlockService blockService = new BlockService(sqlConnection, new AnswerService(sqlConnection));
		PageService pageService = new PageService(sqlConnection, blockService);
		ConferenceCostsService conferenceCostsService = new ConferenceCostsService(sqlConnection);
		PermissionService permissionService = new PermissionService(sqlConnection);
		RegistrationService	registrationService = ServiceFactory.createRegistrationService(sqlConnection);

		retrieveConferenceProcess = new RetrieveConferenceProcess(new ConferenceService(sqlConnection, conferenceCostsService, pageService, new UserService(sqlConnection), permissionService), 
																conferenceCostsService, 
																pageService, 
																blockService,
																registrationService,
																new ClockImpl());
	}
	
	@Test(groups="dbtest")
	public void testRetrieveNorthernMichiganConference()
	{
		Conference northernMichiganConference = retrieveConferenceProcess.get(ConferenceInfo.Id.NorthernMichigan);
		
		Assert.assertNotNull(northernMichiganConference);
		
		/*first check the size of related entities*/
		Assert.assertEquals(northernMichiganConference.getRegistrationPages().size(), 3);
		Assert.assertEquals(northernMichiganConference.getRegistrationPages().get(0).getBlocks().size(), 4);
		Assert.assertEquals(northernMichiganConference.getRegistrationPages().get(1).getBlocks().size(), 4);
		Assert.assertEquals(northernMichiganConference.getRegistrationPages().get(2).getBlocks().size(), 3);
		Assert.assertEquals(northernMichiganConference.getRegistrationCount().intValue(), 2);
		Assert.assertEquals(northernMichiganConference.getCompletedRegistrationCount().intValue(), 0);

		verifyConferenceFields(northernMichiganConference);
		
		verifyConferenceCostFields(northernMichiganConference);
				
		verifyPages(northernMichiganConference);
		
		verifyBlocks(northernMichiganConference);
	}

	private void verifyConferenceFields(Conference northernMichiganConference)
	{
		Assert.assertEquals(northernMichiganConference.getId(), ConferenceInfo.Id.NorthernMichigan);
		Assert.assertEquals(northernMichiganConference.getName(), "Northern Michigan Fall Extravaganza");
		Assert.assertNull(northernMichiganConference.getDescription());
		Assert.assertEquals(northernMichiganConference.getEventStartTime(), DateTimeCreaterHelper.createDateTime(2014, 8, 29, 22, 30, 0));
		Assert.assertEquals(northernMichiganConference.getEventEndTime(), DateTimeCreaterHelper.createDateTime(2014, 8, 31, 16, 0, 0));
		Assert.assertEquals(northernMichiganConference.getRegistrationStartTime(), DateTimeCreaterHelper.createDateTime(2013, 4, 11, 1, 58, 35));
		Assert.assertEquals(northernMichiganConference.getRegistrationEndTime(), DateTimeCreaterHelper.createDateTime(2014, 8, 29, 21, 00, 0));
		Assert.assertTrue(northernMichiganConference.isRegistrationOpen());
		Assert.assertNull(northernMichiganConference.getContactPersonName());
		Assert.assertNull(northernMichiganConference.getContactPersonEmail());
		Assert.assertNull(northernMichiganConference.getContactPersonPhone());
		Assert.assertEquals(northernMichiganConference.getTotalSlots(), 80);
		Assert.assertEquals(northernMichiganConference.getLocationName(), "Black Bear Camp");
		Assert.assertEquals(northernMichiganConference.getLocationAddress(), "5287 St Rt 17");
		Assert.assertEquals(northernMichiganConference.getLocationCity(), "Marquette");
		Assert.assertEquals(northernMichiganConference.getLocationState(), "MI");
		Assert.assertEquals(northernMichiganConference.getLocationZipCode(), "42302");
		Assert.assertFalse(northernMichiganConference.isRequireLogin());
	}
	
	private void verifyConferenceCostFields(Conference northernMichiganConference)
	{
		Assert.assertEquals(northernMichiganConference.getConferenceCost(), new BigDecimal("50.00"));
		Assert.assertEquals(northernMichiganConference.getMinimumDeposit(), new BigDecimal("10.00"));
		Assert.assertFalse(northernMichiganConference.isEarlyRegistrationDiscount());
		Assert.assertNull(northernMichiganConference.getEarlyRegistrationAmount());
		Assert.assertNull(northernMichiganConference.getEarlyRegistrationCutoff());
		Assert.assertFalse(northernMichiganConference.isEarlyRegistrationOpen());
		Assert.assertTrue(northernMichiganConference.isAcceptCreditCards());
		Assert.assertNull(northernMichiganConference.getAuthnetId());
		Assert.assertNull(northernMichiganConference.getAuthnetToken());
	}
	
//	private void verifyPermissions(Conference northernMichiganConference)
//	{
//		for(Permission permission : northernMichiganConference.getPermissions())
//		{
//			if(permission.getId().equals(UUID.fromString("dcb85040-76e2-11e3-981f-0800200c9a66")))
//			{
//				Assert.assertEquals(permission.getConferenceId(), ConferenceInfo.Id.NorthernMichigan);
//				Assert.assertEquals(permission.getUserId(), UserInfo.Id.Ryan);
//				Assert.assertEquals(permission.getPermissionLevel(), PermissionLevel.UPDATE);
//				Assert.assertEquals(permission.getGivenByUserId(), UserInfo.Id.TestUser);
//				Assert.assertEquals(permission.getTimestamp(), DateTimeCreaterHelper.createDateTime(2013, 8, 14, 15, 27, 49));
//			}
//			else if(permission.getId().equals(UUID.fromString("2230e3d0-76e3-11e3-981f-0800200c9a66")))
//			{
//				Assert.assertEquals(permission.getConferenceId(), ConferenceInfo.Id.NorthernMichigan);
//				Assert.assertEquals(permission.getUserId(), UserInfo.Id.Email);
//				Assert.assertEquals(permission.getPermissionLevel(), PermissionLevel.VIEW);
//				Assert.assertEquals(permission.getGivenByUserId(), UserInfo.Id.TestUser);
//				Assert.assertEquals(permission.getTimestamp(), DateTimeCreaterHelper.createDateTime(2013, 8, 14, 15, 27, 50));
//			}
//		}
//	}
	
	private void verifyPages(Conference northernMichiganConference)
	{
		Page firstPage = northernMichiganConference.getRegistrationPages().get(0);
		Page secondPage = northernMichiganConference.getRegistrationPages().get(1);
		Page thirdPage = northernMichiganConference.getRegistrationPages().get(2);
		
		Assert.assertEquals(firstPage.getId(), UUID.fromString("7A52AF36-2F3C-5E45-9F76-0AF10FF50BB8"));
		Assert.assertEquals(firstPage.getConferenceId(), ConferenceInfo.Id.NorthernMichigan);
		Assert.assertEquals(firstPage.getPosition(), 0);
		Assert.assertEquals(firstPage.getTitle(), "About you");
		Assert.assertEquals(firstPage.getBlocks().size(), 4);
		
		Assert.assertEquals(secondPage.getId(), UUID.fromString("0A00D62C-AF29-3723-F949-95A950A0B27C"));
		Assert.assertEquals(secondPage.getConferenceId(), ConferenceInfo.Id.NorthernMichigan);
		Assert.assertEquals(secondPage.getPosition(), 1);
		Assert.assertEquals(secondPage.getTitle(), "About your cat");
		Assert.assertEquals(secondPage.getBlocks().size(), 4);
		
		Assert.assertEquals(thirdPage.getId(), UUID.fromString("7DAE078F-A131-471E-BB70-5156B62DDEA5"));
		Assert.assertEquals(thirdPage.getConferenceId(), ConferenceInfo.Id.NorthernMichigan);
		Assert.assertEquals(thirdPage.getPosition(), 2);
		Assert.assertEquals(thirdPage.getTitle(), "Hobbies and activities");
		Assert.assertEquals(thirdPage.getBlocks().size(), 3);
	}
	
	public void verifyBlocks(Conference northernMichiganConference)
	{
		List<Block> firstPageBlocks = northernMichiganConference.getRegistrationPages().get(0).getBlocks();
		List<Block> secondPageBlocks = northernMichiganConference.getRegistrationPages().get(1).getBlocks();
		List<Block> thirdPageBlocks = northernMichiganConference.getRegistrationPages().get(2).getBlocks();
		
		Assert.assertEquals(firstPageBlocks.get(0).getId(), UUID.fromString("AF60D878-4741-4F21-9D25-231DB86E43EE"));
		Assert.assertEquals(firstPageBlocks.get(0).getTitle(), "About the conference");
		Assert.assertEquals(firstPageBlocks.get(0).getType(), "paragraphContent");
		Assert.assertFalse(firstPageBlocks.get(0).isRequired());
		Assert.assertEquals(firstPageBlocks.get(0).getPosition(), 0);
		Assert.assertEquals(firstPageBlocks.get(0).getContent().textValue(), "This is a paragraph of text describing this conference.");
		
		Assert.assertEquals(firstPageBlocks.get(1).getId(), UUID.fromString("DDA45720-DE87-C419-933A-018712B152D2"));
		Assert.assertEquals(firstPageBlocks.get(1).getTitle(), "Your name");
		Assert.assertEquals(firstPageBlocks.get(1).getType(), "nameQuestion");
		Assert.assertTrue(firstPageBlocks.get(1).isRequired());
		Assert.assertEquals(firstPageBlocks.get(1).getPosition(), 1);
		Assert.assertEquals(firstPageBlocks.get(1).getContent().textValue(), "");
		
		Assert.assertEquals(firstPageBlocks.get(2).getId(), UUID.fromString("F774EA5C-8E44-25DC-9169-2F141C57E3AE"));
		Assert.assertEquals(firstPageBlocks.get(2).getTitle(), "Email address");
		Assert.assertEquals(firstPageBlocks.get(2).getType(), "emailQuestion");
		Assert.assertTrue(firstPageBlocks.get(2).isRequired());
		Assert.assertEquals(firstPageBlocks.get(2).getPosition(), 2);
		Assert.assertEquals(firstPageBlocks.get(2).getContent().textValue(), "");
		
		Assert.assertEquals(firstPageBlocks.get(3).getId(), UUID.fromString("A229C854-6989-F658-7C29-B3DD034F6FD1"));
		Assert.assertEquals(firstPageBlocks.get(3).getTitle(), "Year in school");
		Assert.assertEquals(firstPageBlocks.get(3).getType(), "radioQuestion");
		Assert.assertTrue(firstPageBlocks.get(3).isRequired());
		Assert.assertEquals(firstPageBlocks.get(3).getPosition(), 3);
		Assert.assertEquals(firstPageBlocks.get(3).getContent().toString(), "{\"choices\":[\"Freshman\",\"Sophomore\",\"Junior\",\"Senior\",\"Super Senior\",\"Grad Student\",\"Do not plan on graduating\"]}");
		
		Assert.assertEquals(secondPageBlocks.get(0).getId(), UUID.fromString("AF60D878-4741-4F21-9D25-231DB86E43EC"));
		Assert.assertEquals(secondPageBlocks.get(0).getTitle(), "We love pets");
		Assert.assertEquals(secondPageBlocks.get(0).getType(), "paragraphContent");
		Assert.assertFalse(secondPageBlocks.get(0).isRequired());
		Assert.assertEquals(secondPageBlocks.get(0).getPosition(), 0);
		Assert.assertEquals(secondPageBlocks.get(0).getContent().textValue(), "Please tell us all about your kitteh.");
		
		Assert.assertEquals(secondPageBlocks.get(1).getId(), UUID.fromString("DDA45720-DE87-C419-933A-018712B152DC"));
		Assert.assertEquals(secondPageBlocks.get(1).getTitle(), "Kittehs name");
		Assert.assertEquals(secondPageBlocks.get(1).getType(), "nameQuestion");
		Assert.assertTrue(secondPageBlocks.get(1).isRequired());
		Assert.assertEquals(secondPageBlocks.get(1).getPosition(), 1);
		Assert.assertEquals(secondPageBlocks.get(1).getContent().textValue(), "");
		
		Assert.assertEquals(secondPageBlocks.get(2).getId(), UUID.fromString("F774EA5C-8E44-25DC-9169-2F141C57E3AC"));
		Assert.assertEquals(secondPageBlocks.get(2).getTitle(), "Mah kitteh iz... (check all that apply)");
		Assert.assertEquals(secondPageBlocks.get(2).getType(), "checkboxQuestion");
		Assert.assertFalse(secondPageBlocks.get(2).isRequired());
		Assert.assertEquals(secondPageBlocks.get(2).getPosition(), 2);
		Assert.assertEquals(secondPageBlocks.get(2).getContent().toString(), "{\"choices\":[\"Tabby\",\"Tuxedo\",\"Fat\",\"Hunter/huntress\",\"Lethargic\",\"Calico\",\"Aloof\",\"Curious\",\"Swimmer\",\"Eight of nine lives spent\"]}");
		
		Assert.assertEquals(secondPageBlocks.get(3).getId(), UUID.fromString("A229C854-6989-F658-7C29-B3DD034F6FDC"));
		Assert.assertEquals(secondPageBlocks.get(3).getTitle(), "Preferred cat food brand (or cheezburgerz)");
		Assert.assertEquals(secondPageBlocks.get(3).getType(), "textQuestion");
		Assert.assertFalse(secondPageBlocks.get(3).isRequired());
		Assert.assertEquals(secondPageBlocks.get(3).getPosition(), 3);
		Assert.assertEquals(secondPageBlocks.get(3).getContent().textValue(), "");
		
		Assert.assertEquals(thirdPageBlocks.get(0).getId(), UUID.fromString("AF60D878-4741-4F21-9D25-231DB86E43EB"));
		Assert.assertEquals(thirdPageBlocks.get(0).getTitle(), "Favorite TV show");
		Assert.assertEquals(thirdPageBlocks.get(0).getType(), "textQuestion");
		Assert.assertFalse(thirdPageBlocks.get(0).isRequired());
		Assert.assertEquals(thirdPageBlocks.get(0).getPosition(), 0);
		Assert.assertEquals(thirdPageBlocks.get(0).getContent().textValue(), "");
		
		Assert.assertEquals(thirdPageBlocks.get(1).getId(), UUID.fromString("F774EA5C-8E44-25DC-9169-2F141C57E3AB"));
		Assert.assertEquals(thirdPageBlocks.get(1).getTitle(), "Sessions I will attend... (check all that apply)");
		Assert.assertEquals(thirdPageBlocks.get(1).getType(), "checkboxQuestion");
		Assert.assertFalse(thirdPageBlocks.get(1).isRequired());
		Assert.assertEquals(thirdPageBlocks.get(1).getPosition(), 1);
		Assert.assertEquals(thirdPageBlocks.get(1).getContent().toString(), "{\"choices\":[\"Mens time\",\"Womens time\",\"Wilderness survival\",\"Sword drills 101\",\"What about my cat?\"]}");
		
		Assert.assertEquals(thirdPageBlocks.get(2).getId(), UUID.fromString("A229C854-6989-F658-7C29-B3DD034F6FDB"));
		Assert.assertEquals(thirdPageBlocks.get(2).getTitle(), "Favorite sport");
		Assert.assertEquals(thirdPageBlocks.get(2).getType(), "textQuestion");
		Assert.assertFalse(thirdPageBlocks.get(2).isRequired());
		Assert.assertEquals(thirdPageBlocks.get(2).getPosition(), 2);
		Assert.assertEquals(thirdPageBlocks.get(2).getContent().textValue(), "");
	}
	
}
