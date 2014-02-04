package itti.com.pl.arena.cm.dto.dynamicobj;

import itti.com.pl.arena.cm.utils.helper.StringHelper;

/**
 * Position of the camera installed on platform
 * @author cm-admin
 *
 */
public enum RelativePosition {

    Front("front"), 
    LeftFront("left front"), 
    RightFront("right front"), 
    Back("back"), 
    LeftBack("left back"), 
    RightBack("right back"), 
    Left("left"), 
    Rigth("right")

    ;

    //position in 'human-readable' form
    private String position;

    /**
     * private constructor for this enumeration
     * @param position position
     */
    private RelativePosition(String position) {
        this.position = position;
    }

    /**
     * Returns value of the {@link RelativePosition} in human-readable form
     * @return position
     */
    public String getPosition() {
        return position;
    }

    /**
     * returns value of the {@link RelativePosition} based on the provided human-readable form
     * @param position value of the position
     * @return proper enum value, or Null if invalid position was provided
     */
    public static RelativePosition getPostion(String position) {

        // no position provided
        if (position == null) {
            return null;
        }
        //enumerate available position and return the matching one
        for (RelativePosition positionEnum : RelativePosition.values()) {
            if (StringHelper.equalsIgnoreCase(positionEnum.getPosition(), position)) {
                return positionEnum;
            }
        }
        //could not find matching position
        return null;
    }
}
