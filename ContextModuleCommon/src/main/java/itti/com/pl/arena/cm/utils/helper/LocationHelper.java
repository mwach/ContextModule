package itti.com.pl.arena.cm.utils.helper;

import itti.com.pl.arena.cm.dto.Location;
import itti.com.pl.arena.cm.exception.ErrorMessages;

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
        //2 and 3 params are allowed: (x,y) or (x,y,z)
        if (locationSplit.length != 2 && locationSplit.length != 3) {
            throw new LocationHelperException(ErrorMessages.LOCATION_HELPER_INVALID_FORMAT, locationString);
        }
        //check if all are numbers
        for(int i=0 ; i<locationSplit.length ; i++){
            if (!NumbersHelper.isDouble(locationSplit[i])) {
                throw new LocationHelperException(ErrorMessages.LOCATION_HELPER_INVALID_FORMAT, locationString);
            }
        }
        // create and return location object
        return new Location(
                NumbersHelper.getDoubleFromString(locationSplit[0]),
                NumbersHelper.getDoubleFromString(locationSplit[1]),
                0,
                locationSplit.length == 3 ? NumbersHelper.getDoubleFromString(locationSplit[2]): 0
        );
    }


    public static String createStringFromLocation(Location location) {
        return String.format("%s, %s, %s", location.getLongitude(), location.getLatitude(), location.getAltitude());
    }

    public static String[] createStringsFromLocations(Location[] locations) {
        if(locations == null){
            return null;
        }
        String[] locationStrings = new String[locations.length];
        for (int i=0 ; i<locations.length ; i++) {
            locationStrings[i] = createStringFromLocation(locations[i]);
        }
        return locationStrings;
    }

    public static Location[] getLocationsFromStrings(String[] locationStrings) throws LocationHelperException {
        if(locationStrings == null){
            return null;
        }
        Location[] locations = new Location[locationStrings.length];
        for (int i=0 ; i<locationStrings.length ; i++) {
            locations[i] = getLocationFromString(locationStrings[i]);
        }
        return locations;
    }

}
