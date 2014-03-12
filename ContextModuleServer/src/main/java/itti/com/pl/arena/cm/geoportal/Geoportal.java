package itti.com.pl.arena.cm.geoportal;

import java.util.Set;

import itti.com.pl.arena.cm.dto.GeoObject;
import itti.com.pl.arena.cm.dto.Location;
import itti.com.pl.arena.cm.service.Service;

/**
 * General Geoportal service interface
 * 
 * @author cm-admin
 * 
 */
public interface Geoportal extends Service {

    /**
     * do a request to Geoportal service to retrieve Geoportal data
     * 
     * @param location
     *            location, from which data should be retrieved
     * @param radius
     *            radius defining area, from which data should be collected
     * @return list of objects from given area containing geoportal data
     * @throws GeoportalException
     *             could not retrieve the data
     */
    public Set<GeoObject> getGeoportalData(Location location, double radius) throws GeoportalException;

}
