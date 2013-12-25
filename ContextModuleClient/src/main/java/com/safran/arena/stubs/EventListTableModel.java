/**
 * 
 */
package com.safran.arena.stubs;

import java.awt.EventQueue;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.table.AbstractTableModel;

import com.safran.arena.DataConsumerInterface;
import com.safran.arena.impl.Client;
import com.safran.arena.impl.ModuleImpl;

import eu.arena_fp7._1.AbstractDataFusionType;
import eu.arena_fp7._1.Configuration;
import eu.arena_fp7._1.ConfigurationResult;

/**
 * This model is used to fill a table with a list of events (i.e.:
 * {@link AbstractDataFusionType} creation/modification/suppression). Current
 * version is minimalist. <br>
 * This implements DataConsumerInterface and is to be registered to ModuleImpl
 * to get the events.
 * 
 * @author F270116
 * 
 */
public class EventListTableModel extends AbstractTableModel implements
		DataConsumerInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Internal calss to support the table records.
	 * 
	 * @author F270116
	 * 
	 */
	private static class Event {
		String className;
		String time;

		public Event(String className, String time) {
			super();
			this.className = className;
			this.time = time;
		}
	};

	Client _client;
	ModuleImpl _module;

	public EventListTableModel(Client client, ModuleImpl module) {
		super();
		_client = client;
		_module = module;
		_module.addDataConsumerListener(this);
	}

	private ArrayList<Event> _eventList = new ArrayList<EventListTableModel.Event>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return 2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		int c = 0;
		synchronized (_eventList) {
			c = _eventList.size();
		}
		return c;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int arg0, int arg1) {

		Event e;
		synchronized (_eventList) {
			try {
				e = _eventList.get(arg0);
			} catch (IndexOutOfBoundsException error) {
				return "Try again";
			}
		}
		switch (arg1) {
		case 0:
			return e.className;
		case 1:
			return e.time;
		default:
			return "Out of bound";
		}
	}

	@Override
	public String getColumnName(int column) {
		switch (column) {
		case 0:
			return "Class name";
		case 1:
			return "Receive time";

		}
		return super.getColumnName(column);
	}

	@Override
	public Configuration getConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConfigurationResult setConfiguration(Configuration configuration) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getModuleName() {
		return _module.getModuleName();
	}

	@Override
	public void onDataAvailable(Class<? extends AbstractDataFusionType> dataType, String dataSourceId,
			AbstractDataFusionType data) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDataChanged(Class<? extends AbstractDataFusionType> dataType, String dataSourceId,
			AbstractDataFusionType data) {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat formatter = new SimpleDateFormat();
		String dateStr = formatter.format(date);
		synchronized (_eventList) {
			_eventList.add(new Event(dataType.getSimpleName(), dateStr));

		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					fireTableDataChanged(); // TODO a row inserted should be
											// better

				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		});

	}

	@Override
	public void onDataDeleted(Class<? extends AbstractDataFusionType> dataType, String dataSourceId,
			AbstractDataFusionType data) {
		// TODO Auto-generated method stub

	}

}
