package itti.com.pl.arena.cm.utils.helpers;

import itti.com.pl.arena.cm.ContextModuleException;

/**
 * Exception thrown by the DateTime components
 * @author mawa
 *
 */
public class DateTimeHelperException extends ContextModuleException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DateTimeHelperException(String message, Object... params){
		super(String.format(message, params));
	}
}
