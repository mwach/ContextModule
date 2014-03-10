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
G       Era designator  Text    AD
y       Year    Year    1996; 96
Y       Week year       Year    2009; 09
M       Month in year   Month   July; Jul; 07
w       Week in year    Number  27
W       Week in month   Number  2
D       Day in year     Number  189
d       Day in month    Number  10
F       Day of week in month    Number  2
E       Day name in week        Text    Tuesday; Tue
u       Day number of week (1 = Monday, ..., 7 = Sunday)        Number  1
a       Am/pm marker    Text    PM
H       Hour in day (0-23)      Number  0
k       Hour in day (1-24)      Number  24
K       Hour in am/pm (0-11)    Number  0
h       Hour in am/pm (1-12)    Number  12
m       Minute in hour  Number  30
s       Second in minute        Number  55
S       Millisecond     Number  978
z       Time zone       General time zone       Pacific Standard Time; PST; GMT-08:00
Z       Time zone       RFC 822 time zone       -0800
X       Time zone       ISO 8601 time zone      -08; -0800; -08:00

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
