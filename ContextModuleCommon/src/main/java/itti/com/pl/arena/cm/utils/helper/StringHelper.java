package itti.com.pl.arena.cm.utils.helper;

import itti.com.pl.arena.cm.ErrorMessages;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;

/**
 * String utilities
 * 
 * @author cm-admin
 * 
 */
public final class StringHelper {

    /**
     * Encoding used by some of the methods
     */
    private static final String UTF8_CHARSET_NAME = "UTF-8";

    private StringHelper() {
    }

    /**
     * Returns true, if given string has some non-whitespace content
     * 
     * @param string
     *            string to be checked
     * @return true, if string has some content,false otherwise
     */
    public static boolean hasContent(String string) {
        return string != null && string.trim().length() > 0;
    }

    /**
     * Converts given UTF-8 string into byte array
     * 
     * @param string
     *            UTF-8 string
     * @return byte array created from given string, or empty array if null or empty string was provided
     */
    public static byte[] getUtf8ByteArray(String string) {
        return hasContent(string) ? string.getBytes(Charset.forName(UTF8_CHARSET_NAME)) : new byte[0];
    }

    /**
     * Converts given byte array into UTF-8 string
     * 
     * @param array
     *            byte array to be converted into string
     * @return UTF-8 string created from given array, or NULL if NULL array was provided
     */
    public static String toUtf8String(byte[] array) {
        return array != null ? new String(array, Charset.forName(UTF8_CHARSET_NAME)) : null;
    }

    /**
     * Tries to encode given URL into web-ready format
     * 
     * @param url
     *            URL to be encoded
     * @return encoded URL
     * @throws StringHelperException
     *             could not encode provided URL
     */
    public static String encodeUrl(String url) throws StringHelperException {
        String encodedUrl = null;
        try {
            encodedUrl = URLEncoder.encode(url, UTF8_CHARSET_NAME);
        } catch (UnsupportedEncodingException | RuntimeException exc) {
            throw new StringHelperException(exc, ErrorMessages.STRING_HELPER_CANNOT_ENCODE, url, exc.getLocalizedMessage());
        }
        return encodedUrl;
    }

    /**
     * Compares two string
     * 
     * @param stringOne
     *            first string
     * @param stringTwo
     *            second string
     * @return true, if strings are equal, false otherwise
     */
    public static boolean equals(String stringOne, String stringTwo) {

        return equals(stringOne, stringTwo, false);
    }

    /**
     * Compares two string ignoring case-sensitivity
     * 
     * @param stringOne
     *            first string
     * @param stringTwo
     *            second string
     * @return true, if strings are equal, false otherwise
     */
    public static boolean equalsIgnoreCase(String stringOne, String stringTwo) {

        return equals(stringOne, stringTwo, true);
    }

    /**
     * Compares two string
     * 
     * @param stringOne
     *            first string
     * @param stringTwo
     *            second string
     * @param ignoreCase
     *            true, if case-sensitivity should be ignored, false otherwise
     * @return true, if strings are equal, false otherwise
     */
    private static boolean equals(String stringOne, String stringTwo, boolean ignoreCase) {

        if (stringOne == null && stringTwo == null) {
            return true;
        }
        if (stringOne == null || stringTwo == null) {
            return false;
        }
        if (ignoreCase) {
            return stringOne.equalsIgnoreCase(stringTwo);
        }
        return stringOne.equals(stringTwo);
    }
}
