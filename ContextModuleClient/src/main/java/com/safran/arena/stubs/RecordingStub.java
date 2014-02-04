/**
 * 
 */
package com.safran.arena.stubs;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import com.safran.arena.DataConsumerInterface;

import eu.arena_fp7._1.AbstractDataFusionType;
import eu.arena_fp7._1.Configuration;
import eu.arena_fp7._1.ConfigurationResult;

/**
 * Class for recording received data in xml files.
 * @author F270116
 *
 */
public class RecordingStub implements DataConsumerInterface {
	
	private long _subIndex = 0;
	private File _directory = new File(System.getProperty("user.dir"));
	private Object _lock = new Object();
	private Marshaller _marshaller;
	private boolean _active = false;

	/**
	 * 
	 */
	public RecordingStub() {
		JAXBContext jaxbContext;
		
		try {
			jaxbContext = JAXBContext
					.newInstance("eu.arena_fp7._1:org.uncertml._2");
			_marshaller = jaxbContext.createMarshaller();
		} catch (JAXBException e1) {
			e1.printStackTrace();
			return;
		}
	}
	
	/**
	 * Change de destination directory for messages.
	 * @param dirName
	 * @return false if dirName is null, not a directory or is not writable.
	 */
	public boolean changeDirectory(String dirName) {
		boolean ret = false;
		if (dirName == null) {
			return ret;
		}
		File candidate = new File(dirName);
		if (candidate.isDirectory() && candidate.canWrite()) {
			synchronized (_lock) {
				_directory = candidate;
			}
			ret = true;
		} 
		return ret;
		
	}
	
	/**
	 * Returns the directory used currently to store the xml files.
	 * @return the directory used currently to store the xml files.
	 */
	public File getDirectory() {
		return _directory;
	}
	/**
	 * Record an object in the current directory. File name is <i>&lt;timestamp>_&lt;internal counter>_&lt;object class>.xml</i>.
	 * @param data
	 */
	public void record(AbstractDataFusionType data) {
		if (!_active) {
			return;
		}
		StringBuffer buff = new StringBuffer();
		Long date = data.getTimestamp();
		long index = 0;
		if (date == null) {
			date = data.getStartValidityPeriod();
		}
		if (date == null) {
			date = data.getEndValidityPeriod();
		}
		if (date == null) {
			date = 0L;
		}
		synchronized (_lock) {
			index = _subIndex++;
		}
		buff.append(date);
		buff.append('_');
		buff.append(index);
		buff.append('_');
		buff.append(data.getClass().getName());
		buff.append(".xml");
		
		File destinationDir;
		synchronized (_lock) {
			destinationDir = _directory;
		}
		try {
			File destinationFile = new File(destinationDir.getCanonicalPath()+"/"+buff.toString());
			@SuppressWarnings({ "rawtypes", "unchecked" })
			JAXBElement toMarshall = new JAXBElement(new QName(
					"http://www.arena-fp7.eu/1.0", data
							.getClass().getSimpleName()),
							data.getClass(), data);
			_marshaller.marshal(toMarshall, destinationFile);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	
	
	/**
	 * @return the active
	 */
	public boolean isActive() {
		return _active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		_active = active;
	}

	/* (non-Javadoc)
	 * @see com.safran.arena.ModuleInterface#getConfiguration()
	 */
	@Override
	public Configuration getConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.safran.arena.ModuleInterface#setConfiguration(eu.arena_fp7._1.Configuration)
	 */
	@Override
	public ConfigurationResult setConfiguration(Configuration configuration) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.safran.arena.ModuleInterface#getModuleName()
	 */
	@Override
	public String getModuleName() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.safran.arena.DataConsumerInterface#onDataAvailable(java.lang.Class, java.lang.String, eu.arena_fp7._1.AbstractDataFusionType)
	 */
	@Override
	public void onDataAvailable(
			Class<? extends AbstractDataFusionType> dataType,
			String dataSourceId, AbstractDataFusionType data) {
		record(data);

	}

	/* (non-Javadoc)
	 * @see com.safran.arena.DataConsumerInterface#onDataChanged(java.lang.Class, java.lang.String, eu.arena_fp7._1.AbstractDataFusionType)
	 */
	@Override
	public void onDataChanged(Class<? extends AbstractDataFusionType> dataType,
			String dataSourceId, AbstractDataFusionType data) {
		record(data);

	}

	/* (non-Javadoc)
	 * @see com.safran.arena.DataConsumerInterface#onDataDeleted(java.lang.Class, java.lang.String, eu.arena_fp7._1.AbstractDataFusionType)
	 */
	@Override
	public void onDataDeleted(Class<? extends AbstractDataFusionType> dataType,
			String dataSourceId, AbstractDataFusionType data) {
		// TODO Auto-generated method stub

	}

}
