package itti.com.pl.arena.cm.server.utils.helpers;

import itti.com.pl.arena.cm.dto.coordinates.FieldOfViewObject;
import itti.com.pl.arena.cm.server.exception.ErrorMessages;

/**
 * Helper methods for the {@link FieldOfViewObject} data bean
 * @author cm-admin
 *
 */
public final class FieldOfViewHelper {

    /**
     * Default constructor
     */
    private FieldOfViewHelper(){}
    
    /**
     * Calculates approximate visibility of the given object in the camera
     * Value 100 means, object is completely visible for the camera, 
     * Value 0 means, object is not visible by the camera
     * @param fovObject object representing parking lot object defined in the ontology e.g. building
     * @return visibility (in percentages)
     * @throws FieldOfViewHelperException 
     */
    public static double calculateVisibility(FieldOfViewObject fovObject) throws FieldOfViewHelperException {

        if(fovObject == null){
            throw new FieldOfViewHelperException(
                    ErrorMessages.FIELD_OF_VIEW_HELPER_EMPRY_OBJECT);
        }
        //visible vertexes
        int visibleObjects = fovObject.getVisibleObjects().size();
        //all vertexes of the object
        int allObjects = fovObject.getVisibleObjects().size() + fovObject.getNotVisibleObjects().size();
        //if there are no vertexes for given object return 0, otherwise calculate visibility
        return allObjects == 0 ? 0 : 100.0 * visibleObjects / allObjects;
    }

}
