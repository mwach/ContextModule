package itti.com.pl.arena.cm.utils.helpers;

import itti.com.pl.arena.cm.ContextModuleException;


/**
 * Exception thrown by the Persistence components
 * @author mawa
 *
 */
public class NetworkHelperException extends ContextModuleException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Persistence exception
	 * @param reason base persistence exception
	 * @param errorMsg exception details
	 * @param params additional parameters used to construct errorMsg
	 */
	public NetworkHelperException(Throwable reason, String errorMsg, Object...params) {
		super(String.format(errorMsg, params), reason);
	}

	public NetworkHelperException(String message, Object... params){
		super(String.format(message, params));
	}
}
