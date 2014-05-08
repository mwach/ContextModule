package itti.com.pl.arena.cm.dto.coordinates;

import itti.com.pl.arena.cm.dto.OntologyObject;
import itti.com.pl.arena.cm.utils.helper.CoordinatesHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * List of coordinates for given ontology object
 * 
 * @author cm-admin
 * 
 */
public class ArenaObjectCoordinate extends OntologyObject implements Iterable<RadialCoordinate> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    // list of coordinates describing given object
    private List<RadialCoordinate> radialCoordinates = new ArrayList<>();

    /**
     * Object constructor
     * 
     * @param id
     *            ID of the ontology object
     */
    public ArenaObjectCoordinate(String id) {
        super(id);
    }

    /**
     * adds a new radial coordinate to the list of coordinates
     * 
     * @param radius
     *            radius
     * @param angle
     *            angle
     */
    public void addRadialCoordinates(double radius, double angle) {
        this.radialCoordinates.add(new RadialCoordinate(radius, angle));
    }

    @Override
    public Iterator<RadialCoordinate> iterator() {
        return radialCoordinates.iterator();
    }

    /**
     * Returns a list of radial coordinates for given object
     * 
     * @return list of coordinates
     */
    public List<RadialCoordinate> getRadialCoordinates() {
        return new ArrayList<>(radialCoordinates);
    }

    /**
     * Returns a list of Cartesian coordinates for given object
     * 
     * @return list of coordinates
     */
    public List<CartesianCoordinate> getCartesianCoordinates() {

        return createCartesianCoordinates(getRadialCoordinates());
    }

    /**
     * Translates provided list of {@link RadialCoordinate} into list of {@link CartesianCoordinate} objects
     * 
     * @param radialCoordinates
     *            list of {@link RadialCoordinate} objects
     * @return list of {@link CartesianCoordinate} objects
     */
    private List<CartesianCoordinate> createCartesianCoordinates(List<RadialCoordinate> radialCoordinates) {

        // need to calculate Cartesian coordinates from radial ones
        List<CartesianCoordinate> coordinates = new ArrayList<>();
        for (RadialCoordinate radialCoordinate : radialCoordinates) {

            CartesianCoordinate cartesianCoordinate = new CartesianCoordinate(CoordinatesHelper.getXFromRadial(
                    radialCoordinate.getDistance(), radialCoordinate.getAngle()), CoordinatesHelper.getYFromRadial(
                    radialCoordinate.getDistance(), radialCoordinate.getAngle()));
            coordinates.add(cartesianCoordinate);
        }
        return coordinates;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((radialCoordinates == null) ? 0 : radialCoordinates.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ArenaObjectCoordinate other = (ArenaObjectCoordinate) obj;
        if (radialCoordinates == null) {
            if (other.radialCoordinates != null)
                return false;
        } else if (!radialCoordinates.equals(other.radialCoordinates))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ArenaObjectCoordinate [getId()=" + getId() + ", radialCoordinates=" + radialCoordinates + "]";
    }

}
