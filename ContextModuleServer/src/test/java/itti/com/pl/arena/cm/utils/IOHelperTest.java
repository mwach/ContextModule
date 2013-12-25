package itti.com.pl.arena.cm.utils;

import itti.com.pl.arena.cm.utils.helpers.IOHelper;
import itti.com.pl.arena.cm.utils.helpers.StringHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for the {@link IOHelper}
 * @author mawa
 *
 */
public class IOHelperTest {

	private Random random = new Random();

	@Test
	public void testReadStreamBinaryData() throws IOException{
		//lengths of binary arrays to be sent/receive using IOHelper
		int[] lengthsToCheck = new int[]{0, 1, 12, 16, 32, 33, 64, 65, 512, 1024, 1025, 3343, random.nextInt(100000), random.nextInt(100000)};

		//for each length
		for (int lenght : lengthsToCheck) {

			//generate random input data
			byte[] inputData = getDummyByteArray(lenght);
			DummyInputStream dis = new DummyInputStream(inputData);
			//read data using tested class
			byte[] outputData = IOHelper.readStreamData(dis);
			//compare arrays
			Assert.assertArrayEquals(inputData, outputData);
		}
	}

	@Test
	public void testReadStreamStringData() throws IOException{

		//strings to be used during the test
		String[] stringsToCheck = new String[]{"", "some text", UUID.randomUUID().toString(), getDummyString(64), getDummyString(1024), getDummyString(1300)};

		for (String string : stringsToCheck) {

			//for each tested string generate dummy Input Stream
			DummyInputStream dis = new DummyInputStream(string);
			//get data using tested class
			String response = new String(IOHelper.readStreamData(dis));
			//compare strings
			Assert.assertEquals(string, response);
		}
	}

	/**
	 * Generates random string
	 * @param length length of the string
	 * @return random string
	 */
	private String getDummyString(int length) {
		StringBuilder sb = new StringBuilder();
		for(int i=0 ; i<length ; i++){
			sb.append((char)(32 + random.nextInt(30)));
		}
		return sb.toString();
	}

	/**
	 * Generates random byte array
	 * @param length length of the array 
	 * @return random byte array
	 */
	private byte[] getDummyByteArray(int length) {
		byte[] bytes = new byte[length];
		//generate random data
		random.nextBytes(bytes);
		//search for, and replace EOM sign
		for(int i=0 ; i<bytes.length ; i++){
			if(bytes[i] == -1)
			{
				bytes[i] = 1;
			}
		}
		return bytes;
	}


	/**
	 * Test class mimicking Input Stream data source
	 * @author mawa
	 *
	 */
	private static class DummyInputStream extends InputStream{

		//data to be sent
		private byte[] dummyData = null;
		//length of data
		private int dummyDataLen = 0;
		//data already read
		private int dataRead = 0;

		/**
		 * Constructor
		 * @param dummyData data to be sent
		 */
		public DummyInputStream(byte[] dummyData){
			this.dummyData = dummyData;
			this.dummyDataLen = this.dummyData.length;
		}

		/**
		 * Constructor
		 * @param content content to be sent
		 */
		public DummyInputStream(String content){
			this(StringHelper.getUtf8ByteArray(content));
		}

		@Override
		public int read() throws IOException {

			//reach end of array
			if(dataRead >= dummyDataLen)
			{
				return -1;
			//data still available
			}else{
				return dummyData[dataRead++];
			}
		}
		
		@Override
		public int available() throws IOException {

			return dummyDataLen - dataRead;
		}

		@Override
		public synchronized void reset() throws IOException {
			dataRead = 0;
		}
	}
}
