package itti.com.pl.arena.cm.dto.coordinates;

import itti.com.pl.arena.cm.utils.helper.CoordinatesHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * List of coordinates for given ontology object
 * @author cm-admin
 *
 */
public class ArenaObjectCoordinate implements Iterable<RadialCoordinate> {

    //ID of the object
    private String objectId;
    //list of coordinates describing given object
    private List<RadialCoordinate> radialCoordinates = new ArrayList<>();

    /**
     * Object constructor
     * @param objectId ID of the ontology object
     */
    public ArenaObjectCoordinate(String objectId) {
        this.objectId = objectId;
    }

    /**
     * Returns ID of the ontology object
     * @return ID of the object
     */
    public String getObjectId() {
        return objectId;
    }

    /**
     * adds a new radial coordinate to the list of coordinates
     * @param radius radius
     * @param angle angle
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
     * @return list of coordinates
     */
    public List<RadialCoordinate> getRadialCoordinates(){
        return new ArrayList<>(radialCoordinates);
    }

    /**
     * Returns a list of Cartesian coordinates for given object
     * @return list of coordinates
     */
    public List<CartesianCoordinate> getCartesianCoordinates(){

        return createCartesianCoordinates(getRadialCoordinates());
    }

    /**
     * Translates provided list of {@link RadialCoordinate} into list of {@link CartesianCoordinate} objects
     * @param radialCoordinates list of {@link RadialCoordinate} objects
     * @return list of {@link CartesianCoordinate} objects
     */
    private List<CartesianCoordinate> createCartesianCoordinates(List<RadialCoordinate> radialCoordinates){

        //need to calculate Cartesian coordinates from radial ones
        List<CartesianCoordinate> coordinates = new ArrayList<>();
        for (RadialCoordinate radialCoordinate : radialCoordinates) {

            CartesianCoordinate cartesianCoordinate = new CartesianCoordinate(
                    CoordinatesHelper.getXFromRadial(radialCoordinate.getRadius(), radialCoordinate.getAngle()),
                    CoordinatesHelper.getYFromRadial(radialCoordinate.getRadius(), radialCoordinate.getAngle())
            );
            coordinates.add(cartesianCoordinate);
        }
        return coordinates;
    }
}
