package itti.com.pl.arena.cm.utils.helper;

import itti.com.pl.arena.cm.ContextModuleException;
import itti.com.pl.arena.cm.ErrorMessages;

/**
 * Exception thrown by the {@link JsonHelper} class
 * 
 * @author mawa
 * 
 */
public class JsonHelperException extends ContextModuleException {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    /**
     * Exception, which may be thrown by the {@link IOHelper} class
     * 
     * @param errorMsg
     *            exception details
     * @param params
     *            additional parameters used to construct errorMsg
     */
    public JsonHelperException(ErrorMessages errorMsg, Object... params) {
        super(String.format(errorMsg.getMessage(), params));
    }
}
