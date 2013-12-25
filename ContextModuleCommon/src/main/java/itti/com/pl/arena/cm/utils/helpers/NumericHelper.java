package itti.com.pl.arena.cm.utils.helpers;

public class NumericHelper {

	private NumericHelper()
	{}

	public static Double getDoubleFromString(String value){

		Double doubleVal = null;

		if(StringHelper.hasContent(value)){
			try{
				doubleVal = Double.parseDouble(value);
			}catch(RuntimeException exc){
				//could not parse int float, null will be returned
			}
		}
		return doubleVal;
	}

	public static boolean isNumber(String s){
		boolean valid = false;
		try{
			Integer.parseInt(s);
			valid = true;
		}catch(RuntimeException exc){}
		return valid;
	}

	public static int getIntValue(String value) {
		return Integer.parseInt(value);
	}

}
