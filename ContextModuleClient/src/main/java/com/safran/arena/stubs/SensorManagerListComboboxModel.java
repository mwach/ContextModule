/**
 * 
 */
package com.safran.arena.stubs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

import com.safran.arena.ConfigurationManagerInterface;
import com.safran.arena.impl.Client;
import com.safran.arena.impl.DataRepositoryProxy;
import com.safran.arena.impl.ModuleImpl;

import eu.arena_fp7._1.Configuration;
import eu.arena_fp7._1.ConfigurationResult;
import eu.arena_fp7._1.DataConsumer;
import eu.arena_fp7._1.DataFilters;
import eu.arena_fp7._1.DataProducer;
import eu.arena_fp7._1.DataRepository;
import eu.arena_fp7._1.Module;
import eu.arena_fp7._1.SensorManager;
import eu.arena_fp7._1.ThreatHandler;
import eu.arena_fp7._1.TimeSteppedModule;

/**
 * This model feeds a ComboBox with the known SensorManagers' list. This
 * implements the ConfigurationManagerInterface to get the module registering
 * events. But, this is not a Module. It is a listener for events dispatched by
 * ModuleImpl. <br>
 * BUG: will not display a data producer which has been registered before its
 * initialization but has not distributed data before this module's query to the
 * repository.
 * 
 * @author F270116
 * 
 */
