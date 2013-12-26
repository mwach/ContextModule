package itti.com.pl.arena.cm.utils.helpers;

import itti.com.pl.arena.cm.ContextModuleRuntimeException;
import itti.com.pl.arena.cm.ErrorMessages;

/**
 * Exception thrown by the {@link DateTimeHelper} class
 * @author mawa
 *
 */
public class DateTimeHelperException extends ContextModuleRuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DateTimeHelperException(ErrorMessages message, Object... params){
		super(message.getMessage(), params);
	}
}
