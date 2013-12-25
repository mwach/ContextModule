package itti.com.pl.arena.cm.service.jms;

import itti.com.pl.arena.cm.ContextModuleException;

public class JmsException extends ContextModuleException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JmsException(String message, Throwable throwable, Object... args) {
		super(message, throwable, args);
	}

	public JmsException(String message, Object... args) {
		super(message, args);
	}

}
