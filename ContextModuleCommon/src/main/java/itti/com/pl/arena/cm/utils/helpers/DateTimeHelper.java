package itti.com.pl.arena.cm.utils.helpers;

import itti.com.pl.arena.cm.ErrorMessages;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Date-time utilities
 * @author cm-admin
 *
 */
public final class DateTimeHelper {

	private DateTimeHelper()
	{
	}

	/**
	 * Format timestamp into string using provided format
	 * @param timestamp timestamp
	 * @param timestampFormat format of the string date
	 * @return formatted timestamp
	 * @throws DateTimeHelperException could not format timestamp into string
	 */
	public static String formatTime(long timestamp, String timestampFormat) throws DateTimeHelperException {

		// format not specified
		if(!StringHelper.hasContent(timestampFormat))
		{
			throw new DateTimeHelperException(ErrorMessages.DATE_TIME_HELPER_EMPTY_TIMESTAMP);
		}

		//try to parse date into string
		String dateString = null;
		try{
			//create formatter, then use it to parse timestamp
			SimpleDateFormat sdf = new SimpleDateFormat(timestampFormat);
			dateString = sdf.format(new Date(timestamp));
		}catch(RuntimeException exc){
			//could not convert timestamp into string
			throw new DateTimeHelperException(ErrorMessages.DATE_TIME_HELPER_PARSER_FAILURE, timestamp, timestampFormat, exc.getLocalizedMessage());
		}
		//returns formatted date
		return dateString;
	}
}
