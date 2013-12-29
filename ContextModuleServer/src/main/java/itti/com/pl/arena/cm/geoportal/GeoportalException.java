package itti.com.pl.arena.cm.geoportal;

import itti.com.pl.arena.cm.ContextModuleException;
import itti.com.pl.arena.cm.ErrorMessages;

/**
 * Geoportal exception Thrown by the Geoportal module
 * 
 * @author mawa
 * 
 */
public class GeoportalException extends ContextModuleException {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public GeoportalException(ErrorMessages message, Object... args) {
	super(message.getMessage(), args);
    }

    public GeoportalException(Throwable throwables, ErrorMessages message,
	    Object... args) {
	super(message.getMessage(), throwables, args);
    }

}
