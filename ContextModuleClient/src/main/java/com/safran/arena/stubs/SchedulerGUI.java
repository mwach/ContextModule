package com.safran.arena.stubs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.swing.DefaultCellEditor;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableColumn;

import com.safran.arena.impl.Client;

public class SchedulerGUI implements SchedulerThreadListener {

	public static final String MODULENAME = "SchedulerGUI";
	private static long REF_TS; 

	private JFrame _frame;
	private JTextField _startTextField;
	private JTextField _endTextField;
	private JLabel _startLabel;
	private JSpinner _stepSpinner;
	private JLabel _endLabel;
	private Long _start = 0l;
	private Long _end = System.currentTimeMillis();
	private Long _step = 800l;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss.SSS");
	private JTable _table;
	private SchedulerThread _scheduler;
	private Client _client;
	private JProgressBar _progressBar;
	private boolean isPaused = false;
	
	private ActionListener _startListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			_scheduler.startScheduling(_start, _end);
			
		}
	};

	private ActionListener _stopListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			_scheduler.stopScheduling();
			
		}
	};
	
	private ActionListener _pauseListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if (isPaused) {
				_scheduler.unPause();
			} else {
				_scheduler.pause();
			}
			
		}
	};
	private JButton _btnStart;
	private JButton _btnPause;
	private JButton _btnStop;

	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Client client = new Client();
					client.connectToServer();
					SchedulerGUI window = new SchedulerGUI(client);
					window._frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private int getProgressTime(long timestamp){
		Long ts = (timestamp - REF_TS) / 100l;
		return ts.intValue();
	}
	
	/**
	 * Sets the start date of the scheduling. 
	 * @param value String containing a long (timestamp).
	 * @return
	 */
	protected boolean setStartDate(String value) {
		boolean ret = false;
		Long start = 0l;
		try {
			start = Long.parseLong(value);
		} catch (Exception e) {
			_startLabel.setText(_start.toString());
			return false;
		}
		_startTextField.setText(start.toString());
		_start = start;
		_startLabel.setText(dateFormat.format(new Date(_start)));
		_progressBar.setMinimum(getProgressTime(_start));
		return ret;
	}
	/**
	 * Sets the end date of the scheduling. Upon reaching this date, the scheduling stops.
	 * @param value String containing a long (timestamp).
	 * @return
	 */
	protected boolean setEndDate(String value) {
		boolean ret = false;
		Long end = 0l;
		try {
			end = Long.parseLong(value);
		} catch (Exception e) {
			_endLabel.setText(_end.toString());
			return false;
		}
		_endTextField.setText(end.toString());
		_end = end;
		_endLabel.setText(dateFormat.format(new Date(_end)));
		_progressBar.setMaximum(getProgressTime(_end));
		_scheduler.setStepAndEnd(_step, _end);
		return ret;
	}
	
	
	
	/* (non-Javadoc)
	 * @see com.safran.arena.stubs.SchedulerThreadListener#moduleAdded(java.lang.String)
	 */
	@Override
	public void moduleAdded(String name) {
		// nothing - treated by the table model
		
	}

	/* (non-Javadoc)
	 * @see com.safran.arena.stubs.SchedulerThreadListener#moduleStatusChanged(java.lang.String)
	 */
	@Override
	public void moduleStatusChanged(String name) {
		// nothing - treated by the table model
		
	}

	/* (non-Javadoc)
	 * @see com.safran.arena.stubs.SchedulerThreadListener#moduleTSChanged(java.lang.String)
	 */
	@Override
	public void moduleTSChanged(String name) {
		// nothing - treated by the table model
		
	}
	
	

	/* (non-Javadoc)
	 * @see com.safran.arena.stubs.SchedulerThreadListener#moduleRemoved(java.lang.String)
	 */
	@Override
	public void moduleRemoved(String name) {
		// nothing - treated by the table model
		
	}

	/* (non-Javadoc)
	 * @see com.safran.arena.stubs.SchedulerThreadListener#currentDateChanged(long)
	 */
	@Override
	public void currentDateChanged(long timeStamp) {
		final long ts= timeStamp;
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				if (ts == 0) {
					_progressBar.setValue(getProgressTime(_start));
				}
				_progressBar.setValue(getProgressTime(ts));
				_progressBar.setString(""+ts);
				
			}
		});
	}
	
	

	/* (non-Javadoc)
	 * @see com.safran.arena.stubs.SchedulerThreadListener#statusChanged(com.safran.arena.stubs.SchedulerThreadListener.ThreadStatus)
	 */
	@Override
	public void statusChanged(ThreadStatus status) {
		if (status == ThreadStatus.PAUSED) {
			isPaused = true;
		} else {
			isPaused = false;
		}
		
		final ThreadStatus fStatus = status; 
		
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				switch (fStatus) {
				 case RUNNING:
					 _btnStart.setEnabled(false);
					 _btnStop.setEnabled(true);
					 _btnPause.setEnabled(true);
					 _btnPause.setText("Pause");
					 _btnPause.setBackground(Color.green);
					 break;
				 case STOPPED:
					 _btnStart.setEnabled(true);
					 _btnStop.setEnabled(false);
					 _btnPause.setEnabled(false);
					 _btnPause.setText("Pause scheduling");
					 _btnPause.setBackground(Color.green);
					 break;
				 case PAUSED:
					 _btnStart.setEnabled(false);
					 _btnStop.setEnabled(true);
					 _btnPause.setEnabled(true);
					 _btnPause.setText("Resume scheduling");
					 _btnPause.setBackground(Color.orange);
					 break;
				 }
				
			}
		});
		 
	}

	
	
	/* (non-Javadoc)
	 * @see com.safran.arena.stubs.SchedulerThreadListener#moduleGroupChanged(java.lang.String)
	 */
	@Override
	public void moduleGroupChanged(String name) {
		// nothing - treated by the table model
		
	}

	/**
	 * Create the application.
	 */
	public SchedulerGUI(Client client) {
		_client = client;
		GregorianCalendar gc = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
		gc.set(2000, 1, 1);
		REF_TS = gc.getTimeInMillis();
		initialize();
		_scheduler.addListener(this);
		Thread t = new Thread(_scheduler);
		t.setName("SchedulerThread");
		t.start();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		_frame = new JFrame();
		_frame.setName(MODULENAME);
		_frame.setTitle(MODULENAME);
		
		File f = new File("images/osa_lifecycle.png");
		if (f.canRead()) {
			
			Image img;
			try {
				img = ImageIO.read(f);
				_frame.setIconImage(img);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
		_frame.setBounds(100, 100, 842, 469);
		_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel listPanel = new JPanel();
		_frame.getContentPane().add(listPanel, BorderLayout.CENTER);
		
		_table = new JTable();
		_table.setPreferredScrollableViewportSize(new Dimension(800, 300));
		
		_scheduler = new SchedulerThread(MODULENAME, _client);
		_table.setModel(new TimeSteppedModuleListTableModel(_scheduler));
		
		TableColumn groupCol = _table.getColumnModel().getColumn(
				TimeSteppedModuleListTableModel.Columns.Group.ordinal());
		JComboBox groupChooser = new JComboBox();
		for (SchedulerThread.Group group : SchedulerThread.Group.values()) {
			groupChooser.addItem(group);
		}
		groupCol.setCellEditor(new DefaultCellEditor(groupChooser));

		JScrollPane scrollPane = new JScrollPane(_table);
		GroupLayout gl_listPanel = new GroupLayout(listPanel);
		gl_listPanel.setHorizontalGroup(
			gl_listPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_listPanel.createSequentialGroup()
					.addGap(12)
					.addComponent(scrollPane)
					.addGap(12))
		);
		gl_listPanel.setVerticalGroup(
			gl_listPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_listPanel.createSequentialGroup()
					.addGap(5)
					.addComponent(scrollPane)
					.addGap(31))
		);
		listPanel.setLayout(gl_listPanel);
		
		JPanel configPanel = new JPanel();
		configPanel.setPreferredSize(new Dimension(10, 60));
		_frame.getContentPane().add(configPanel, BorderLayout.NORTH);
		
		JLabel lblStart = new JLabel("Start ");
		
		_progressBar = new JProgressBar();
		
		_startTextField = new JTextField();
		_startTextField.setColumns(10);
		_startTextField.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setStartDate(_startTextField.getText());
				
			}
		});
		_startTextField.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				setStartDate(_startTextField.getText());
				
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				// no operation
				
			}
		});
		
		_startLabel = new JLabel("yyyyy/mm/dd hh:mm:ss.sss");
		
		JLabel lblStep = new JLabel("Step");
		
		_stepSpinner = new JSpinner();
		_stepSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Object o = _stepSpinner.getValue();
				if (o instanceof Number) {
					_step = ((Number)o).longValue();
					System.err.println("Changed step to "+_step);
					_scheduler.setStepAndEnd(_step, _end);
				}
			}
		});
		_stepSpinner.setValue(_step);
		
		JLabel lblEnd = new JLabel("End");
		
		_endTextField = new JTextField();
		_endTextField.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setEndDate(_endTextField.getText());
				
			}
		});
		_endTextField.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				setEndDate(_endTextField.getText());
				
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				// no operation
				
			}
		});
		_endTextField.setColumns(10);
		
		_endLabel = new JLabel("yyyyy/mm/dd hh:mm:ss.sss");
		GroupLayout gl_configPanel = new GroupLayout(configPanel);
		gl_configPanel.setHorizontalGroup(
			gl_configPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_configPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_configPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(_progressBar, GroupLayout.DEFAULT_SIZE, 501, Short.MAX_VALUE)
						.addGroup(gl_configPanel.createSequentialGroup()
							.addComponent(lblStart)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(_startTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(_startLabel)
							.addGap(27)
							.addComponent(lblStep, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(_stepSpinner, GroupLayout.PREFERRED_SIZE, 73, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addComponent(lblEnd)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(_endTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(_endLabel, GroupLayout.PREFERRED_SIZE, 134, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		gl_configPanel.setVerticalGroup(
			gl_configPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_configPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_configPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblStart)
						.addComponent(_startTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(_startLabel)
						.addComponent(lblStep)
						.addComponent(_stepSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblEnd)
						.addComponent(_endTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(_endLabel))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(_progressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		configPanel.setLayout(gl_configPanel);
		
		JPanel actionPanel = new JPanel();
		_frame.getContentPane().add(actionPanel, BorderLayout.SOUTH);
		
		_btnStart = new JButton("Start");
		actionPanel.add(_btnStart);
		_btnStart.addActionListener(_startListener);
		
		_btnPause = new JButton("Pause");
		actionPanel.add(_btnPause);
		_btnPause.addActionListener(_pauseListener);
		
		_btnStop = new JButton("Stop");
		actionPanel.add(_btnStop);
		_btnStop.addActionListener(_stopListener);
		_btnStop.setEnabled(false);
		
		setStartDate(_start.toString());
		setEndDate(_end.toString());
	}
}
