package itti.com.pl.arena.cm.server.utils.helpers;

import itti.com.pl.arena.cm.exception.ContextModuleException;
import itti.com.pl.arena.cm.server.exception.ErrorMessages;

/**
 * Exception thrown by the {@link SpringHelper} class
 * 
 * @author mawa
 * 
 */
public class SpringHelperException extends ContextModuleException {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    /**
     * Exception, which may be thrown by the {@link SpringHelper} class
     * 
     * @param reason
     *            base exception
     * @param errorMsg
     *            exception details
     * @param params
     *            additional parameters used to construct errorMsg
     */
    public SpringHelperException(Throwable reason, ErrorMessages errorMsg, Object... params) {
        super(String.format(errorMsg.getMessage(), params), reason);
    }

    /**
     * Exception, which may be thrown by the {@link SpringHelper} class
     * 
     * @param errorMsg
     *            exception details
     * @param params
     *            additional parameters used to construct errorMsg
     */
    public SpringHelperException(ErrorMessages errorMsg, Object... params) {
        super(String.format(errorMsg.getMessage(), params));
    }
}
