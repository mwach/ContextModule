package itti.com.pl.arena.cm.ontology;

import java.util.List;

import itti.com.pl.arena.cm.dto.GeoObject;
import itti.com.pl.arena.cm.dto.PlatformInformation;
import itti.com.pl.arena.cm.geoportal.gov.pl.dto.GeoportalResponse;

public interface Ontology {

	public PlatformInformation getPlatformInformation(String platformId) throws OntologyException;

	public List<String> getPlatforms(double x, double y) throws OntologyException;

	public List<String> getGISObjects(double x, double y) throws OntologyException;

	public GeoObject getGISObject(String objectId) throws OntologyException;

	public void addGeoportalData(double x, double y, GeoportalResponse geoportalData);
}
