package itti.com.pl.arena.cm.service.jms;

import itti.com.pl.arena.cm.ContextModuleException;
import itti.com.pl.arena.cm.ErrorMessages;

/**
 * Exception class for the JMS service
 * 
 * @author cm-admin
 * 
 */
public class JmsException extends ContextModuleException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Exception constructor
     * 
     * @param message
     *            exception message
     * @param args
     *            optional message parameters
     */
    public JmsException(ErrorMessages message, Object... args) {
	super(message.getMessage(), args);
    }
}
