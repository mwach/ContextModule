package itti.com.pl.arena.cm.geoportal;

import itti.com.pl.arena.cm.geoportal.govpl.GeoportalService;
import itti.com.pl.arena.cm.geoportal.govpl.dto.GeoportalRequestObject;

public interface Geoportal {

	public String getGeoportalStringData(GeoportalService service, GeoportalRequestObject requestObject) throws GeoportalException;

}
