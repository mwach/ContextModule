package itti.com.pl.arena.cm.geoportal;

import itti.com.pl.arena.cm.Service;

/**
 * General Geoportal service interface
 * @author cm-admin
 *
 */
public interface Geoportal extends Service{

    /**
     * do a HTTP request to retrieve Geoportal data
     * @param service
     * @param requestObject
     * @return
     * @throws GeoportalException
     */
    public String getGeoportalData(String geoportalUrl) throws GeoportalException;

}
