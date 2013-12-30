package itti.com.pl.arena.cm.persistence;

import itti.com.pl.arena.cm.ContextModuleException;
import itti.com.pl.arena.cm.ErrorMessages;

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
	 * @param reason exception
	 * @param message exception details
	 * @param params additional parameters used to construct message
	 */
	public PersistenceException(Throwable reason, ErrorMessages message, Object...params) {
		super(message.getMessage(), reason, params);
	}
}
