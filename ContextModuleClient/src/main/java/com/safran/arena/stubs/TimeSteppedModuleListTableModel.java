/**
 * 
 */
package com.safran.arena.stubs;

import java.awt.EventQueue;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

/**
 * Example of table model for a list of modules. This implements the
 * ConfigurationManagerInterface to be registered as a submodule of a
 * ModuleImpl.
 * 
 * @author F270116
 * 
 */
public class TimeSteppedModuleListTableModel extends AbstractTableModel
		implements SchedulerThreadListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SchedulerThread _scheduler;
	private SimpleDateFormat format = new SimpleDateFormat();

	public static enum Columns {
		Name, Date, TimeStamp, Status, Group
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
		long date;
		SchedulerThread.Status status;
		SchedulerThread.Group group;
	}

	private final Map<String, Row> _rows = new HashMap<String, TimeSteppedModuleListTableModel.Row>();

	public TimeSteppedModuleListTableModel(SchedulerThread scheduler) {
		super();
		_scheduler = scheduler;
		_scheduler.addListener(this);
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
		Columns col = Columns.values()[colId];
		switch (col) {
		case Name:
			return row._name;
		case TimeStamp:
			return row.date;
		case Date:
			return format.format(new Date(row.date));
		case Status:
			return row.status;
		case Group:
			return row.group;
		}
		return null;
	}

	
	
	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		Columns col = Columns.values()[columnIndex];
		switch (col) {
		case Name:
			return String.class;
		case TimeStamp:
			return Long.class;
		case Date:
			return String.class;
		case Status:
			return SchedulerThread.Status.class;
		case Group:
			return SchedulerThread.Group.class;
		}
		return null;
	}

	@Override
	public String getColumnName(int column) {

		return Columns.values()[column].name();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		Columns col = Columns.values()[columnIndex];
		if (col == Columns.Group) {
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object,
	 * int, int)
	 */
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		Columns col = Columns.values()[columnIndex];
		if (col == Columns.Group && aValue instanceof SchedulerThread.Group) {
			synchronized (_rows) {
				if (_rows.size() > rowIndex) {
					Row row = (Row) _rows.values().toArray()[rowIndex];
					if (row != null) {
						_scheduler.changeGroup(row._name, (SchedulerThread.Group) aValue );
						
					}
				}

			}

		}
		super.setValueAt(aValue, rowIndex, columnIndex);
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
	 * com.safran.arena.stubs.SchedulerThreadListener#moduleAdded(java.lang.
	 * String)
	 */
	@Override
	public void moduleAdded(String name) {
		synchronized (_rows) {
			Row row = _rows.get(name);
			if (row == null) {
				row = new Row();
				_rows.put(name, row);
				row._name = name;
				row.status = _scheduler.getModuleStatus(name);
				row.date = _scheduler.getModuleTS(name);
				row.group = _scheduler.getModuleGroup(name);
			} 

		}
		updateAsABeaf();

	}

	
	
	/* (non-Javadoc)
	 * @see com.safran.arena.stubs.SchedulerThreadListener#moduleRemoved(java.lang.String)
	 */
	@Override
	public void moduleRemoved(String name) {
		synchronized (_rows) {
			Row row = _rows.get(name);
			if (row != null) {
				_rows.remove(name);
			}

		}
		updateAsABeaf();
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.safran.arena.stubs.SchedulerThreadListener#moduleStatusChanged(java
	 * .lang.String)
	 */
	@Override
	public void moduleStatusChanged(String name) {
		synchronized (_rows) {
			Row row = _rows.get(name);
			if (row != null) {
				row.status = _scheduler.getModuleStatus(name);
			} else {
				System.err.println("moduleStatusChanged received unknown name ="+name);
			}

		}
		updateAsABeaf();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.safran.arena.stubs.SchedulerThreadListener#moduleTSChanged(java.lang
	 * .String)
	 */
	@Override
	public void moduleTSChanged(String name) {
		synchronized (_rows) {
			Row row = _rows.get(name);
			if (row != null) {
				row.date = _scheduler.getModuleTS(name);
			} else {
				System.err.println("moduleTSChanged received unknown name ="+name);
			}

		}
		updateAsABeaf();

	}

	
	
	/* (non-Javadoc)
	 * @see com.safran.arena.stubs.SchedulerThreadListener#moduleGroupChanged(java.lang.String)
	 */
	@Override
	public void moduleGroupChanged(String name) {
		synchronized (_rows) {
			Row row = _rows.get(name);
			if (row != null) {
				row.group = _scheduler.getModuleGroup(name);
			} else {
				System.err.println("moduleGroupChanged received unknown name ="+name);
			}

		}
		updateAsABeaf();
		
	}

	/* (non-Javadoc)
	 * @see com.safran.arena.stubs.SchedulerThreadListener#currentDateChanged(long)
	 */
	@Override
	public void currentDateChanged(long timeStamp) {
		// nothing
		
	}

	/* (non-Javadoc)
	 * @see com.safran.arena.stubs.SchedulerThreadListener#statusChanged(com.safran.arena.stubs.SchedulerThreadListener.ThreadStatus)
	 */
	@Override
	public void statusChanged(ThreadStatus status) {
		// nothing
		
	}

	
}
