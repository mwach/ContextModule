/**
 * 
 */
package com.safran.arena.stubs;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.safran.arena.DataProducerInterface;
import com.safran.arena.impl.Client;
import com.safran.arena.impl.ModuleImpl;

import eu.arena_fp7._1.AbstractDataFusionType;
import eu.arena_fp7._1.ObjectFactory;

/**
 * This stub acts like a dummy data provider. It registers, unregisters, and
 * sends dummy data.
 * <p>
 * If it is called with one or more command line parameters, it tries to
 * understand them as directories containning XML files, read them, build
 * {@link AbstractDataFusionType} object from them, and push them on the
 * platform.
 * 
 * @author F270116
 * 
 */
public class DataProviderStubService extends ModuleImpl implements
		DataProducerInterface {

	private Client _client;
	private boolean _scheduling = true;

	public DataProviderStubService(String moduleName) {
		super(moduleName);
	}

	/**
	 * Initializations
	 */
	public void init() {
		_client = new Client();
		_client.connectToServer();

	}

	/**
	 * This module is only a Data Provider. This method registers this module as
	 * such.
	 */
	public void registerService() {
		_client.registerModule(this);
		_client.registerModuleAsDataProvider(this);
	}

	public void unregisterService() {
		// _client.unregisterModule(this);
		_client.stop();
	}

	private static class ByDate implements Comparator<AbstractDataFusionType> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(AbstractDataFusionType o1, AbstractDataFusionType o2) {
			Long leftTS = o1.getTimestamp();
			if (leftTS == null) {
				leftTS = o1.getStartValidityPeriod();
			}
			Long rightTS = o2.getTimestamp();
			if (rightTS == null) {
				rightTS = o2.getStartValidityPeriod();
			}

			if (leftTS == null) {
				if (rightTS == null) {
					return 0;
				} else {
					return 1;
				}
			}
			if (rightTS == null) {
				return -1;
			}

			return leftTS.compareTo(rightTS);
		}

	}

	/**
	 * Method to push a directory content on the Integration Platform. Current
	 * limitation : no time scheduling is done !
	 * 
	 * @param directory
	 */
	public void flushDirectory(File directory) {
		Long formerTS = 0l;
		Long newTS = 0l;
		JAXBContext jaxbContext;
		Unmarshaller unmarshaller;
		try {
			jaxbContext = JAXBContext
					.newInstance("eu.arena_fp7._1:org.uncertml._2");
			unmarshaller = jaxbContext.createUnmarshaller();
		} catch (JAXBException e1) {
			e1.printStackTrace();
			return;
		}

		File list[] = directory.listFiles().clone();
		Arrays.sort(list, new Comparator<File>() {

			@Override
			public int compare(File left, File right) {

				return left.getName().compareTo(right.getName());
			}

		});
		List<AbstractDataFusionType> objectsToSend = new ArrayList<AbstractDataFusionType>();

		for (File file : list) {
			try {
				JAXBElement<?> je = (JAXBElement<?>) unmarshaller
						.unmarshal(file);
				Object o = je.getValue();
				if (o instanceof AbstractDataFusionType) {
					AbstractDataFusionType data = (AbstractDataFusionType) o;
					if (data.getDataSourceId() == null
							|| data.getDataSourceId().isEmpty()) {
						data.setDataSourceId(getModuleName());
					}
					objectsToSend.add(data);

				} else {
					System.err.println("Error in file " + file
							+ " : object is not AbstractDataFusionType");
				}

			} catch (JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (_scheduling) {
			Collections.sort(objectsToSend, new ByDate());
		}

		for (AbstractDataFusionType data : objectsToSend) {
			if (_scheduling) {
				if (newTS == 0) {
					formerTS = data.getTimestamp();
					newTS = formerTS;
				} else {
					newTS = data.getTimestamp();
					if (newTS > formerTS) {
						long delta = newTS - formerTS;
						if (delta > 5000) {
							System.err
									.println("More than 5s between two messages, shortening");
							delta = 5000;
						}
						try {
							synchronized (this) {
								this.wait(delta);
							}
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						formerTS = newTS;
					}
				}
			}
			_client.publish(this.getModuleName(), data);

		}

	}

	/**
	 * @return true if scheduling is set
	 */
	public boolean isScheduling() {
		return _scheduling;
	}

	/**
	 * @param scheduling
	 *            true if the scheduling has to be handled
	 */
	public void setScheduling(boolean scheduling) {
		_scheduling = scheduling;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ObjectFactory factory = new ObjectFactory();
		ArrayList<String> argsList = new ArrayList<String>(Arrays.asList(args));

		DataProviderStubService stub = new DataProviderStubService(
				"DataProvider Stub " + Math.random());
		stub.init();
		stub.registerService();
		if (argsList.contains("--noScheduling")) {
			stub.setScheduling(false);
		}

		try {
			if (args.length > 0) {
				for (String directoryPath : args) {
					File directory = new File(directoryPath);
					if (!directory.canRead()) {
						System.err.println("Cannot read directory "
								+ directoryPath);
					} else if (!directory.isDirectory()) {
						System.err.println("Not a directory directory "
								+ directoryPath);
					} else {
						stub.flushDirectory(directory);
					}

				}
			} else {

				for (int i = 0; i < 10; i++) {
					eu.arena_fp7._1.Object o = factory.createObject();
					o.setId("toto" + i);
					stub._client.publish(stub.getClass().toString(), o);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} finally {
			stub.unregisterService();
		}
	}

}
