package itti.com.pl.arena.cm;

/**
 * Base runtime exception thrown by the Context Module
 * 
 * @author cm-admin
 * 
 */
public class ContextModuleRuntimeException extends RuntimeException {

    /**
     * Class UID
     */
    private static final long serialVersionUID = 1L;

    /**
     * Base runtime exception class thrown by the ContextModule
     * 
     * @param message
     *            message
     * @param exc
     *            exception
     * @param args
     *            list of message parameters
     */
    public ContextModuleRuntimeException(String message, RuntimeException exc, Object... args) {
        super(String.format(message, args), exc);
    }

    /**
     * Base runtime exception class thrown by the ContextModule
     * 
     * @param message
     *            message
     * @param args
     *            list of message parameters
     */
    public ContextModuleRuntimeException(String message, Object... args) {
        super(String.format(message, args));
    }
}
