package itti.com.pl.arena.cm.geoportal;

import itti.com.pl.arena.cm.ContextModuleException;

/**
 * Geoportal exception
 * Thrown by geoportal module
 * @author mawa
 *
 */
public class GeoportalException extends ContextModuleException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GeoportalException(String message) {
		super(message);
	}

	public GeoportalException(GeoportalExceptionCodes exceptionCode) {
		super(exceptionCode.getErrorMsg());
	}

	public GeoportalException(String message, Object[] args) {
		super(message, args);
	}

	public GeoportalException(String message, Throwable throwables, Object... args) {
		super(message, throwables, args);
	}

	public enum GeoportalExceptionCodes {

		VALIDATION_SERVICE_NOT_PROVIDED(100, "Geoportal service not specified"),
		VALIDATION_REQUEST_DATA_NOT_PROVIDED(101, "Request data not provided"),
		VALIDATION_NULL_START_LOCATION(102, "StartLocation not provided"),
		VALIDATION_NULL_END_LOCATION(103, "StartLocation not provided"),
		
		HELPER_SERIALIZE_NULL_OBJECT_PROVIDED(200, "Could not serialize null object into valid JSON request"),
		HELPER_DESERIALIZE_NULL_JSON_PROVIDED(201, "Could not deserialize null string into valid object"),
		HELPER_DESERIALIZE_INVALID_JSON_PROVIDED(202, "Could not deserialize given string into valid object"),
		HELPER_REQUEST_NULL_OBJECT_PROVIDED(203, "Could not create valid request from null object"),

		API_GET_FAILED(300, "Could not retrieve data from the Geoportal service"),

		;

		private int codeId;
		private String errorMsg;

		private GeoportalExceptionCodes(int codeId, String errorMsg){
			this.codeId = codeId;
			this.errorMsg = errorMsg;
		}

		public int getCodeId(){
			return codeId;
		}
		public String getErrorMsg(){
			return errorMsg;
		}
	}


}
