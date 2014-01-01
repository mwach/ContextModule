package itti.com.pl.arena.cm.utils.helpers;

/**
 * Numbers utilities
 * 
 * @author cm-admin
 * 
 */
public class NumbersHelper {

    private NumbersHelper() {
    }

    /**
     * Checks, if given value represents valid double value
     * 
     * @param value
     *            string to check
     * @return true, if string represents valid double, false otherwise
     */
    public static boolean isDouble(String value) {

	if (value == null) {
	    return false;
	}

	boolean valid = false;
	try {
	    Double.parseDouble(value);
	    valid = true;
	} catch (RuntimeException exc) {
	    // could not parse into double, false will be returned
	}
	return valid;
    }

    /**
     * Tries to parse given string into double
     * 
     * @param value
     *            string representation of double value
     * @return double value, or NULL if value cannot be parsed
     */
    public static Double getDoubleFromString(String value) {

	if (value == null) {
	    return null;
	}
	Double doubleVal = null;

	try {
	    doubleVal = Double.parseDouble(value);
	} catch (RuntimeException exc) {
	    // could not parse into float, null will be returned
	}
	return doubleVal;
    }

    /**
     * Checks, if given value represents valid integer value
     * 
     * @param value
     *            string to check
     * @return true, if string represents valid integer, false otherwise
     */
    public static boolean isInteger(String value) {
	boolean valid = false;
	try {
	    Integer.parseInt(value);
	    valid = true;
	} catch (RuntimeException exc) {
	    // could not parse into int, false will be returned
	}
	return valid;
    }

    /**
     * Tries to parse given string into integer
     * 
     * @param value
     *            string representation of int value
     * @return int value, or NULL if value cannot be parsed
     */
    public static Integer getIntegerFromString(String value) {

	if (value == null) {
	    return null;
	}

	Integer intVal = null;

	try {
	    intVal = Integer.parseInt(value);
	} catch (RuntimeException exc) {
	    // could not parse into int, null will be returned
	}
	return intVal;
    }
}
