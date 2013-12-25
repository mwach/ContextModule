package itti.com.pl.arena.cm.utils.helpers;

import itti.com.pl.arena.cm.Constants;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Arrays;

/**
 * Utility class for common IO operations
 * @author mawa
 *
 */
public final class IOHelper {

	// size of the buffer used for reading data from streams
	private static final int INITIAL_DATA_BUFFER = 1024;
	// size of the data portion to be read at once during reading data from streams
	private static final int DATA_CHUNK_SIZE = 128;

	//maximum time of sleeping between read data chunks from the input stream
	private static final int MAX_SLEEP_TIME = 2000;

	private IOHelper()
	{}

	public static String readStreamData(InputStream inputStream, String encoding) throws IOException{
		byte[] data = readStreamData(inputStream);
		return new String(data, encoding);
	}

	public static byte[] readStreamData(InputStream inputStream) throws IOException{

		int arraySize = INITIAL_DATA_BUFFER;
		int dataChunk = DATA_CHUNK_SIZE;
		int totalRead = 0;
		int totalSleep = 0;

		//initial byte array for read data
		byte[] response = new byte[arraySize];

		//if there is data available
		while(inputStream.available() > 0){
			//read new chunk of it
			totalRead += inputStream.read(response, totalRead, dataChunk);

			//check, if array needs to be extended
			if((totalRead + DATA_CHUNK_SIZE) > arraySize){
				arraySize += INITIAL_DATA_BUFFER;
				response = Arrays.copyOf(response, arraySize);
			}
			if(inputStream.available() == 0 && totalSleep < MAX_SLEEP_TIME){
				try {
					totalSleep +=250;
					Thread.sleep(250);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		return Arrays.copyOf(response, totalRead);
	}


	/**
	 * Attempts to close data stream
	 * @param dataStream stream to be closed
	 */
	public static void closeStream(Closeable dataStream){
		if(dataStream != null){
			try {
				dataStream.close();
			} catch (IOException exc) {
				LogHelper.exception(IOHelper.class, "closeStream", "Could not close data stream", exc);
			}
		}
	}

	/**
	 * Attempts to close Http connection
	 * @param connection connection to be closed
	 */
	public static final void closeConnection(HttpURLConnection connection){
		if(connection != null){
			try {
				connection.disconnect();
			} catch (RuntimeException exc) {
				LogHelper.exception(IOHelper.class, "closeConnection", "Could not close connection", exc);
			}
		}
	}


	public static String readDataFromFile(String inputFileName) throws IOHelperException {

		if(!StringHelper.hasContent(inputFileName)){
			throw new IOHelperException("Input file name not specified");
		}

		InputStream stream = null;
		String data = null;
		try{
			stream = new FileInputStream(inputFileName);
			data = readStreamData(stream, Constants.ENCODING);
		}catch(IOException exc){
			throw new IOHelperException(exc, "Could not read data from the file: %s. Details: %s", inputFileName, exc.getLocalizedMessage());
		}finally{
			closeStream(stream);
		}
		return data;
	}

	public static void saveDataToFile(String content, String outputFileName) throws IOHelperException {

		if(!StringHelper.hasContent(outputFileName)){
			throw new IOHelperException("Output file name not specified");
		}
		if(!StringHelper.hasContent(content)){
			throw new IOHelperException("Empty content specified for file '%s'", outputFileName);
		}

		OutputStream stream = null;
		try{
			stream = new FileOutputStream(outputFileName);
			stream.write(content.getBytes(Constants.ENCODING));
		}catch(IOException exc){
			throw new IOHelperException(exc, "Could not write data to the file: %s. Details: %s", outputFileName, exc.getLocalizedMessage());
		}finally{
			closeStream(stream);
		}
	}
}
