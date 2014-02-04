package itti.com.pl.arena.cm.service.jms;

import com.safran.arena.MessageFilterInterface;

import eu.arena_fp7._1.AbstractDataFusionType;

/**
 * Default mesage filter used by the Context Module
 * 
 * @author cm-admin
 * 
 */
public class ContextModuleFilter implements MessageFilterInterface {

    @Override
    public boolean accept(AbstractDataFusionType arg0) {
        // for now, accepts any data
        return true;
    }

}
