package itti.com.pl.arena.cm.utils.helper;

import itti.com.pl.arena.cm.Constants;
import itti.com.pl.arena.cm.exception.ErrorMessages;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.Arrays;

/**
 * Utility class for common IO operations
 * 
 * @author mawa
 * 
 */
public final class IOHelper {

    // size of the buffer used for reading data from streams
    private static final int INITIAL_DATA_BUFFER = 1024;
    // size of the data portion to be read at once during reading data from
    // streams
    private static final int DATA_CHUNK_SIZE = 128;

    // maximum time of sleeping between read data chunks from the input stream
    private static final int MAX_SLEEP_TIME = 2000;

    private IOHelper() {
    }

    /**
     * Read data from provided input stream into string Provided encoding is used to format binary data into string
     * 
     * @param inputStream
     *            input stream
     * @param encoding
     *            encoding
     * @return array of read bytes
     * @throws IOHelperException
     *             could not read all the data from provided stream
     */
    public static String readStreamData(InputStream inputStream, String encoding) throws IOHelperException {
        byte[] data = readStreamData(inputStream);
        String outputData = null;
        try {
            outputData = new String(data, encoding);
        } catch (UnsupportedEncodingException exc) {
            throw new IOHelperException(exc, ErrorMessages.IO_HELPER_INVLAID_ENCODING, encoding, exc.getLocalizedMessage());
        }
        return outputData;
    }

    /**
     * Read date from provided input stream into byte array
     * 
     * @param inputStream
     *            input stream
     * @return array of read bytes
     * @throws IOHelperException
     *             could not read all the data from provided stream
     */
    public static byte[] readStreamData(InputStream inputStream) throws IOHelperException {

        // sleep between two consecutive data reads
        int sleepTime = 250;

        int arraySize = INITIAL_DATA_BUFFER;
        int dataChunk = DATA_CHUNK_SIZE;
        int totalRead = 0;
        int totalSleep = 0;

        // initial byte array for read data
        byte[] response = new byte[arraySize];

        try {
            // if there is data available
            while (inputStream.available() > 0) {
                // read new chunk of it
                totalRead += inputStream.read(response, totalRead, dataChunk);

                // check, if array needs to be extended
                if ((totalRead + DATA_CHUNK_SIZE) > arraySize) {
                    arraySize += INITIAL_DATA_BUFFER;
                    response = Arrays.copyOf(response, arraySize);
                }
                if (inputStream.available() == 0 && totalSleep < MAX_SLEEP_TIME) {
                    try {
                        totalSleep += sleepTime;
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException exc) {
                        throw new IOHelperException(exc, ErrorMessages.IO_HELPER_INTERRUPT_EXCEPTION, exc.getLocalizedMessage());
                    }
                }
            }
        } catch (IOException | RuntimeException exc) {
            throw new IOHelperException(exc, ErrorMessages.IO_HELPER_IO_EXCEPTION, exc.getLocalizedMessage());
        }
        return Arrays.copyOf(response, totalRead);
    }

    /**
     * Attempts to close data stream
     * 
     * @param dataStream
     *            stream to be closed
     */
    public static void closeStream(Closeable dataStream) {
        if (dataStream != null) {
            try {
                dataStream.close();
            } catch (IOException exc) {
                // ignore potential exceptions
            }
        }
    }

    /**
     * Attempts to close Http connection
     * 
     * @param connection
     *            connection to be closed
     */
    public static final void closeConnection(HttpURLConnection connection) {
        if (connection != null) {
            try {
                connection.disconnect();
            } catch (RuntimeException exc) {
            }
        }
    }

    /**
     * Read text data from provided file using 'UTF-8' encoding
     * 
     * @param inputFileName
     *            name of the file to read text data from
     * @return content of the file
     * @throws IOHelperException
     *             could not read data from the file
     */
    public static String readDataFromFile(String inputFileName) throws IOHelperException {

        // file name not provided
        if (!StringHelper.hasContent(inputFileName)) {
            throw new IOHelperException(ErrorMessages.IO_HELPER_NO_INPUT_FILE_PROVIDED);
        }

        InputStream stream = null;
        String data = null;
        try {
            stream = new FileInputStream(inputFileName);
            data = readStreamData(stream, Constants.ENCODING);
        } catch (IOException exc) {
            throw new IOHelperException(exc, ErrorMessages.IO_HELPER_COULD_NOT_READ_DATA_FROM_FILE, inputFileName,
                    exc.getLocalizedMessage());
        } finally {
            closeStream(stream);
        }
        return data;
    }

    /**
     * Writes text data to file specified by its name using 'UTF-8' encoding
     * 
     * @param content content of the file
     * @param outputFileName
     *            name of the file to write text data to
     * @throws IOHelperException
     *             could not read data from the file
     */
    public static void saveDataToFile(String content, String outputFileName) throws IOHelperException {

        // file name not provided
        if (!StringHelper.hasContent(outputFileName)) {
            throw new IOHelperException(ErrorMessages.IO_HELPER_NO_OUTPUT_FILE_PROVIDED);
        }

        // nothing to write
        if (content == null) {
            throw new IOHelperException(ErrorMessages.IO_HELPER_NO_OUTPUT_DATA_PROVIDED, outputFileName);
        }

        OutputStream stream = null;
        try {
            stream = new FileOutputStream(outputFileName);
            stream.write(content.getBytes(Constants.ENCODING));
        } catch (IOException exc) {
            throw new IOHelperException(exc, ErrorMessages.IO_HELPER_COULD_NOT_WRITE_DATA_TO_FILE, outputFileName,
                    exc.getLocalizedMessage());
        } finally {
            closeStream(stream);
        }
    }
}
