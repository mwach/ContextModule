package itti.com.pl.arena.cm.geoportal;

import itti.com.pl.arena.cm.ContextModuleException;
import itti.com.pl.arena.cm.ErrorMessages;

/**
 * Geoportal exception thrown by the Geoportal module
 * 
 * @author mawa
 * 
 */
public class GeoportalException extends ContextModuleException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Exception constructor
     * @param message exception message
     * @param args optional arguments
     */
    public GeoportalException(ErrorMessages message, Object... args) {
        super(message.getMessage(), args);
    }

    /**
     * Exception constructor
     * @param message exception message
     * @param throwables base exception
     * @param args optional arguments
     */
    public GeoportalException(ErrorMessages message, Throwable throwables, Object... args) {
        super(message.getMessage(), throwables, args);
    }

}
