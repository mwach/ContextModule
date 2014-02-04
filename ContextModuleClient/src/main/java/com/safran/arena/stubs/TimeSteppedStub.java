/**
 * 
 */
package com.safran.arena.stubs;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.RestoreAction;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.safran.arena.DataConsumerInterface;
import com.safran.arena.DataProducerInterface;
import com.safran.arena.TimeSteppedModuleInterface;
import com.safran.arena.impl.ArgParser;
import com.safran.arena.impl.Client;
import com.safran.arena.impl.ModuleImpl;
import com.safran.arena.impl.SimpleMessageFilter;

import eu.arena_fp7._1.AbstractDataFusionType;
import eu.arena_fp7._1.FinishedUpTo;
import eu.arena_fp7._1.ObjectFactory;

/**
 * This stub acts like a dummy time stepped module. It registers, unregisters,
 * and sends dummy data according to a time step order.
 * <p>
 * If it is called with one or more command line parameters, it tries to
 * understand them as directories containning XML files, read them, build
 * {@link AbstractDataFusionType} object from them, and push them on the
 * platform.
 * 
 * @author F270116
 * 
 */
public class TimeSteppedStub extends ModuleImpl implements
		DataProducerInterface, TimeSteppedModuleInterface,
		DataConsumerInterface, Runnable {

	private Client _client;
	private Long _targetDate = 0l;
	private long _startDate;
	private Object _dateLock = new Object();
	ObjectFactory factory = new ObjectFactory();

	private Map<String, Long> _sources = new HashMap<String, Long>();
	private ArrayList<AbstractDataFusionType> objectsToSend = new ArrayList<AbstractDataFusionType>();
	private SimpleMessageFilter _filter = new SimpleMessageFilter(false);
	/**
	 * This boolean serves to ensure that the FinishedUpTo message is sent.
	 */
	private boolean _updatedTS = false;
	/**
	 * This boolean marks that the module is to be restarted
	 */
	private boolean _restarted = true;

	/**
	 * Constructor.
	 * 
	 * @param moduleName
	 *            Name of the module, shall be unique in the system.
	 */
	public TimeSteppedStub(String moduleName) {
		super(moduleName);
	}

	/**
	 * Initializations
	 */
	public void init(Client client) {
		_client = client;

	}

	/**
	 * This module is only a Data Provider. This method registers this module as
	 * such.
	 */
	public void registerService() {
		_filter.addClass(FinishedUpTo.class);
		_client.registerModule(this);
		_client.registerModuleAsDataProvider(this);
		_client.registerModuleAsTimeStepped(this);
		_client.registerModuleAsDataConsumer(this, _filter);
	}

	public void unregisterService() {
		// _client.unregisterModule(this);
		_client.stop();
	}

	/**
	 * Comparator to sort the list of messages to send.
	 * 
	 * @author F270116
	 * 
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.safran.arena.impl.ModuleImpl#timeStep(long)
	 */
	@Override
	public void timeStep(long timeStamp) {
		System.out.println(getModuleName()+ " received timeStep " + timeStamp);
		synchronized (_dateLock) {
			_targetDate = timeStamp;
			_updatedTS = true;
			_dateLock.notifyAll();

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.safran.arena.impl.ModuleImpl#start(long)
	 */
	@Override
	public void start(long timeStamp) {
		System.out.println(getModuleName() + " received Start " + timeStamp);
		super.start(timeStamp);
		synchronized (_dateLock) {
			_targetDate = timeStamp;
			_startDate = timeStamp;
			_restarted = true;
			synchronized (_sources) {
				_sources.notifyAll();
			}
			_dateLock.notifyAll();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.safran.arena.impl.ModuleImpl#onDataChanged(java.lang.Class,
	 * java.lang.String, eu.arena_fp7._1.AbstractDataFusionType)
	 */
	@Override
	public void onDataChanged(Class<? extends AbstractDataFusionType> dataType,
			String dataSourceId, AbstractDataFusionType data) {

		super.onDataChanged(dataType, dataSourceId, data);
		synchronized (_sources) {
			System.out.println(getModuleName() + " received finished up to "
					+ data.getEndValidityPeriod() + " from "
					+ data.getDataSourceId());
			_sources.put(data.getDataSourceId(), data.getEndValidityPeriod());
			_sources.notifyAll();
		}

	}

	/**
	 * Fill the list of messages to send using the XML files stored in the given
	 * directory.
	 * 
	 * @param dir
	 */
	public void fillFromDir(String dir) {
		File directory = new File(dir);
		if (!directory.canRead()) {
			System.err.println("Cannot read directory " + dir);
			return;
		} else if (!directory.isDirectory()) {
			System.err.println("Not a directory directory " + dir);
			return;
		}

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
					if (data.getEndValidityPeriod() == null) {
						data.setEndValidityPeriod(data.getTimestamp());
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

		Collections.sort(objectsToSend, new ByDate());
		if (!objectsToSend.isEmpty()) {
			System.out.println("First timeStamp = "
					+ objectsToSend.get(0).getTimestamp());
			System.out.println("Last timeStamp = "
					+ objectsToSend.get(objectsToSend.size() - 1)
							.getTimestamp());
		}

	}

	/**
	 * Fills the list of messages to send using random generated messages.
	 * 
	 * @param start
	 *            time stamp of the first message in the list.
	 * @param finish
	 *            time stamp limit for the list.
	 * @param step
	 *            time in ms between two generated messages.
	 */
	public void createRandom(long start, long finish, long step) {
		if (step <= 0) {
			throw new IllegalArgumentException(
					"Step should be strictly positive");
		}
		int i = 0;
		for (long ts = start; ts < finish; ts += step) {
			i++;
			eu.arena_fp7._1.Object o = factory.createObject();
			o.setId(getModuleName() + "o" + i);
			o.setDataSourceId(getModuleName());
			o.setTimestamp(ts);
			o.setEndValidityPeriod(ts);
			o.setStartValidityPeriod(ts - step);

			objectsToSend.add(o);
			// no need to sort, its sorted by construction
		}
	}

	/**
	 * Seeks the index of the last message before the timeStamp
	 * 
	 * @param timeStamp
	 * @return
	 */
	private int seekIndexOf(long timeStamp) {
		int idx = -1;

		for (int i = 0; (i < objectsToSend.size())
				&& (objectsToSend.get(i).getEndValidityPeriod() < timeStamp); i++) {
			idx = i;
		}

		return idx;
	}

	/**
	 * Run: - use internal list of messages (AbstractDataFusionType objects) -
	 * 
	 */
	public void run() {
		try {
			ObjectFactory factory = new ObjectFactory();
			FinishedUpTo finishMsg = factory.createFinishedUpTo();
			finishMsg.setDataSourceId(getModuleName());
			finishMsg.setId(getModuleName() + " CDate");

			int currentIndex = 0;

			boolean cont = true;
			boolean allDataReceived = false;

			while (cont) {
				long usedTarget = _targetDate;
				// fetch if reset
				// if reset, seek right point in array for messages, and reset
				// variables
				synchronized (_dateLock) {
					if (_restarted) {
						currentIndex = seekIndexOf(_startDate);
						if (currentIndex < 0) {
							System.err
									.println(getModuleName()
											+ "Received target date before first message "
											+ _startDate);
						} else if (currentIndex >= objectsToSend.size() - 1) {
							System.err
									.println(getModuleName()
											+ "Received target date after last message "
											+ _startDate);
						}
						_restarted = false;
						usedTarget = _targetDate;
					}
					if (_updatedTS) {
						usedTarget = _targetDate;
						_updatedTS = false;
					}
				}

				// wait for source readiness
				synchronized (_sources) {

					do {
						allDataReceived = true;
						for (Entry<String, Long> e : _sources.entrySet()) {
							if (e.getValue() < usedTarget) {
								allDataReceived = false;
								System.out.println(getModuleName() + " at "
										+ usedTarget + " waiting for "
										+ e.getKey() + " at " + e.getValue());
							}
						}
						if (!allDataReceived) {
							try {
								_sources.wait();
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							}
						}
					} while (!allDataReceived && !_restarted);

				}

				if (_restarted) {
					continue;
				}
				// time step : send messages up to target date

				currentIndex++;

				while (currentIndex >= 0
						&& currentIndex < objectsToSend.size()
						&& objectsToSend.get(currentIndex)
								.getEndValidityPeriod() < usedTarget) {

					// publish message
					_client.publish(getModuleName(),
							objectsToSend.get(currentIndex));
					// wait a little to simulate a computing time
					long delta = 1000 + new Double(Math.random() * 2000)
							.longValue();
					try {
						synchronized (this) {
							this.wait(delta);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					currentIndex++;
				}

				// once target date reached, block until new target received
				synchronized (_dateLock) {
					finishMsg.setEndValidityPeriod(usedTarget);
					System.out.println(getModuleName()
							+ " sending FINISH message TS=" + usedTarget);
					_client.publish(getModuleName(), finishMsg);
					try {
						// if we didn't get a new target meanwhile, block until
						// it
						// arrives
						if (!_updatedTS && !_restarted) {
							_dateLock.wait();
						}
					} catch (InterruptedException e) {

						e.printStackTrace();
					}

				}

			}
		} finally {
			unregisterService();
		}

	}

	/**
	 * Adds a source in the list of waited modules.
	 * 
	 * @param src
	 *            the module name of the source.
	 */
	public void addSource(String src) {
		synchronized (_sources) {
			_sources.put(src, 0l);
			_filter.addSource(src);
		}
	}

	/**
	 * <pre>
	 * TimeSteppedStub --name &lt;moduleName&gt; --sources &lt;coma separated source names&gt; --directory &lt;directory&gt;
	 * </pre>
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		ArgParser argParser = new ArgParser(args);
		Client client = new Client();
		client.connectToServer();

		String myName = argParser.getSValue("name");
		if (myName == null) {
			myName = "DataProvider Stub " + Math.random();
		}
		System.out.println("Starting module " + myName);

		ArrayList<TimeSteppedStub> stubs = new ArrayList<TimeSteppedStub>();

		if (argParser.getBValue("multi")) {
			System.out.println("Starting multi modules");
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 3; j++) {
					TimeSteppedStub mul = new TimeSteppedStub(myName + "_" + i
							+ "_" + j);
					if (i > 0) {
						mul.addSource(myName + "_" + (i - 1) + "_" + j);
					}
					stubs.add(mul);
				}
			}
		} else {

			TimeSteppedStub stub = new TimeSteppedStub(myName);
			stubs.add(stub);
		}

		for (TimeSteppedStub stub : stubs) {
			stub.init(client);
			stub.registerService();

			String waitedString = argParser.getSValue("sources");
			if (waitedString != null) {
				String waitedArray[] = waitedString.split(",");
				for (String waited : waitedArray) {
					stub.addSource(waited);
				}
			}

			String dir = argParser.getSValue("directory");
			if (dir == null) {
				GregorianCalendar gc = new GregorianCalendar(
						TimeZone.getTimeZone("GMT"));
				gc.set(2013, 01, 13, 14, 15);
				stub.createRandom(gc.getTimeInMillis(),
						gc.getTimeInMillis() + 60 * 1000, 1000);
			} else {
				stub.fillFromDir(dir);
			}

			// stub.run();

			System.out.println("Starting thread for " + stub.getModuleName());
			Thread t = new Thread(stub);
			t.start();
		}
	}

}
