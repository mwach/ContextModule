package itti.com.pl.arena.cm.server.utils.helpers;

import itti.com.pl.arena.cm.exception.ContextModuleException;
import itti.com.pl.arena.cm.server.exception.ErrorMessages;

public class FieldOfViewHelperException extends ContextModuleException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor
     * @param errMessage General error message
     * @param args optional arguments
     */
    public FieldOfViewHelperException(ErrorMessages errMessage, Object... args) {
        super(errMessage.getMessage(), args);
    }

}
