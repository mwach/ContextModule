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

    // percentage of the visibility of the object in the camera
    private double visibility = Constants.UNDEFINED_VALUE;

    // camera left angle
    private double cameraLeftAngle = Constants.UNDEFINED_VALUE;
    // camera right angle
    private double cameraRightAngle = Constants.UNDEFINED_VALUE;

    //closest coordinate in the camera field of view
    private RadialCoordinate closestCoordinate = null;
    //closest coordinate in the camera field of view
    private RadialCoordinate maxLeftCoordinate = null;
    //closest coordinate in the camera field of view
    private RadialCoordinate maxRightCoordinate = null;

    // visibility of the object in the camera in degrees
    private double cameraFoVOccupancy = Constants.UNDEFINED_VALUE;
    // percentage of the visibility of the object in the camera
    private double cameraFoVOccupancyPercentage = Constants.UNDEFINED_VALUE;

    // list of objects in the camera FoV
    private List<RadialCoordinate> visibleObjects = new ArrayList<>();
    // list of objects not in the camera FoV
    private List<RadialCoordinate> notVisibleObjects = new ArrayList<>();

    public FieldOfViewObject(String id) {
        super(id);
    }

    /**
     * returns percentage of the visibility of the object in the camera
     * 
     * @return visibility in percentages
     */
    public double getVisibility() {
        return visibility;
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

    /**
     * returns camera left angle
     * 
     * @return camera left angle
     */
    public double getCameraLeftAngle() {
        return cameraLeftAngle;
    }

    /**
     * sets camera left angle
     * 
     * @param cameraLeftAngle camera left angle
     */
    public void setCameraLeftAngle(double cameraLeftAngle) {
        this.cameraLeftAngle = cameraLeftAngle;
    }

    /**
     * returns camera right angle
     * 
     * @return camera right angle
     */
    public double getCameraRightAngle() {
        return cameraRightAngle;
    }

    /**
     * sets camera left angle
     * 
     * @param cameraRightAngle camera right angle
     */
    public void setCameraRightAngle(double cameraRightAngle) {
        this.cameraRightAngle = cameraRightAngle;
    }

    /**
     * returns closest coordinate
     * 
     * @return closest coordinate
     */
    public RadialCoordinate getClosestCoordinate() {
        return closestCoordinate;
    }

    /**
     * sets the closest coordinate
     * 
     * @param closestCoordinate closest coordinate
     */
    public void setClosestCoordinate(RadialCoordinate closestCoordinate) {
        this.closestCoordinate= closestCoordinate;
    }

    /**
     * returns max left coordinate
     * 
     * @return max left coordinate
     */
    public RadialCoordinate getMaxLeftCoordinate() {
        return maxLeftCoordinate;
    }

    /**
     * sets the max left coordinate
     * 
     * @param maxLeftCoordinate max left coordinate
     */
    public void setMaxLeftCoordinate(RadialCoordinate maxLeftCoordinate) {
        this.maxLeftCoordinate = maxLeftCoordinate;
    }

    /**
     * returns max right coordinate
     * 
     * @return max right coordinate
     */
    public RadialCoordinate getMaxRightCoordinate() {
        return maxRightCoordinate;
    }

    /**
     * sets the max right coordinate
     * 
     * @param maxRightCoordinate max right coordinate
     */
    public void setMaxRightCoordinate(RadialCoordinate maxRightCoordinate) {
        this.maxRightCoordinate = maxRightCoordinate;
    }

    /**
     * returns Building area in the camera field of view area in degrees
     * 
     * @return visibility in degrees
     */
    public double getCameraFoVOccupancy() {
        return cameraFoVOccupancy;
    }

    /**
     * sets Building area in the camera field of view area in degrees
     * 
     * @param visibility
     *            visibility in degrees
     */
    public void setCameraFoVOccupancy(double objectVisibilityInTheCamera) {
        this.cameraFoVOccupancy = objectVisibilityInTheCamera;
    }

    /**
     * returns Percentage of: building area in the camera field of view area
     * 
     * @return visibility in percentages
     */
    public double getCameraFoVOccupancyPercentage() {
        return cameraFoVOccupancyPercentage;
    }

    /**
     * sets Percentage of: building area in the camera field of view area
     * 
     * @param visibility
     *            visibility in percentages
     */
    public void setCameraFoVOccupancyPercentage(double cameraFoVOccupancyPercentage) {
        this.cameraFoVOccupancyPercentage = cameraFoVOccupancyPercentage;
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


    @Override
    public String toString() {
        return "FieldOfViewObject [visibility=" + visibility + ", visibleObjects=" + visibleObjects + ", notVisibleObjects=" + notVisibleObjects + "]";
    }

    public String toPrettyString(){ 
        return String.format(
                "FieldOfViewObject [id=%s, visibility=%f%%,\n"
                + "cameraLeftAngle=%f, cameraRightAngle=%f,\n"
                + "cameraFoVOccupancyDegrees=%f,\n"
                + "cameraFoVOccupancyPct=%f,\n"
                + "pctOfTheBuildingInFoV=%f%%,\n"
                + "closestCoordinate=%s,\n"
                + "maxLeftCoordinate=%s,\n"
                + "maxRightCoordinate=%s,\n"
                + "visibleObjects=%s,\n"
                + "notVisibleObjects=%s]",
                getId(), getVisibility(), getCameraLeftAngle(), getCameraRightAngle(), getCameraFoVOccupancy(), 
                Math.abs(100.0 * getCameraFoVOccupancy() / (getCameraRightAngle() - getCameraLeftAngle())),
                getCameraFoVOccupancyPercentage(),
                getClosestCoordinate(), getMaxLeftCoordinate(), getMaxRightCoordinate(),
                getVisibleObjects(), getNotVisibleObjects());
    }
}
