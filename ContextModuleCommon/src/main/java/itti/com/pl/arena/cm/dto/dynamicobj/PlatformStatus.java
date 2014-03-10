package itti.com.pl.arena.cm.dto.dynamicobj;

import itti.com.pl.arena.cm.Constants;

/**
 * Current status of the platform
 * @author cm-admin
 *
 */
public enum PlatformStatus {

    /*
     * Status of the platform is unknown. 
     * This is the default status after system initialization
     */
    Unknown(Constants.UNDEFINED_VALUE),
    /*
     * Platform is on the CM-managed parking
     */
    StoppedOnParking(0),
    /*
     * Platform is stopped outside CM-managed parking
     */
    StoppedOutsideParking(1),
    /*
     * Platform is moving
     */
    Moving(2),

    ;

    /*
     * numeric representation of the current platform status
     */
    private int statusCode = Constants.UNDEFINED_VALUE;

    /**
     * Default private constructor
     * @param statusCode ID of the status code
     */
    private PlatformStatus(int statusCode){
        this.statusCode = statusCode;
    }

    /**
     * returns numeric representation of the current status
     * @return status code as int
     */
    public int getStatusCode(){
        return statusCode;
    }
}
