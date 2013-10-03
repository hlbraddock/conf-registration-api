package org.cru.crs.api.utils;

import com.google.common.base.Preconditions;
import org.ccci.util.time.Clock;
import org.cru.crs.api.model.Conference;
import org.joda.time.DateTime;

public class RegistrationWindowCalculator
{
    public static void setRegistrationOpenFieldOn(Conference conference, Clock clock)
    {
        if(conference.getRegistrationStartTime() == null || conference.getRegistrationEndTime() == null)
        {
            conference.setRegistrationOpen(false);
            return;
        }
        conference.setRegistrationOpen(calculateIfRegistrationIsOpen(conference.getRegistrationStartTime(), conference.getRegistrationEndTime(), clock.currentDateTime()));
    }

    public static void setEarlyRegistrationOpenFieldOn(Conference conference, Clock clock)
    {
        if(conference.isEarlyRegistrationDiscount())
        {
            if(conference.getRegistrationStartTime() == null || conference.getRegistrationEndTime() == null || conference.getEarlyRegistrationCutoff() == null)
            {
                conference.setEarlyRegistrationOpen(false);
                return;
            }

            DateTime thisMomentInTime = clock.currentDateTime();

            conference.setEarlyRegistrationOpen(calculateIfRegistrationIsOpen(conference.getRegistrationStartTime(), conference.getRegistrationEndTime(), thisMomentInTime) &&
                                                      thisMomentInTime.isBefore(conference.getEarlyRegistrationCutoff()));
        }
        else
        {
            conference.setEarlyRegistrationOpen(false); //this should be defaulted to 'false', but just to make sure...
        }
    }

    private static boolean calculateIfRegistrationIsOpen(DateTime registrationOpeningTime, DateTime registrationClosingTime, DateTime thisMomentInTime)
    {
        return thisMomentInTime.isAfter(registrationOpeningTime) && thisMomentInTime.isBefore(registrationClosingTime);
    }

}
