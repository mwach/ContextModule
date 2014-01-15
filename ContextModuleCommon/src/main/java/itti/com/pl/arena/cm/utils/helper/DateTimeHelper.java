package itti.com.pl.arena.cm.utils.helper;

import itti.com.pl.arena.cm.ErrorMessages;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Date-time utilities
 * 
 * @author cm-admin
 * 
 */
public final class DateTimeHelper {

    public static final int SECOND = 1;
    public static final int MINUTE = 2;
    public static final int HOUR = 3;

    private DateTimeHelper() {
    }

    /**
     * Format timestamp into string using provided format
     * 
     * @param timestamp
     *            timestamp
     * @param timestampFormat
     *            format of the string date
     * @return formatted timestamp
     * @throws DateTimeHelperException
     *             could not format timestamp into string
     */
    public static String formatTime(long timestamp, String timestampFormat) throws DateTimeHelperException {

        // format not specified
        if (!StringHelper.hasContent(timestampFormat)) {
            throw new DateTimeHelperException(ErrorMessages.DATE_TIME_HELPER_EMPTY_TIMESTAMP);
        }

        // try to parse date into string
        String dateString = null;
        try {
            // create formatter, then use it to parse timestamp
            SimpleDateFormat sdf = new SimpleDateFormat(timestampFormat);
            dateString = sdf.format(new Date(timestamp));
        } catch (RuntimeException exc) {
            // could not convert timestamp into string
            throw new DateTimeHelperException(ErrorMessages.DATE_TIME_HELPER_PARSER_FAILURE, timestamp, timestampFormat,
                    exc.getLocalizedMessage());
        }
        // returns formatted date
        return dateString;
    }

    /**
     * Returns a delta between two timestamps (end - start) measured in defined time unit. If no unit (or invalid
     * value) will be specified, delta measured in milliseconds will be returned
     * 
     * @param timeEnd
     *            start timestamp
     * @param timeStart
     *            end timestamp
     * @param unit
     *            measure unit (can be second, minute or hour, check class constants for details)
     * @return delta between two timestamps
     */
    public static long delta(long timeEnd, long timeStart, int unit) {
        long delta = timeEnd - timeStart;
        switch (unit) {
        case SECOND:
            return delta / 1000;
        case MINUTE:
            return delta / 1000 / 60;
        case HOUR:
            return delta / 1000 / 60 / 60;
        default:
            return delta;
        }
    }
}
