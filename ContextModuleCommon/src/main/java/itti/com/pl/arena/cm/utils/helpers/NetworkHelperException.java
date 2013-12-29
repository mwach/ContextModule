package itti.com.pl.arena.cm.utils.helpers;

import itti.com.pl.arena.cm.ContextModuleException;
import itti.com.pl.arena.cm.ErrorMessages;


/**
 * Exception thrown by the {@link NetworkHelper} components
 * @author mawa
 *
 */
public class NetworkHelperException extends ContextModuleException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Exception thrown by the {@link NetworkHelper} utility class methods
	 * @param reason exception
	 * @param errorMsg exception details
	 * @param params additional parameters used to construct errorMsg
	 */
	public NetworkHelperException(Throwable reason, ErrorMessages errorMsg, Object...params) {
		super(String.format(errorMsg.getMessage(), params), reason);
	}
}
