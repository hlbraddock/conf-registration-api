package org.cru.crs.service;

import org.cru.crs.api.model.Block;
import org.cru.crs.auth.AuthenticationProviderType;
import org.cru.crs.auth.UnauthorizedException;
import org.cru.crs.auth.model.CrsApplicationUser;
import org.cru.crs.model.AnswerEntity;
import org.cru.crs.model.BlockEntity;
import org.cru.crs.model.PageEntity;
import org.cru.crs.model.RegistrationEntity;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.UUID;

@Test(groups="db-integration-tests")
public class BlockServiceTest
{
	private static final String PERSISTENCE_UNIT_NAME = "crsUnitTestPersistence";
	private EntityManagerFactory emFactory;
	private EntityManager em;

	private BlockService blockService;

	private CrsApplicationUser testAppUser = new CrsApplicationUser(UUID.fromString("dbc6a808-d7bc-4d92-967c-d82d9d312898"), AuthenticationProviderType.RELAY, "crs.testuser@crue.org");
	private CrsApplicationUser testAppUserNotAuthorized = new CrsApplicationUser(UUID.randomUUID(), null, null);
	
	@BeforeClass
	public void setup()
	{
		emFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		em = emFactory.createEntityManager();

		blockService = new BlockService(em, new ConferenceService(em, new UserService(em)), new PageService(em, new ConferenceService(em, new UserService(em))));
	}

	@AfterClass
	public void cleanup()
	{
		em.close();
		emFactory.close();
	}

	@Test(groups="db-integration-tests")
	public void fetchBlockById()
	{
		BlockEntity block = blockService.fetchBlockBy(UUID.fromString("af60d878-4741-4f21-9d25-231db86e43ee"));

		Assert.assertNotNull(block);
		Assert.assertEquals(block.getId(), UUID.fromString("af60d878-4741-4f21-9d25-231db86e43ee"));
		Assert.assertEquals(block.getPosition(), 0);
		Assert.assertEquals(block.getPageId(), UUID.fromString("7a52af36-2f3c-5e45-9f76-0af10ff50bb8"));
		Assert.assertFalse(block.isAdminOnly());
		Assert.assertEquals(block.getBlockType(), "paragraphContent");
		Assert.assertEquals(block.getTitle(), "About the conference");
        Assert.assertNotNull(block.getContent());
	}

	@Test(groups="db-integration-tests")
	public void testUpdateBlock() throws UnauthorizedException
	{
		BlockEntity block = blockService.fetchBlockBy(UUID.fromString("af60d878-4741-4f21-9d25-231db86e43ee"));

		Assert.assertEquals(block.getTitle(), "About the conference");

		Block webBlock = Block.fromJpa(block);
		webBlock.setTitle("Kittys name");

		try
		{
			em.getTransaction().begin();
			blockService.updateBlock(webBlock.toJpaBlockEntity(), testAppUser);
			em.flush();
			em.getTransaction().commit();
		}
		catch(Exception e)
		{
			em.getTransaction().rollback();
			Assert.fail("failed updating the block", e);
		}
		
		BlockEntity updatedBlock = em.find(BlockEntity.class, UUID.fromString("af60d878-4741-4f21-9d25-231db86e43ee"));

		Assert.assertEquals(updatedBlock.getId(), UUID.fromString("af60d878-4741-4f21-9d25-231db86e43ee"));
		Assert.assertEquals(updatedBlock.getTitle(), "Kittys name");

		updatedBlock.setTitle("About the conference");
	}
	
	public void testUpdatePageNotAuthorized() throws UnauthorizedException
	{
		BlockEntity block = blockService.fetchBlockBy(UUID.fromString("af60d878-4741-4f21-9d25-231db86e43ee"));

		Assert.assertEquals(block.getTitle(), "About the conference");

		Block webBlock = Block.fromJpa(block);
		webBlock.setTitle("Fun stuff");

		try
		{
			em.getTransaction().begin();
			blockService.updateBlock(webBlock.toJpaBlockEntity(), testAppUserNotAuthorized);
			Assert.fail("Should have thrown an UnauthorizedException");
		}
		catch(UnauthorizedException e)
		{
			/*do nothing*/
		}
		finally
		{
			em.getTransaction().rollback();
		}
		
	}

