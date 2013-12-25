package itti.com.pl.arena.cm.utils.helpers;

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public final class LogHelper {

	private LogHelper()
	{}

	static{
        try {
        	LogManager.getLogManager().reset();
        	InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("logging.properties");
            LogManager.getLogManager().readConfiguration(is);
        } catch (Exception exc) {
        	System.out.println("Could not initialize logger: " + exc.getLocalizedMessage());
        }
	}

	public static void error(Class<?> clazz, String method, String msg, Object... args){
		log(clazz, Level.SEVERE, false, method, msg, args);
	}

	public static void exception(Class<?> clazz, String method, String msg, Throwable throwable){
		log(clazz, Level.SEVERE, true, method, msg, throwable);
	}

	public static void warning(Class<?> clazz, String method, String msg, Object... args){
		log(clazz, Level.WARNING, false, method, msg, args);
	}

	public static void info(Class<?> clazz, String method, String msg, Object... args){
		log(clazz, Level.INFO, false, method, msg, args);
	}

	public static void debug(Class<?> clazz, String method, String msg, Object... args){
		log(clazz, Level.FINE, false, method, msg, args);
	}

	private static void log(Class<?> clazz, Level level, boolean exception, String method, String msg, Object... args){

		Logger logger = Logger.getLogger(clazz.getPackage().getName());

		//optimization - don't waste time for formatting strings that will not be logged
		if(logger.isLoggable(level)){
			//add thread ID to all logs
			long threadId = Thread.currentThread().getId();
			String formattedMessage = String.format("[%s:%s][Thread:%d] %s", clazz.getName(), method, threadId, msg);

			//check, if exception is reported
			if(exception)
			{
				logger.log(level, formattedMessage, (Throwable)args[0]);
			}
			else
			{
				logger.log(level, String.format(msg, args));
			}
		}
	}

}
