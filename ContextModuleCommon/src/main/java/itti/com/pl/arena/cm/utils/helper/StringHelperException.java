package itti.com.pl.arena.cm.utils.helper;

import itti.com.pl.arena.cm.exception.ContextModuleException;
import itti.com.pl.arena.cm.exception.ErrorMessages;

/**
 * Exception thrown by the {@link StringHelper} utility
 * 
 * @author mawa
 * 
 */
public class StringHelperException extends ContextModuleException {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    /**
     * Exception thrown by the {@link StringHelper} utility class methods
     * 
     * @param reason
     *            exception
     * @param errorMsg
     *            exception details
     * @param params
     *            additional parameters used to construct errorMsg
     */
    public StringHelperException(Throwable reason, ErrorMessages errorMsg, Object... params) {
        super(String.format(errorMsg.getMessage(), params), reason);
    }
}
