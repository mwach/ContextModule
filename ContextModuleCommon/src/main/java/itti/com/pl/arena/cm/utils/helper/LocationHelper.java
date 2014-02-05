package itti.com.pl.arena.cm.utils.helper;

import itti.com.pl.arena.cm.ErrorMessages;
import itti.com.pl.arena.cm.dto.Location;

/**
 * Helper class for the {@link Location} objects
 * 
 * @author cm-admin
 * 
 */
public final class LocationHelper {

    /**
     * Private contructor
     */
    private LocationHelper(){}


    /**
     * Parses given string into location object
     * 
     * @param locationString
     *            location string
     * @return {@link Location} object
     * @throws LocationHelperException
     */
    public static Location getLocationFromString(String locationString) throws LocationHelperException {

        // validation: check if data was provided
        if (!StringHelper.hasContent(locationString)) {
            throw new LocationHelperException(ErrorMessages.LOCATION_HELPER_NULL_LOCATION);
        }
        // validation: check data format
        String[] locationSplit = locationString.split(",");
        if (locationSplit.length != 2) {
            throw new LocationHelperException(ErrorMessages.LOCATION_HELPER_INVALID_FORMAT, locationString);
        }
        if (NumbersHelper.isDouble(locationSplit[0]) && NumbersHelper.isDouble(locationSplit[1])) {
            throw new LocationHelperException(ErrorMessages.LOCATION_HELPER_INVALID_FORMAT, locationString);
        }
        // create and return location object
        return new Location(NumbersHelper.getDoubleFromString(locationSplit[0]),
                NumbersHelper.getDoubleFromString(locationSplit[1]));
    }
}
