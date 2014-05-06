package itti.com.pl.arena.cm.utils.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
    private LocationHelper() {
    }

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
        // 2 and 3 params are allowed: (x,y) or (x,y,z)
        if (locationSplit.length != 2 && locationSplit.length != 3) {
            throw new LocationHelperException(ErrorMessages.LOCATION_HELPER_INVALID_FORMAT, locationString);
        }
        // check if all are numbers
        for (int i = 0; i < locationSplit.length; i++) {
            if (!NumbersHelper.isDouble(locationSplit[i])) {
                throw new LocationHelperException(ErrorMessages.LOCATION_HELPER_INVALID_FORMAT, locationString);
            }
        }
        // create and return location object
        return new Location(NumbersHelper.getDoubleFromString(locationSplit[0]),
                NumbersHelper.getDoubleFromString(locationSplit[1]), 0,
                locationSplit.length == 3 ? NumbersHelper.getDoubleFromString(locationSplit[2]) : 0);
    }

    public static String createStringFromLocation(Location location) {
        return String.format("%s, %s, %s", location.getLongitude(), location.getLatitude(), location.getAltitude());
    }

    public static String[] createStringsFromLocations(Location[] locations) {
        if (locations == null) {
            return null;
        }
        String[] locationStrings = new String[locations.length];
        for (int i = 0; i < locations.length; i++) {
            locationStrings[i] = createStringFromLocation(locations[i]);
        }
        return locationStrings;
    }

    public static List<String> getStringsFromLocations(Collection<Location> locations) {
        if (locations == null) {
            return null;
        }
        List<String> locationStrings = new ArrayList<>();
        for (Location location : locations) {
            locationStrings.add(createStringFromLocation(location));
        }
        return locationStrings;
    }

    public static Location[] getLocationsFromStrings(String[] locationStrings) throws LocationHelperException {
        if (locationStrings == null) {
            return null;
        }
        Location[] locations = new Location[locationStrings.length];
        for (int i = 0; i < locationStrings.length; i++) {
            locations[i] = getLocationFromString(locationStrings[i]);
        }
        return locations;
    }

    public static List<Location> getLocationsFromStrings(List<String> locationStrings) throws LocationHelperException {
        if (locationStrings == null) {
            return null;
        }
        List<Location> locations = new ArrayList<>();
        for (String locationString : locationStrings) {
            locations.add(getLocationFromString(locationString));
        }
        return locations;
    }

    public static double calculateDistance(Location locationOne, Location locationTwo) {
        return HaversineAlgorithm.HaversineInM(locationOne.getLatitude(), locationOne.getLongitude(), locationTwo.getLatitude(),
                locationTwo.getLongitude());
    }

    /**
     * Calculates angle between two locations stored as {@link Location} objects. Calculation was implemented based on
     * instructions from: http://stackoverflow.com/questions/7586063
     * 
     * @param baseLocation
     *            first coordinate
     * @param referenceLocation
     *            second coordinate
     * @return angle between two locations measured radians
     */
    public static Double calculateAngle(Location baseLocation, Location referenceLocation) {

        double deltaLongitude = referenceLocation.getLongitude() - baseLocation.getLongitude();
        double deltaLatitude = referenceLocation.getLatitude() - baseLocation.getLatitude();

        double angle = Math.atan2(deltaLatitude, deltaLongitude) * 180 / Math.PI;
        return angle;
    }

    private static class HaversineAlgorithm {

        static final double _eQuatorialEarthRadius = 6378.1370D;
        static final double _d2r = (Math.PI / 180D);

        public static int HaversineInM(double lat1, double long1, double lat2, double long2) {
            return (int) (1000D * HaversineInKM(lat1, long1, lat2, long2));
        }

        public static double HaversineInKM(double lat1, double long1, double lat2, double long2) {
            double dlong = (long2 - long1) * _d2r;
            double dlat = (lat2 - lat1) * _d2r;
            double a = Math.pow(Math.sin(dlat / 2D), 2D) + Math.cos(lat1 * _d2r) * Math.cos(lat2 * _d2r)
                    * Math.pow(Math.sin(dlong / 2D), 2D);
            double c = 2D * Math.atan2(Math.sqrt(a), Math.sqrt(1D - a));
            double d = _eQuatorialEarthRadius * c;

            return d;
        }

    }
}
