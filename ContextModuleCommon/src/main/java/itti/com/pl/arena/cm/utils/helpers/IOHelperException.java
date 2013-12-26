package itti.com.pl.arena.cm.utils.helpers;

import itti.com.pl.arena.cm.ContextModuleException;
import itti.com.pl.arena.cm.ErrorMessages;


/**
 * Exception thrown by the {@link IOHelper} class
 * @author mawa
 *
 */
public class IOHelperException extends ContextModuleException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Exception, which may be thrown by the {@link IOHelper} class
	 * @param reason base exception
	 * @param errorMsg exception details
	 * @param params additional parameters used to construct errorMsg
	 */
	public IOHelperException(Throwable reason, ErrorMessages errorMsg, Object...params) {
		super(String.format(errorMsg.getMessage(), params), reason);
	}

	/**
	 * Exception, which may be thrown by the {@link IOHelper} class
	 * @param errorMsg exception details
	 * @param params additional parameters used to construct errorMsg
	 */
	public IOHelperException(ErrorMessages errorMsg, Object... params){
		super(String.format(errorMsg.getMessage(), params));
	}
}
