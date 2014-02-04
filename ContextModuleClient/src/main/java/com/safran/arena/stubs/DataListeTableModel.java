/**
 * 
 */
package com.safran.arena.stubs;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import eu.arena_fp7._1.AbstractDataFusionType;

/**
 * This model fills a table with a data list. This is clearly not optimized for
 * adding/removing data, it is build for a data set that changed fully between
 * two updates.<bR>
 * This model is not a listener, as other models could be, because it is filled
 * by the result of a request and not by events.
 * 
 * @author F270116
 * 
 */
public class DataListeTableModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<AbstractDataFusionType> _data = new ArrayList<AbstractDataFusionType>();

	/**
	 * Column names.
	 * 
	 * @author F270116
	 * 
	 */
	public static enum Columns {
		DataSourceId, Id, Confidence, StartValidityPeriod, EndValidityPeriod, TimeStamp, Href
	}

	static final int _colCount = Columns.values().length;

	/**
	 * 
	 */
	public DataListeTableModel() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {

		return _colCount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		int rowCount = 0;
		synchronized (_data) {
			rowCount = _data.size();
		}

		return rowCount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int colIndex) {
		Object o = null;
		AbstractDataFusionType data;
		synchronized (_data) {
			data = _data.get(rowIndex);
		}
		Columns col = Columns.values()[colIndex];
		switch (col) {
		case DataSourceId:
			o = data.getDataSourceId();
			break;
		case Id:
			o = data.getId();
			break;
		case Confidence:
			o = data.getProbability();
			break;
		case StartValidityPeriod:
			o = data.getStartValidityPeriod();
			break;
		case EndValidityPeriod:
			o = data.getEndValidityPeriod();
			break;
		case TimeStamp:
			o = data.getTimestamp();
			break;
		case Href:
			o = data.getHref();
			break;
		}

		return o;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int colIndex) {
		Columns col = Columns.values()[colIndex];
		return col.toString();
	}

	/**
	 * sets the data and fire an event to update the table.
	 * 
	 * @param data
	 */
	public void setData(List<AbstractDataFusionType> data) {

		synchronized (_data) {
			_data.clear();
			if (data != null) {
				_data.addAll(data);
			}
		}
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				fireTableDataChanged();
			}

		});

	}
}
