package itti.com.pl.arena.cm.dto.dynamicobj;

import itti.com.pl.arena.cm.utils.helper.StringHelper;

/**
 * Build-in camera types available for the {@link Camera}
 * @author cm-admin
 *
 */
public enum CameraType {

    Fisheye,
    Sphere,
    Infrared,
    Thermal,
    Other,
    ;

    /**
     * Returns camera type from a list of predefined types
     * If no matching type is found, a default type is going to be returned
     * @param cameraType name of the type
     * @return value of the type
     */
    public static CameraType getCameraType(String cameraType){

        if(StringHelper.hasContent(cameraType)){
            for (CameraType buildInCameraType : CameraType.values()) {
                if(StringHelper.equalsIgnoreCase(buildInCameraType.name(), cameraType)){
                    return buildInCameraType;
                }
            }
        }
        return getDefaultCameraType();
    }

    /**
     * Returns default camera type
     * @return default camera type
     */
    public static CameraType getDefaultCameraType(){
        return Other;
    }
}
