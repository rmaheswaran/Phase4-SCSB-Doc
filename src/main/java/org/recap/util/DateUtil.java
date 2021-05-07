package org.recap.util;

import org.recap.ScsbConstants;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by angelind on 17/5/17.
 */
@Service
public class DateUtil {

    /**
     * Gets from date from the parameter and formats by setting start time of the day.
     *
     * @param createdDate the created date
     * @return the from date
     */
    public Date getFromDate(Date createdDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(createdDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return  cal.getTime();
    }

    /**
     *
     * @param createdDate
     * @return EST time formate date
     */
    public Date getFromDateAccession(Date createdDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(createdDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.setTimeZone(TimeZone.getTimeZone(ScsbConstants.EST_TIMEZONE));
        return  cal.getTime();
    }
    /**
     * Gets to date from the parameter and formats by setting end time of the day.
     *
     * @param createdDate the created date
     * @return the to date
     */
    public Date getToDate(Date createdDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(createdDate);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }

    /**
     *
     * @param createdDate
     * @return EST time formate date
     */
    public Date getToDateAccession(Date createdDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(createdDate);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.setTimeZone(TimeZone.getTimeZone(ScsbConstants.EST_TIMEZONE));
        return cal.getTime();
    }
}
