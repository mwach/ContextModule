package itti.com.pl.arena.cm.utils.helpers;

import com.google.gson.Gson;

/**
 * Helper for working with JSON-formatted objects
 * @author cm-admin
 *
 */
public final class JsonHelper {

	// GSON parser for JSON
	private static Gson gson = new Gson();

	private static Gson getJsonParser(){
		return gson;
	}

	private JsonHelper(){
	}

	/**
	 * Serializes given object into JSON string
	 * @param object input object
	 * @return JSON representation of provided object
	 */
	public static String toJson(Object object) {
		return getJsonParser().toJson(object);
	}

	/**
	 * De-serializes given JSON string into object
	 * @param jsonSerializedObject input object
	 * @param objectClass base class
	 * @return object created from provided JSON string
	 */
	public static <T> T fromJson(String jsonSerializedObject,
			Class<T> objectClass) {
		return getJsonParser().fromJson(jsonSerializedObject, objectClass);
	}

	/**
	 * Returns value specified by the key from provided JSON string
	 * Case-sensitivity is ignored
	 * @param jsonString JSON sting
	 * @param jsonProperty name of the property
	 * @return value of the property
	 */
	public static String getJsonValue(String jsonString, String jsonProperty){
		return getJsonValue(jsonString, jsonProperty, true);
	}

	/**
	 * Returns value specified by the key from provided JSON string
	 * @param jsonString JSON sting
	 * @param jsonProperty name of the property
	 * @param caseSensitive true, if case-sensitivity should not be ignored, false otherwise
	 * @return value of the property
	 */
	public static String getJsonValue(String jsonString, String jsonProperty, boolean caseSensitive){

		//check if provided values have content
		if(!StringHelper.hasContent(jsonString) || !StringHelper.hasContent(jsonProperty)){
			return null;
		}

		//add quotation chars - don't want to find some 'in-the-middle' string
		String propertyName = String.format("\"%s\"", caseSensitive ? jsonProperty : jsonProperty.toLowerCase());
		String wholeString = caseSensitive ? jsonString : jsonString.toLowerCase();

		int keyPos = wholeString.indexOf(propertyName);
		//if found:
		if(keyPos != -1){
			//search for ':' char (beginning of the value)
			int valueStartPos = jsonString.indexOf(":", keyPos) + 1;
			//end char position: second quotation char
			int valueEndPos = jsonString.indexOf(",", valueStartPos);
			if(valueStartPos > 0 && valueEndPos > valueStartPos){
				String jsonValue = jsonString.substring(valueStartPos, valueEndPos);

				//remove quotation marks
				if(jsonValue.length() > 0 && jsonValue.startsWith("\"")){
					jsonValue = jsonValue.substring(1);
				}
				if(jsonValue.length() > 0 && jsonValue.endsWith("\"")){
					jsonValue = jsonValue.substring(0, jsonValue.length()-1);
				}
				return jsonValue;
			}
		}
		return null;
	}
}
