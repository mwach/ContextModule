package itti.com.pl.arena.cm;

import itti.com.pl.arena.cm.utils.helpers.StringHelper;

/**
 * Default exception thrown by the ContextModule
 * All ContextModule exceptions should extend this one
 * @author mawa
 *
 */
public abstract class ContextModuleException extends Exception{

	private static final String EXCEPTION_MESSAGE = ContextModuleException.class.getSimpleName();

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ContextModuleException(String message, Throwable throwable, Object... args){
		super(StringHelper.hasContent(message) ? String.format(message, args) : EXCEPTION_MESSAGE, throwable);
	}

	public ContextModuleException(String message, Object... args){
		this(message, (Throwable)null, args);
	}

	public ContextModuleException(String message){
		super(message);
	}
}
