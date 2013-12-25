package itti.com.pl.arena.cm.utils.helpers;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;

public final class StringHelper {

	private static final String UTF8_CHARSET_NAME = "UTF-8";

	private StringHelper()
	{}

	public static boolean hasContent(String message){
		return message != null && message.length() > 0;
	}

	public static byte[] getUtf8ByteArray(String message){
		return hasContent(message) ? 
				message.getBytes(Charset.forName(UTF8_CHARSET_NAME)) :
				new byte[0]
		;
	}

	public static String toUtf8String(byte[] message){
		return message != null ? 
				new String(message, Charset.forName(UTF8_CHARSET_NAME)) :
				null
		;
	}

	public static String encodeUrl(String url){
		String encodedUrl = null;
		if (hasContent(url)){
			try{
				encodedUrl = URLEncoder.encode(url, UTF8_CHARSET_NAME);
			}catch(UnsupportedEncodingException | RuntimeException exc){
				LogHelper.warning(StringHelper.class, "encodeUrl", 
						"Could not encode string '%s' using default charsed. Msg: %s",
						url, exc.getLocalizedMessage());
			}
		}
		return encodedUrl;
	}

	public static boolean equals(String stringOne, String stringTwo) {

		return equals(stringOne, stringTwo, false);
	}

	public static boolean equalsIgnoreCase(String stringOne, String stringTwo) {

		return equals(stringOne, stringTwo, true);
	}

	private static boolean equals(String stringOne, String stringTwo, boolean ignoreCase) {

		if(stringOne == null && stringTwo == null){
			return true;
		}
		if(stringOne == null || stringTwo == null){
			return false;
		}
		if(ignoreCase){
			return stringOne.equalsIgnoreCase(stringTwo);
		}
		return stringOne.equals(stringTwo);
	}

	public static String toString(Object response) {
		return (response == null) ? "(null)" : String.valueOf(response);
	}
}
