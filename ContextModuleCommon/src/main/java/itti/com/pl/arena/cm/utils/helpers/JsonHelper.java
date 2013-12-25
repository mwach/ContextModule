package itti.com.pl.arena.cm.utils.helpers;

import com.google.gson.Gson;

public final class JsonHelper {

	private static Gson gson = new Gson();

	private JsonHelper(){
	}

	public static Gson getJsonParser(){
		return gson;
	}

	public static String toJson(Object object) {
		return getJsonParser().toJson(object);
	}

	public static String getJsonValue(String jsonString, String jsonProperty){
		return getJsonValue(jsonString, jsonProperty, true);
	}

	public static String getJsonValue(String jsonString, String jsonProperty, boolean caseSensitive){

		//check if provided values have content
		if(!StringHelper.hasContent(jsonString) || !StringHelper.hasContent(jsonProperty)){
			return null;
		}
		//add quotation chars - don't want to find some 'random' string
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
				if(jsonValue.length() > 0 && jsonValue.startsWith("\""))
				{
					jsonValue = jsonValue.substring(1);
				}
				if(jsonValue.length() > 0 && jsonValue.endsWith("\""))
				{
					jsonValue = jsonValue.substring(0, jsonValue.length()-1);
				}
				return jsonValue;
			}
		}
		return null;
	}

	public static <T> T fromJson(String featureValue,
			Class<T> class1) {
		return gson.fromJson(featureValue, class1);
	}
}
