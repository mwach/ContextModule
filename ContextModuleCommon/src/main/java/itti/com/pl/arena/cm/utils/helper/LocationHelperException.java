package itti.com.pl.arena.cm.utils.helper;

import itti.com.pl.arena.cm.ContextModuleException;
import itti.com.pl.arena.cm.ErrorMessages;

/**
 * Exception thrown by the {@link LocationHelper} class
 * 
 * @author mawa
 * 
 */
public class LocationHelperException extends ContextModuleException {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    /**
     * Exception, which may be thrown by the {@link LocationHelper} class
     * 
     * @param errorMsg
     *            exception details
     * @param params
     *            additional parameters used to construct errorMsg
     */
    public LocationHelperException(ErrorMessages errorMsg, Object... params) {
        super(String.format(errorMsg.getMessage(), params));
    }
}