public class SensorManagerListComboboxModel extends AbstractListModel implements
		ComboBoxModel, ConfigurationManagerInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TreeSet<String> sensorManagersNames = new TreeSet<String>();
	private ArrayList<String> _sensorManagerNamesList = new ArrayList<String>();

	private Client _client;
	private ModuleImpl _module;
	private String _selected;

	/**
	 * 
	 * @param client
	 * @param module
	 */
	public SensorManagerListComboboxModel(Client client, ModuleImpl module) {
		super();
		_client = client;
		_module = module;
	
		synchronized (sensorManagersNames) {
			_module.addConfigurationManagerListener(this);

			sensorManagersNames.add("");
			List<Module> modules = _client.getModuleList(getModuleName());
			for (Module registeredModule : modules) {

				if (registeredModule instanceof SensorManager) {
					sensorManagersNames.add(registeredModule.getId());
				}
			}
			_sensorManagerNamesList.addAll(sensorManagersNames);
		}


	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.safran.arena.ModuleInterface#getConfiguration()
	 */
	@Override
	public Configuration getConfiguration() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.safran.arena.ModuleInterface#setConfiguration(eu.arena_fp7._1.
	 * Configuration)
	 */
	@Override
	public ConfigurationResult setConfiguration(Configuration configuration) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.safran.arena.ModuleInterface#getModuleName()
	 */
	@Override
	public String getModuleName() {
		return _module.getModuleName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.safran.arena.ConfigurationManagerInterface#onConsumerSubscribed(eu
	 * .arena_fp7._1.DataConsumer, eu.arena_fp7._1.DataFilters)
	 */
	@Override
	public void onConsumerSubscribed(DataConsumer consumer, DataFilters filters) {
		// nothing

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.safran.arena.ConfigurationManagerInterface#onConsumerUnsubscribed
	 * (eu.arena_fp7._1.DataConsumer, eu.arena_fp7._1.DataFilters)
	 */
	@Override
	public void onConsumerUnsubscribed(DataConsumer consumer,
			DataFilters filters) {
		// nothing

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.safran.arena.ConfigurationManagerInterface#onConsumerRegistered(eu
	 * .arena_fp7._1.DataConsumer)
	 */
	@Override
	public void onConsumerRegistered(DataConsumer consumer) {
		// nothing

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.safran.arena.ConfigurationManagerInterface#onConsumerUnregistered
	 * (eu.arena_fp7._1.DataConsumer)
	 */
	@Override
	public void onConsumerUnregistered(DataConsumer consumer) {
		// nothing

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.safran.arena.ConfigurationManagerInterface#onDataProducerRegistered
	 * (eu.arena_fp7._1.DataProducer)
	 */
	@Override
	public void onDataProducerRegistered(DataProducer producer) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.safran.arena.ConfigurationManagerInterface#onDataProducerUnregistered
	 * (eu.arena_fp7._1.DataProducer)
	 */
	@Override
	public void onDataProducerUnregistered(DataProducer producer) {
		// nothing

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.safran.arena.ConfigurationManagerInterface#onModuleUnregistered(eu
	 * .arena_fp7._1.Module)
	 */
	@Override
	public void onModuleUnregistered(Module module) {
		// nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.safran.arena.ConfigurationManagerInterface#onDataRepositoryRegistered
	 * (eu.arena_fp7._1.DataRepository)
	 */
	@Override
	public void onDataRepositoryRegistered(DataRepository dataRepository) {
		// nothing

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.safran.arena.ConfigurationManagerInterface#onDataRepositoryUnregistered
	 * (eu.arena_fp7._1.DataRepository)
	 */
	@Override
	public void onDataRepositoryUnregistered(DataRepository dataRepository) {
		// nothing

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.safran.arena.ConfigurationManagerInterface#onSensorManagerRegistered
	 * (eu.arena_fp7._1.SensorManager)
	 */
	@Override
	public void onSensorManagerRegistered(SensorManager sensorManager) {
		synchronized (sensorManagersNames) {
			sensorManagersNames.add(sensorManager.getId());
			_sensorManagerNamesList.clear();
			_sensorManagerNamesList.addAll(sensorManagersNames);
			fireContentsChanged(this, 0, sensorManagersNames.size());
		}

	}

	/**
	 * Add a sensorManager created in local.
	 * 
	 * @param name
	 */
	public void onTestSensorManagerRegistered(String name) {
		synchronized (sensorManagersNames) {
			sensorManagersNames.add(name);
			_sensorManagerNamesList.clear();
			_sensorManagerNamesList.addAll(sensorManagersNames);
			fireContentsChanged(this, 0, sensorManagersNames.size());
		}
	}

	
	
	/* (non-Javadoc)
	 * @see com.safran.arena.ConfigurationManagerInterface#onTimeSteppedModuleRegistered(eu.arena_fp7._1.TimeSteppedModule)
	 */
	@Override
	public void onTimeSteppedModuleRegistered(TimeSteppedModule module) {
		// nothing
		
	}

	/* (non-Javadoc)
	 * @see com.safran.arena.ConfigurationManagerInterface#onTimeSteppedModuleUnregistered(eu.arena_fp7._1.TimeSteppedModule)
	 */
	@Override
	public void onTimeSteppedModuleUnregistered(TimeSteppedModule module) {
		// nothing
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.safran.arena.ConfigurationManagerInterface#onSensorManagerUnregistered
	 * (eu.arena_fp7._1.SensorManager)
	 */
	@Override
	public void onSensorManagerUnregistered(SensorManager sensorManager) {
		synchronized (sensorManagersNames) {
			sensorManagersNames.remove(sensorManager.getId());
			_sensorManagerNamesList.clear();
			_sensorManagerNamesList.addAll(sensorManagersNames);
			fireContentsChanged(this, 0, sensorManagersNames.size());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.ListModel#getElementAt(int)
	 */
	@Override
	public String getElementAt(int arg0) {
		synchronized (sensorManagersNames) {
			return _sensorManagerNamesList.get(arg0);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.ListModel#getSize()
	 */
	@Override
	public int getSize() {
		synchronized (sensorManagersNames) {
			return _sensorManagerNamesList.size();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.ComboBoxModel#getSelectedItem()
	 */
	@Override
	public Object getSelectedItem() {

		return _selected;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.ComboBoxModel#setSelectedItem(java.lang.Object)
	 */
	@Override
	public void setSelectedItem(Object arg0) {
		if (arg0 instanceof String) {
			_selected = (String) arg0;
		} else {
			IllegalArgumentException e = new IllegalArgumentException(
					"Argument should be String, was " + arg0);
			e.printStackTrace();
			throw e;
		}

	}

}
