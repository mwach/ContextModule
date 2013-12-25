package itti.com.pl.arena.cm;

public class ContextModuleRuntimeException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public ContextModuleRuntimeException(String message, Exception exc) {
		super(message, exc);
	}

	public ContextModuleRuntimeException(String message) {
		super(message);
	}
}
