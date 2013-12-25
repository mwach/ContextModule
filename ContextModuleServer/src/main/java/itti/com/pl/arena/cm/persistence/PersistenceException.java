package itti.com.pl.arena.cm.persistence;

import itti.com.pl.arena.cm.ContextModuleException;

/**
 * Exception thrown by the Persistence components
 * @author mawa
 *
 */
public class PersistenceException extends ContextModuleException{

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
	public PersistenceException(Throwable reason, String errorMsg, Object...params) {
		super(String.format(errorMsg, params), reason);
	}
}
