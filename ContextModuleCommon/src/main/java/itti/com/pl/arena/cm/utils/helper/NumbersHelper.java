package itti.com.pl.arena.cm.utils.helper;

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
     * Tries to parse given string into double
     * if parser fail, then default value will be returned
     * 
     * @param value
     *            string representation of double value
     *            @param defaultValue value to be returned in case of parser failure
     * @return double value, or default one in case of failure
     */
    public static double getDoubleFromString(String value, double defaultValue) {
        Double result = getDoubleFromString(value);
        return result == null ? defaultValue  : result.doubleValue();
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

    /**
     * Parses provided string into array of double values
     * 
     * @param valueString
     *            input string containing one or more double values
     * @param delimiter
     *            double values delimiter
     * @return array of double values
     */
    public static Double[] getDoublesFromString(String valueString, String delimiter) {
        // first, check if not null array was provided
        if (valueString == null) {
            return null;
        }
        // split string into array of tokens
        String[] valuesArray = valueString.split(delimiter);
        Double[] output = new Double[valuesArray.length];
        // for each of tokens, try to parse into double
        for (int i = 0; i < valuesArray.length; i++) {
            output[i] = getDoubleFromString(valuesArray[i]);
        }
        return output;
    }

    public static boolean equals(Double valueA, Double valueB){
        if(valueA == null && valueB == null){
            return true;
        }else if(valueA == null || valueB == null){
            return false;
        }
        double delta = Math.abs(valueA - valueB);
        return  delta < 0.001;
    }

    public static boolean equals(Integer valueA, Integer valueB){
        if(valueA == null && valueB == null){
            return true;
        }else if(valueA == null || valueB == null){
            return false;
        }
        return valueA.intValue() == valueB.intValue();
    }

    public static boolean equals(Long valueA, Long valueB){
        if(valueA == null && valueB == null){
            return true;
        }else if(valueA == null || valueB == null){
            return false;
        }
        return valueA.longValue() == valueB.longValue();
    }

    public static Double changePrecision(Double value){
        if(value == null){
            return value;
        }
        long longValue = value.longValue();
        long fractionValue = (long) ((value - longValue) * Math.pow(10, 3));
        return (double)longValue + (((double)fractionValue) / Math.pow(10, 3));
    }
}
