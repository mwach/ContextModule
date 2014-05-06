package itti.com.pl.arena.cm.dto.coordinates;

import java.util.ArrayList;
import java.util.List;

import itti.com.pl.arena.cm.Constants;
import itti.com.pl.arena.cm.dto.OntologyObject;

/**
 * Contains information of the single building/infrastructure in the camera field of view
 * 
 * @author cm-admin
 * 
 */
public class FieldOfViewObject extends OntologyObject {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private enum Searches {
        Closest, MaxLeft, MaxRight
    }

    // percentage of the visibility of the object in the camera
    private double visibility = Constants.UNDEFINED_VALUE;

    private double leftAngle = Constants.UNDEFINED_VALUE;
    private double rightAngle = Constants.UNDEFINED_VALUE;

    // list of objects in the camera FoV
    private List<RadialCoordinate> visibleObjects = new ArrayList<>();
    // list of objects not in the camera FoV
    private List<RadialCoordinate> notVisibleObjects = new ArrayList<>();

    public FieldOfViewObject(String id, double leftAngle, double rightAngle) {
        super(id);
        this.leftAngle = leftAngle;
        this.rightAngle = rightAngle;
    }

    /**
     * returns percentage of the visibility of the object in the camera
     * 
     * @return visibility in percentages
     */
    public double getVisibility() {
        return visibility;
    }

    public double getLeftAngle() {
        return leftAngle;
    }

    public double getRightAngle() {
        return rightAngle;
    }

    /**
     * sets percentage of the visibility of the object in the camera
     * 
     * @param visibility
     *            visibility in percentages
     */
    public void setVisibility(double visibility) {
        this.visibility = visibility;
    }

    public List<RadialCoordinate> getVisibleObjects() {
        return new ArrayList<>(visibleObjects);
    }

    public void addVisibleObject(RadialCoordinate visibleObject) {
        if (visibleObject != null) {
            visibleObjects.add(visibleObject);
        }
    }

    public List<RadialCoordinate> getNotVisibleObjects() {
        return new ArrayList<>(notVisibleObjects);
    }

    public void addNotVisibleObject(RadialCoordinate notVisibleObject) {
        if (notVisibleObject != null) {
            notVisibleObjects.add(notVisibleObject);
        }
    }

    /**
     * Returns building coordinate, which is the closest one to the camera
     * 
     * @return closest building coordinate
     */
    public RadialCoordinate getClosestCoordinate() {
        return getSpecialCoordinate(Searches.Closest);
    }

    public RadialCoordinate getMaxLeftCoordinate() {
        return getSpecialCoordinate(Searches.MaxLeft);
    }

    public RadialCoordinate getMaxRightCoordinate() {
        return getSpecialCoordinate(Searches.MaxRight);
    }

    public double getBuildingAngleInTheCameraFieldOfView() {

        double minAngle = Constants.UNDEFINED_VALUE;
        double maxAngle = Constants.UNDEFINED_VALUE;

        // check all non-visible coordinates
        for (RadialCoordinate notVisibleCoordinate : getNotVisibleObjects()) {
            // check, if there are building vertexes on the left side out of camera field of view
            if (notVisibleCoordinate.getAngle() < getMaxLeftCoordinate().getAngle()) {
                minAngle = getMaxLeftCoordinate().getAngle();
            }
            // check, if there are building vertexes on the right side out of camera field of view
            else if (notVisibleCoordinate.getAngle() > getMaxRightCoordinate().getAngle()) {
                maxAngle = getMaxRightCoordinate().getAngle();
            }
        }

        // check all visible coordinates - building is 'narrower' than camera field of view
        for (RadialCoordinate visibleCoordinate : getVisibleObjects()) {
            // check, if there are building vertexes on the left side out of camera field of view
            if (visibleCoordinate.getAngle() > maxAngle || maxAngle == Constants.UNDEFINED_VALUE) {
                maxAngle = visibleCoordinate.getAngle();
            }
            // the same for other side
            else if (visibleCoordinate.getAngle() < minAngle || minAngle == Constants.UNDEFINED_VALUE) {
                maxAngle = visibleCoordinate.getAngle();
            }
        }
        return maxAngle - minAngle;
    }

    public double getPercentage() {
        double buildingAngleForCamera = getBuildingAngleInTheCameraFieldOfView();
        double buildingAngle = getBuildingWidthAngle();
        return (100.0 * buildingAngleForCamera) / buildingAngle;
    }

    public double getBuildingWidthAngle() {
        double minAngle = Constants.UNDEFINED_VALUE;
        double maxAngle = Constants.UNDEFINED_VALUE;

        List<RadialCoordinate> allCoordinates = getVisibleObjects();
        allCoordinates.addAll(getNotVisibleObjects());

        for (RadialCoordinate coordinate : allCoordinates) {
            if (coordinate.getAngle() > maxAngle || maxAngle == Constants.UNDEFINED_VALUE) {
                maxAngle = coordinate.getAngle();
            } else if (coordinate.getAngle() < minAngle || minAngle == Constants.UNDEFINED_VALUE) {
                minAngle = coordinate.getAngle();
            }
        }
        return maxAngle - minAngle;
    }

    private RadialCoordinate getSpecialCoordinate(Searches searchCriteria) {
        RadialCoordinate closestCoordinate = null;
        // check all coordinates
        for (RadialCoordinate visibleCoordinate : getVisibleObjects()) {
            if (closestCoordinate == null) {
                closestCoordinate = visibleCoordinate;
                // if current one is closer, use it
            } else {
                switch (searchCriteria) {
                case Closest:
                    if (visibleCoordinate.getRadius() < closestCoordinate.getRadius()) {
                        closestCoordinate = visibleCoordinate;
                    }
                    break;
                case MaxLeft:
                    if (visibleCoordinate.getAngle() < closestCoordinate.getAngle()) {
                        closestCoordinate = visibleCoordinate;
                    }
                    break;
                case MaxRight:
                    if (visibleCoordinate.getAngle() > closestCoordinate.getAngle()) {
                        closestCoordinate = visibleCoordinate;
                    }
                    break;
                default:
                    break;
                }
            }
        }
        return closestCoordinate;
    }

}
