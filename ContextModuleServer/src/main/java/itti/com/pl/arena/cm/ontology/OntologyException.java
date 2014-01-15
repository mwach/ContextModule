package itti.com.pl.arena.cm.ontology;

import itti.com.pl.arena.cm.ContextModuleException;
import itti.com.pl.arena.cm.ErrorMessages;

/**
 * Exception thrown by the Ontology module
 * 
 * @author cm-admin
 * 
 */
public class OntologyException extends ContextModuleException {

    /**
     * ID of the class
     */
    private static final long serialVersionUID = 1L;

    /**
     * Default exception
     * 
     * @param message
     *            message
     * @param args
     *            optional message arguments
     */
    public OntologyException(ErrorMessages message, Object... args) {
        super(message.getMessage(), args);
    }

    /**
     * Default exception
     * 
     * @param throwable
     *            throwable
     * @param message
     *            message
     * @param args
     *            optional message arguments
     */
    public OntologyException(ErrorMessages message, Throwable throwable, Object... args) {
        super(message.getMessage(), throwable, args);
    }
}
