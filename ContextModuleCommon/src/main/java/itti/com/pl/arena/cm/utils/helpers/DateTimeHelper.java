package itti.com.pl.arena.cm.utils.helpers;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateTimeHelper {

	private DateTimeHelper()
	{
	}

	public static String formatTime(long timestamp, String timestampFormat) throws DateTimeHelperException {

		if(!StringHelper.hasContent(timestampFormat))
		{
			throw new DateTimeHelperException("Empty timestamp format provided");
		}

		String dateString = null;
		try{
			SimpleDateFormat sdf = new SimpleDateFormat(timestampFormat);
			dateString = sdf.format(new Date(timestamp));
		}catch(RuntimeException exc){
			throw new DateTimeHelperException("Could not parse provided timestamp '%d' using provided format '%s'. Details: %s", timestamp, timestampFormat, exc.getLocalizedMessage());
		}
		return dateString;
	}
}