	@Test(groups="db-integration-tests")
	public void testDeleteBlock() throws UnauthorizedException
	{
		EntityManager setupEm = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME).createEntityManager();

		BlockEntity block = new BlockEntity();

		block.setId(UUID.randomUUID());
		block.setTitle("New Page");
		block.setPageId(UUID.fromString("7a52af36-2f3c-5e45-9f76-0af10ff50bb8"));

		setupEm.getTransaction().begin();

		PageEntity pageToAddBlockTo = setupEm.find(PageEntity.class, UUID.fromString("7a52af36-2f3c-5e45-9f76-0af10ff50bb8"));

		pageToAddBlockTo.getBlocks().add(block);

		setupEm.flush();
		setupEm.getTransaction().commit();

		Assert.assertNotNull(em.find(BlockEntity.class, block.getId()));

		em.getTransaction().begin();

		blockService.deleteBlock(block.getId(), testAppUser);

		em.flush();
		em.getTransaction().commit();

		EntityManager cleanupEm = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME).createEntityManager();
		Assert.assertNull(cleanupEm.find(BlockEntity.class, block.getId()));
	}

	@Test(groups="db-integration-tests")
	public void testDeleteBlockNotAuthorized() throws UnauthorizedException
	{
		try
		{
			em.getTransaction().begin();
			blockService.deleteBlock(UUID.fromString("af60d878-4741-4f21-9d25-231db86e43ee"), testAppUserNotAuthorized);
			Assert.fail("Should have thrown an UnauthorizedException");
		}
		catch(UnauthorizedException e)
		{
			/*do nothing*/
		}
		finally
		{
			em.getTransaction().rollback();
		}
	}

    @Test(groups="db-integration-tests")
    public void testDeleteBlocksWithAssociatedAnswers() throws UnauthorizedException
    {
        EntityManager setupEm = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME).createEntityManager();


        BlockEntity block = new BlockEntity();

        block.setId(UUID.randomUUID());
        block.setTitle("New Block");
        block.setPageId(UUID.fromString("7a52af36-2f3c-5e45-9f76-0af10ff50bb8"));

        AnswerEntity answer = new AnswerEntity();
        answer.setId(UUID.randomUUID());
        answer.setBlockId(block.getId());
        answer.setRegistrationId(UUID.fromString("a2bff4a8-c7dc-4c0a-bb9e-67e6dcb982e7"));

        try
        {
            RegistrationEntity registration = setupEm.find(RegistrationEntity.class, UUID.fromString("a2bff4a8-c7dc-4c0a-bb9e-67e6dcb982e7"));

            setupEm.getTransaction().begin();

            PageEntity pageToAddBlockTo = setupEm.find(PageEntity.class, UUID.fromString("7a52af36-2f3c-5e45-9f76-0af10ff50bb8"));

            pageToAddBlockTo.getBlocks().add(block);

            setupEm.flush();

            registration.getAnswers().add(answer);

            setupEm.flush();

            setupEm.getTransaction().commit();

            BlockEntity retrievedBlock = em.find(BlockEntity.class, block.getId());

            em.getTransaction().begin();
            em.remove(retrievedBlock);

            em.flush();

            em.getTransaction().commit();

            Assert.assertNull(em.find(BlockEntity.class, block.getId()));
            Assert.assertNull(em.find(AnswerEntity.class, answer.getId()));
        }
        catch(Exception e)
        {
            setupEm.getTransaction().rollback();
            setupEm.close();
            em.getTransaction().rollback();
            Assert.fail();
        }
    }
}