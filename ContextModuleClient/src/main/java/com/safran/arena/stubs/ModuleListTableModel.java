/**
 * 
 */
package com.safran.arena.stubs;

import java.awt.EventQueue;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import com.safran.arena.ConfigurationManagerInterface;
import com.safran.arena.DataProducerInterface;
import com.safran.arena.impl.Client;
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
 * Example of table model for a list of modules. This implements the
 * ConfigurationManagerInterface to be registered as a submodule of a
 * ModuleImpl.
 * 
 * @author F270116
 * 
 */
public class ModuleListTableModel extends AbstractTableModel implements
		ConfigurationManagerInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Client _client;
	private ModuleImpl _module;

	public static enum Columns {
		Name, ConfigurationManager, SensorManager, DataProducer, DataConsumer, DataRepository, ThreatHandler, TimeStepped
	}

	static final int nbColumns = Columns.values().length;

	/**
	 * Small structure to record the properties of each module.
	 * 
	 * @author F270116
	 * 
	 */
	private static class Row {
		String _name;
		boolean _moduleTypes[] = { false, false, false, false, false, false,
				false, false };
	}

	private final Map<String, Row> _rows = new HashMap<String, ModuleListTableModel.Row>();

	public ModuleListTableModel(Client client, ModuleImpl module) {
		super();
		_client = client;
		_module = module;
		_module.addConfigurationManagerListener(this);
		List<Module> modules = _client.getModuleList(getModuleName());
		for (Module registeredModule : modules) {

			if (registeredModule instanceof DataProducer) {
				this.onDataProducerRegistered((DataProducer) registeredModule);
			} else if (registeredModule instanceof DataConsumer) {
				this.onConsumerRegistered((DataConsumer) registeredModule);
			} else if (registeredModule instanceof ThreatHandler) {
				// absent ??
			} else if (registeredModule instanceof DataRepository) {
				this.onDataRepositoryRegistered((DataRepository) registeredModule);
			} else if (registeredModule instanceof SensorManager) {
				this.onSensorManagerRegistered((SensorManager) registeredModule);
			} else if (registeredModule instanceof TimeSteppedModule) {
				this.onTimeSteppedModuleRegistered((TimeSteppedModule) registeredModule);
			}
		}
	}

	@Override
	public int getColumnCount() {

		return nbColumns;
	}

	@Override
	public int getRowCount() {
		int c = 0;
		synchronized (_rows) {
			c = _rows.size();
		}
		return c;
	}

	@Override
	public Object getValueAt(int rowId, int colId) {
		Row row = null;
		synchronized (_rows) {
			row = (Row) _rows.values().toArray()[rowId];
		}
		if (colId == 0) {
			return row._name;
		}
		return row._moduleTypes[colId];
	}

	@Override
	public String getColumnName(int column) {

		return Columns.values()[column].name();
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
		Row row = null;
		synchronized (_rows) {
			row = _rows.get(consumer.getId());
			if (row == null) {
				row = new Row();
				row._name = consumer.getId();
				_rows.put(row._name, row);
			}
		}

		row._moduleTypes[Columns.DataConsumer.ordinal()] = true;
		updateAsABeaf();
	}

	/**
	 * Simple way to update the table. This is not a good, efficient, way to
	 * code an IHM! This has been done because this stub is just what it is: a
	 * quick and dirty example of Integration Platform usage, not an example of
	 * good practices in IHM implementation.
	 */
	private void updateAsABeaf() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					fireTableDataChanged();

				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		});

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
		// TODO

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
		Row row = null;
		synchronized (_rows) {
			row = _rows.get(consumer.getId());
			if (row == null) {
				row = new Row();
				row._name = consumer.getId();
				_rows.put(row._name, row);
			}
		}

		row._moduleTypes[Columns.DataConsumer.ordinal()] = true;
		updateAsABeaf();

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
		Row row = null;
		synchronized (_rows) {
			row = _rows.get(consumer.getId());
			if (row != null) {
				row._moduleTypes[Columns.DataConsumer.ordinal()] = false;
			}
		}

		updateAsABeaf();

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
		Row row = null;
		synchronized (_rows) {
			row = _rows.get(producer.getId());
			if (row == null) {
				row = new Row();
				row._name = producer.getId();
				_rows.put(row._name, row);
			}
		}

		DataProducerInterface producerProxy = _client.getProducerProxy(producer
				.getId());
		// this line is particularly USELESS here; it has been added for testing
		// purpose only
		Configuration configuration = producerProxy.getConfiguration();

		row._moduleTypes[Columns.DataProducer.ordinal()] = true;
		updateAsABeaf();

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
		Row row = null;
		synchronized (_rows) {
			row = _rows.get(producer.getId());
			if (row != null) {
				row._moduleTypes[Columns.DataProducer.ordinal()] = false;
			}
		}

		updateAsABeaf();

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
		Row row = null;
		synchronized (_rows) {
			row = _rows.get(dataRepository.getId());
			if (row == null) {
				row = new Row();
				row._name = dataRepository.getId();
				_rows.put(row._name, row);
			}
		}

		row._moduleTypes[Columns.DataRepository.ordinal()] = true;
		updateAsABeaf();

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
		Row row = null;
		synchronized (_rows) {
			row = _rows.get(dataRepository.getId());
			if (row != null) {
				row._moduleTypes[Columns.DataRepository.ordinal()] = false;
			}
		}

		updateAsABeaf();

	}

	@Override
	public void onModuleUnregistered(Module module) {
		Row row = null;
		synchronized (_rows) {
			row = _rows.get(module.getId());
			if (row != null) {
				_rows.remove(module.getId());
			}
		}

		updateAsABeaf();
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
		Row row = null;
		synchronized (_rows) {
			row = _rows.get(sensorManager.getId());
			if (row == null) {
				row = new Row();
				row._name = sensorManager.getId();
				_rows.put(row._name, row);
			}
		}
		row._moduleTypes[Columns.SensorManager.ordinal()] = true;
		updateAsABeaf();

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
		Row row = null;
		synchronized (_rows) {
			row = _rows.get(sensorManager.getId());
			if (row != null) {
				row._moduleTypes[Columns.SensorManager.ordinal()] = false;
			}
		}

		updateAsABeaf();

	}

	/* (non-Javadoc)
	 * @see com.safran.arena.ConfigurationManagerInterface#onTimeSteppedModuleRegistered(eu.arena_fp7._1.TimeSteppedModule)
	 */
	@Override
	public void onTimeSteppedModuleRegistered(TimeSteppedModule module) {
		Row row = null;
		synchronized (_rows) {
			row = _rows.get(module.getId());
			if (row == null) {
				row = new Row();
				row._name = module.getId();
				_rows.put(row._name, row);
			}
		}
		row._moduleTypes[Columns.TimeStepped.ordinal()] = true;
		updateAsABeaf();
		
	}

	/* (non-Javadoc)
	 * @see com.safran.arena.ConfigurationManagerInterface#onTimeSteppedModuleUnregistered(eu.arena_fp7._1.TimeSteppedModule)
	 */
	@Override
	public void onTimeSteppedModuleUnregistered(TimeSteppedModule module) {
		Row row = null;
		synchronized (_rows) {
			row = _rows.get(module.getId());
			if (row != null) {
				row._moduleTypes[Columns.TimeStepped.ordinal()] = false;
			}
		}

		updateAsABeaf();
		
	}

	
}
