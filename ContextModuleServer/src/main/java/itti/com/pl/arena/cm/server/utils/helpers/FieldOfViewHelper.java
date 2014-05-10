package itti.com.pl.arena.cm.server.utils.helpers;

import java.util.List;

import itti.com.pl.arena.cm.Constants;
import itti.com.pl.arena.cm.dto.coordinates.FieldOfViewObject;
import itti.com.pl.arena.cm.dto.coordinates.RadialCoordinate;
import itti.com.pl.arena.cm.server.exception.ErrorMessages;

/**
 * Helper methods for the {@link FieldOfViewObject} data bean
 * 
 * @author cm-admin
 * 
 */
public final class FieldOfViewHelper {

    /**
     * Default constructor
     */
    private FieldOfViewHelper() {
    }

    private static enum Searches {
        Closest, MaxLeft, MaxRight
    }

    /**
     * Calculates approximate visibility of the given object in the camera Value 100 means, object is completely visible
     * for the camera, Value 0 means, object is not visible by the camera
     * 
     * @param fovObject
     *            object representing parking lot object defined in the ontology e.g. building
     * @return visibility (in percentages)
     * @throws FieldOfViewHelperException
     */
    public static double calculateVisibility(FieldOfViewObject fovObject) throws FieldOfViewHelperException {

        if (fovObject == null) {
            throw new FieldOfViewHelperException(ErrorMessages.FIELD_OF_VIEW_HELPER_EMPRY_OBJECT);
        }
        // visible vertexes
        int visibleObjects = fovObject.getVisibleObjects().size();
        // all vertexes of the object
        int allObjects = fovObject.getVisibleObjects().size() + fovObject.getNotVisibleObjects().size();
        // if there are no vertexes for given object return 0, otherwise calculate visibility
        return allObjects == 0 ? 0 : 100.0 * visibleObjects / allObjects;
    }

    /**
     * Returns object coordinate, which is the closest one to the camera
     * 
     * @param fov
     *            analyzed {@link FieldOfViewObject} object
     * 
     * @return closest object coordinate
     * @throws FieldOfViewHelperException
     */
    public static RadialCoordinate getClosestCoordinate(FieldOfViewObject fov) throws FieldOfViewHelperException {
        return getSpecialCoordinate(fov, Searches.Closest);
    }

    /**
     * Returns most left object coordinate, which is visible by the camera
     * 
     * @param fov
     *            analyzed {@link FieldOfViewObject} object
     * 
     * @return most left/visible object coordinate
     * @throws FieldOfViewHelperException
     */
    public static RadialCoordinate getMaxLeftCoordinate(FieldOfViewObject fov) throws FieldOfViewHelperException {
        return getSpecialCoordinate(fov, Searches.MaxLeft);
    }

    /**
     * Returns most right object coordinate, which is visible by the camera
     * 
     * @param fov
     *            analyzed {@link FieldOfViewObject} object
     * 
     * @return most right/visible object coordinate
     * @throws FieldOfViewHelperException
     */
    public static RadialCoordinate getMaxRightCoordinate(FieldOfViewObject fov) throws FieldOfViewHelperException {
        return getSpecialCoordinate(fov, Searches.MaxRight);
    }

    /**
     * Returns coordinate matching the criteria
     * 
     * @param fov
     *            analyzed {@link FieldOfViewObject} object
     * @param searchCriteria
     *            criteria
     * @return coordinate matching specified criteria
     * @throws FieldOfViewHelperException
     */
    private static RadialCoordinate getSpecialCoordinate(FieldOfViewObject fov, Searches searchCriteria)
            throws FieldOfViewHelperException {

        // check, if request param is null
        if (fov == null) {
            throw new FieldOfViewHelperException(ErrorMessages.FIELD_OF_VIEW_HELPER_EMPRY_OBJECT);
        }
        RadialCoordinate closestCoordinate = null;
        // check all coordinates
        for (RadialCoordinate visibleCoordinate : fov.getVisibleObjects()) {
            if (closestCoordinate == null) {
                closestCoordinate = visibleCoordinate;
                // if current one is closer, use it
            } else if ((searchCriteria == Searches.Closest && visibleCoordinate.getDistance() < closestCoordinate.getDistance())
                    || (searchCriteria == Searches.MaxLeft && visibleCoordinate.getAngle() < closestCoordinate.getAngle())
                    || (searchCriteria == Searches.MaxRight && visibleCoordinate.getAngle() > closestCoordinate.getAngle())) {
                closestCoordinate = visibleCoordinate;
            }
        }
        return closestCoordinate;
    }


    /**
     * Percentage of: building area in the camera field of view area
     * @param fov
     * @param maxDoubleLeft
     * @param maxDoubleRight
     * @return
     */
    public static double getPercentageWidthOfTheObjectInCameraFoV(FieldOfViewObject fov, double maxDoubleLeft, double maxDoubleRight) {
        //get the object width in the camera field of view
        double buildingAngleForCamera = getBuildingAngleInTheCameraFieldOfView(fov, maxDoubleLeft, maxDoubleRight);
        //get the total object width
        double buildingAngle = getBuildingWidthAngle(fov);
        return (100.0 * buildingAngleForCamera) / buildingAngle;
    }

    /**
     * Calculates total 'width' of the object in degrees (not only in the camera FoV)
     * @param fov object coordinates
     * @return object's width in degrees
     */
    private static double getBuildingWidthAngle(FieldOfViewObject fov) {
        double minAngle = Constants.UNDEFINED_VALUE;
        double maxAngle = Constants.UNDEFINED_VALUE;

        //get all coordinates (visible and non-visible)
        List<RadialCoordinate> allCoordinates = fov.getVisibleObjects();
        allCoordinates.addAll(fov.getNotVisibleObjects());

        //for all the coordinates
        for (RadialCoordinate coordinate : allCoordinates) {
            //search for the max and min angles
            if (coordinate.getAngle() > maxAngle || maxAngle == Constants.UNDEFINED_VALUE) {
                maxAngle = coordinate.getAngle();
            } else if (coordinate.getAngle() < minAngle || minAngle == Constants.UNDEFINED_VALUE) {
                minAngle = coordinate.getAngle();
            }
        }
        //object's width is a difference between these two values
        return maxAngle - minAngle;
    }

    /**
     * Calculates width of the object in the camera FoV
     * @param fov analyzed object's coordinates
     * @param maxLeftAngle maximum camera left angle
     * @param maxRightAngle maximum camera rigth angle
     * @return
     */
    private static double getBuildingAngleInTheCameraFieldOfView(FieldOfViewObject fov, double maxLeftAngle, double maxRightAngle) {

        double minAngle = Constants.UNDEFINED_VALUE;
        double maxAngle = Constants.UNDEFINED_VALUE;

        // check all non-visible coordinates
        for (RadialCoordinate notVisibleCoordinate : fov.getNotVisibleObjects()) {
            // check, if there are building vertexes on the left side out of camera field of view
            if (notVisibleCoordinate.getAngle() < maxLeftAngle) {
                minAngle = maxLeftAngle;
            }
            // check, if there are building vertexes on the right side out of camera field of view
            else if (notVisibleCoordinate.getAngle() > maxRightAngle) {
                maxAngle = maxRightAngle;
            }
        }

        // check all visible coordinates - building is 'narrower' than camera field of view
        for (RadialCoordinate visibleCoordinate : fov.getVisibleObjects()) {
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

}
