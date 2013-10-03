package org.cru.crs.api.utils;

import org.ccci.util.time.Clock;
import org.cru.crs.api.model.Conference;
import org.cru.crs.auth.AuthenticationProviderType;
import org.cru.crs.auth.model.CrsApplicationUser;
import org.cru.crs.model.ConferenceEntity;
import org.cru.crs.service.AnswerService;
import org.cru.crs.service.ConferenceService;
import org.cru.crs.service.UserService;
import org.cru.crs.utils.DateTimeCreaterHelper;
import org.joda.time.DateTime;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.UUID;

public class RegistrationWindowCalculatorTest
{
    private static final String PERSISTENCE_UNIT_NAME = "crsUnitTestPersistence";

    private Conference testConferenceWithEarlyRegistration;
    private Conference testConferenceWithoutEarlyRegistration;

    class TestClock extends Clock
    {
        private DateTime currentDateTime;

        public TestClock setDateAtFourPM(int month, int day, int year)
        {
            currentDateTime = new DateTime().withYear(year)
                                            .withMonthOfYear(month)
                                            .withDayOfMonth(day)
                                            .withHourOfDay(16)
                                            .withMinuteOfHour(0)
                                            .withSecondOfMinute(0)
                                            .withMillisOfSecond(0);

            return this;

        }

        public TestClock setDateAtNinePM(int month, int day, int year)
        {
            currentDateTime = new DateTime().withYear(year)
                    .withMonthOfYear(month)
                    .withDayOfMonth(day)
                    .withHourOfDay(21)
                    .withMinuteOfHour(0)
                    .withSecondOfMinute(0)
                    .withMillisOfSecond(0);

            return this;

        }

        @Override
        public DateTime currentDateTime()
        {
            return currentDateTime;
        }
    }

    @BeforeMethod(groups="unittest")
    public void setup()
    {
        testConferenceWithEarlyRegistration = new Conference();
        testConferenceWithEarlyRegistration.setEarlyRegistrationDiscount(true);
        testConferenceWithEarlyRegistration.setEarlyRegistrationCutoff(DateTimeCreaterHelper.createDateTime(2013,03,31,23,59,59));
        testConferenceWithEarlyRegistration.setRegistrationStartTime(DateTimeCreaterHelper.createDateTime(2013,3,17,12,29,14));
        testConferenceWithEarlyRegistration.setRegistrationEndTime(DateTimeCreaterHelper.createDateTime(2013, 5, 13, 8, 9, 37));

        testConferenceWithoutEarlyRegistration = new Conference();
        testConferenceWithoutEarlyRegistration.setEarlyRegistrationDiscount(false);
        testConferenceWithoutEarlyRegistration.setEarlyRegistrationCutoff(null);
        testConferenceWithoutEarlyRegistration.setRegistrationStartTime(DateTimeCreaterHelper.createDateTime(2013,4,10,21,58,35));
        testConferenceWithoutEarlyRegistration.setRegistrationEndTime(DateTimeCreaterHelper.createDateTime(2013,5,22,18,53,8));
    }

    @Test(groups="unittest")
    public void testRegistrationOpenNoEarlyRegistration()
    {
        RegistrationWindowCalculator.setEarlyRegistrationOpenFieldOn(testConferenceWithoutEarlyRegistration, new TestClock().setDateAtFourPM(4,17,2013));
        RegistrationWindowCalculator.setRegistrationOpenFieldOn(testConferenceWithoutEarlyRegistration, new TestClock().setDateAtFourPM(4, 17, 2013));

        Assert.assertFalse(testConferenceWithoutEarlyRegistration.isEarlyRegistrationOpen());
        Assert.assertTrue(testConferenceWithoutEarlyRegistration.isRegistrationOpen());
    }

    @Test(groups="unittest")
    public void testRegistrationClosedNoEarlyRegistration()
    {
        RegistrationWindowCalculator.setEarlyRegistrationOpenFieldOn(testConferenceWithoutEarlyRegistration, new TestClock().setDateAtFourPM(5,27,2013));
        RegistrationWindowCalculator.setRegistrationOpenFieldOn(testConferenceWithoutEarlyRegistration, new TestClock().setDateAtFourPM(5, 27, 2013));

        Assert.assertFalse(testConferenceWithoutEarlyRegistration.isEarlyRegistrationOpen());
        Assert.assertFalse(testConferenceWithoutEarlyRegistration.isRegistrationOpen());
    }

    @Test(groups="unittest")
    public void testRegistrationClosedSameDayNoEarlyRegistration()
    {
        RegistrationWindowCalculator.setEarlyRegistrationOpenFieldOn(testConferenceWithoutEarlyRegistration, new TestClock().setDateAtNinePM(5, 22, 2013));
        RegistrationWindowCalculator.setRegistrationOpenFieldOn(testConferenceWithoutEarlyRegistration, new TestClock().setDateAtNinePM(5, 22, 2013));

        Assert.assertFalse(testConferenceWithoutEarlyRegistration.isEarlyRegistrationOpen());
        Assert.assertFalse(testConferenceWithoutEarlyRegistration.isRegistrationOpen());
    }

    @Test(groups="unittest")
    public void testRegistrationOpenEarlyRegistrationOpen()
    {
        RegistrationWindowCalculator.setEarlyRegistrationOpenFieldOn(testConferenceWithEarlyRegistration, new TestClock().setDateAtFourPM(3, 28, 2013));
        RegistrationWindowCalculator.setRegistrationOpenFieldOn(testConferenceWithEarlyRegistration, new TestClock().setDateAtFourPM(3, 28, 2013));

        Assert.assertTrue(testConferenceWithEarlyRegistration.isEarlyRegistrationOpen());
        Assert.assertTrue(testConferenceWithEarlyRegistration.isRegistrationOpen());
    }

    @Test(groups="unittest")
    public void testRegistrationOpenEarlyRegistrationClosed()
    {
        RegistrationWindowCalculator.setEarlyRegistrationOpenFieldOn(testConferenceWithEarlyRegistration, new TestClock().setDateAtFourPM(4, 1, 2013));
        RegistrationWindowCalculator.setRegistrationOpenFieldOn(testConferenceWithEarlyRegistration, new TestClock().setDateAtFourPM(4, 1, 2013));

        Assert.assertFalse(testConferenceWithEarlyRegistration.isEarlyRegistrationOpen());
        Assert.assertTrue(testConferenceWithEarlyRegistration.isRegistrationOpen());
    }

    /**
     * This test specifically tests a date before the window opens.
     */
    @Test(groups="unittest")
    public void testRegistrationNotStartedEarlyRegistrationNotStarted()
    {
        RegistrationWindowCalculator.setEarlyRegistrationOpenFieldOn(testConferenceWithEarlyRegistration, new TestClock().setDateAtFourPM(1, 1, 2013));
        RegistrationWindowCalculator.setRegistrationOpenFieldOn(testConferenceWithEarlyRegistration, new TestClock().setDateAtFourPM(1, 1, 2013));

        Assert.assertFalse(testConferenceWithEarlyRegistration.isEarlyRegistrationOpen());
        Assert.assertFalse(testConferenceWithEarlyRegistration.isRegistrationOpen());
    }
}
