package com.safran.arena.stubs;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingUtilities;

import com.safran.arena.SensorManagerInterface;
import com.safran.arena.impl.Client;
import com.safran.arena.impl.DataRepositoryProxy;
import com.safran.arena.impl.ModuleImpl;
import com.safran.arena.impl.SensorManagerProxy;

import eu.arena_fp7._1.AbstractDataFusionType;
import eu.arena_fp7._1.Command;
import eu.arena_fp7._1.RegisterResultMessage;

/**
 * This stub has for purpose to test the following features:
 * <ul>
 * <li>subscribe to module register / unregister</li>
 * <li>subscribe to all data</li>
 * <li>get filtered data from the repository</li>
 * </ul>
 * It has been coded using Eclipse Juno built-in GUI editor. It may be used as
 * an example for coding such an application. WARNING: this version does not
 * unregister itself while closing. Unregistering itself before quitting should
 * be mandatory for a real application.
 * 
 * @author F270116
 * 
 */
public class GUIStubFrame {

	private JFrame _frame;
	private JTable _messageTable;
	private Client _client;
	private ModuleImpl _module;
	private EventListTableModel _eventTableModel;
	private JTable _modulesTable;
	private ModuleListTableModel _modulesTableModel;
	private JTextField _getDataStartTextField;
	private JTextField _getDataEndTextField;
	private JTable _getDataTable;
	private JComboBox _dataSourceCombo;
	private JComboBox _classComboBox;
	private ModuleListComboboxModel _dataProducersComboBoxModel;
	private ClassNamesComboboxModel _classNamesComboBoxModel;
	private DataListeTableModel _dataListTableModel;
	private JTextField _sensorRequestDateTextField;
	private JLabel _sensorImage;
	private SensorManagerListComboboxModel _sensorManagerListComboboxModel;
	private HashMap<String, SensorManagerProxy> _testProxies = new HashMap<String, SensorManagerProxy>();
	private static String DATEFORMAT = "yyyyMMdd HH:mm:ss";
	private static SimpleDateFormat _dateFormat = new SimpleDateFormat(
			DATEFORMAT);
	private JLabel _recordingDestinationLabel;
	private RecordingStub _recordingMachine;
	private JLabel _lblImagedate;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		File out = new File("out.log");
		PrintStream ps;
		/* try {
			ps = new PrintStream(out);
			System.setOut(ps);
			System.setErr(ps);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} */
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					// The Client is the representative of the Integration
					// Platform. It has to be instantiated and connected first.
					Client client = new Client();
					client.connectToServer();

					GUIStubFrame window = new GUIStubFrame(client);
					// window._frame.setUndecorated(true);
					window._frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GUIStubFrame(Client client) {
		_client = client;

		// An application is seen by the Integration Platform as a Module
		// A Quick-and-dirty way to have a module is to instantiate a
		// ModuleImpl.
		// While this is not the best way for a simple data provider, it is fair
		// enough for a GUI.
		_module = new ModuleImpl("GUI Stub " + Math.random());
		// First action then, register the module, ...
		_client.registerModule(_module);
		// then declare each of its roles : here this application is Data
		// Consumer and ConfigurationManager
		_client.registerModuleAsDataConsumer(_module, null);
		_client.registerModuleAsManager(_module);
		// various model initialization
		// remark: these models here implement one or the other module
		// interface, but they are not registered to the CLient; they are
		// registered to the ModuleImpl, which will perform the dispatching fo
		// events. Why? Because they are not modules, they are <i>parts</i> of a
		// module.
		_eventTableModel = new EventListTableModel(_client, _module);
		_modulesTableModel = new ModuleListTableModel(_client, _module);
		_dataProducersComboBoxModel = new ModuleListComboboxModel(_client,
				_module);
		_classNamesComboBoxModel = new ClassNamesComboboxModel();
		_dataListTableModel = new DataListeTableModel();
		_sensorManagerListComboboxModel = new SensorManagerListComboboxModel(
				_client, _module);
		_recordingMachine = new RecordingStub();
		_module.addDataConsumerListener(_recordingMachine);
		// standard GUI initialization
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		_frame = new JFrame();
		_frame.setBounds(100, 100, 579, 398);
		_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		_frame.setTitle(_module.getModuleName());
		
		File f = new File("images/osa_user_black_hat.png");
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

		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		_frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);

		JPanel modulesPanel = new JPanel();
		tabbedPane.addTab("Modules", null, modulesPanel, null);
		modulesPanel.setLayout(new BorderLayout(0, 0));

		JScrollPane modulesScrollPane = new JScrollPane();
		modulesPanel.add(modulesScrollPane, BorderLayout.CENTER);

		_modulesTable = new JTable();
		_modulesTable.setModel(_modulesTableModel);
		modulesScrollPane.setViewportView(_modulesTable);
		
		JPanel panel_2 = new JPanel();
		modulesPanel.add(panel_2, BorderLayout.SOUTH);
		
		JButton btnForceUnregister = new JButton("Force Unregister");
		btnForceUnregister.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selected[] = _modulesTable.getSelectedRows();
				String res = "";
				for (int rowIdx : selected) {
					int modelRowIdx = _modulesTable.convertRowIndexToModel(rowIdx);
					String moduleName = (String) _modulesTableModel.getValueAt(
							modelRowIdx,
							ModuleListTableModel.Columns.Name.ordinal());
					RegisterResultMessage msg = _client.forceUnregister(moduleName);
					if (msg.getErrorMessage() != null) {
						res = res+" "+msg.getErrorMessage();
					}
				}
				_lblModuleActionReturn.setText(res);
			}
		});
		panel_2.add(btnForceUnregister);
		
		_lblModuleActionReturn = new JLabel("message");
		panel_2.add(_lblModuleActionReturn);

		JPanel messagePanel = new JPanel();
		tabbedPane.addTab("Messages", null, messagePanel, null);
		messagePanel.setLayout(new BorderLayout(0, 0));

		_messageTable = new JTable();
		_messageTable.setModel(_eventTableModel);

		JScrollPane eventScrollPane = new JScrollPane(_messageTable);
		messagePanel.add(eventScrollPane, BorderLayout.CENTER);

		JPanel getDataPanel = new JPanel();
		tabbedPane.addTab("Data request", null, getDataPanel, null);

		JLabel lblDataSource = new JLabel("Data source");

		_dataSourceCombo = new JComboBox();
		_dataSourceCombo.setModel(_dataProducersComboBoxModel);

		JLabel lblDataClass = new JLabel("Data class");

		_classComboBox = new JComboBox();
		_classComboBox.setModel(_classNamesComboBoxModel);

		JLabel lblStartDate = new JLabel("Start date");

		_getDataStartTextField = new JTextField();
		_getDataStartTextField.setColumns(10);

		JLabel lblEndDate = new JLabel("End date");

		_getDataEndTextField = new JTextField();
		_getDataEndTextField.setColumns(10);

		JButton btnSendRequest = new JButton("Send request");
		btnSendRequest.addActionListener(new SendRequestAction());

		JScrollPane getDataScrollPane = new JScrollPane();
		GroupLayout gl_getDataPanel = new GroupLayout(getDataPanel);
		gl_getDataPanel
				.setHorizontalGroup(gl_getDataPanel
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								gl_getDataPanel
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												gl_getDataPanel
														.createParallelGroup(
																Alignment.LEADING)
														.addComponent(
																getDataScrollPane,
																GroupLayout.DEFAULT_SIZE,
																476,
																Short.MAX_VALUE)
														.addGroup(
																gl_getDataPanel
																		.createSequentialGroup()
																		.addComponent(
																				lblDataSource)
																		.addPreferredGap(
																				ComponentPlacement.UNRELATED)
																		.addComponent(
																				_dataSourceCombo,
																				GroupLayout.PREFERRED_SIZE,
																				GroupLayout.DEFAULT_SIZE,
																				GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				ComponentPlacement.UNRELATED)
																		.addComponent(
																				lblDataClass)
																		.addPreferredGap(
																				ComponentPlacement.RELATED)
																		.addComponent(
																				_classComboBox,
																				GroupLayout.PREFERRED_SIZE,
																				GroupLayout.DEFAULT_SIZE,
																				GroupLayout.PREFERRED_SIZE))
														.addGroup(
																gl_getDataPanel
																		.createSequentialGroup()
																		.addComponent(
																				lblStartDate)
																		.addPreferredGap(
																				ComponentPlacement.UNRELATED)
																		.addComponent(
																				_getDataStartTextField,
																				GroupLayout.PREFERRED_SIZE,
																				GroupLayout.DEFAULT_SIZE,
																				GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				ComponentPlacement.UNRELATED)
																		.addComponent(
																				lblEndDate)
																		.addPreferredGap(
																				ComponentPlacement.UNRELATED)
																		.addComponent(
																				_getDataEndTextField,
																				GroupLayout.PREFERRED_SIZE,
																				GroupLayout.DEFAULT_SIZE,
																				GroupLayout.PREFERRED_SIZE))
														.addComponent(
																btnSendRequest))
										.addContainerGap()));
		gl_getDataPanel
				.setVerticalGroup(gl_getDataPanel
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								gl_getDataPanel
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												gl_getDataPanel
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(
																lblDataSource)
														.addComponent(
																_dataSourceCombo,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE)
														.addComponent(
																lblDataClass)
														.addComponent(
																_classComboBox,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												ComponentPlacement.RELATED)
										.addGroup(
												gl_getDataPanel
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(
																lblStartDate)
														.addComponent(
																_getDataStartTextField,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE)
														.addComponent(
																lblEndDate)
														.addComponent(
																_getDataEndTextField,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												ComponentPlacement.RELATED)
										.addComponent(btnSendRequest)
										.addPreferredGap(
												ComponentPlacement.RELATED)
										.addComponent(getDataScrollPane,
												GroupLayout.DEFAULT_SIZE, 229,
												Short.MAX_VALUE)
										.addContainerGap()));

		_getDataTable = new JTable();
		_getDataTable.setModel(_dataListTableModel);
		getDataScrollPane.setViewportView(_getDataTable);
		getDataPanel.setLayout(gl_getDataPanel);

		JPanel panel = new JPanel();
		tabbedPane.addTab("Sensor Management", null, panel, null);

		JLabel lblSensorManager = new JLabel("Sensor Manager");

		JComboBox sensorsComboBox = new JComboBox();
		sensorsComboBox.setModel(_sensorManagerListComboboxModel);

		JLabel lblDate = new JLabel("Date");

		_sensorRequestDateTextField = new JTextField();
		_sensorRequestDateTextField.setColumns(DATEFORMAT.length() + 1);

		JPanel panel_1 = new JPanel();

		JButton btnGetimagedate = new JButton("getImage(date)");
		btnGetimagedate.addActionListener(new GetImageAction());

		JButton btnGetnextimage = new JButton("getNextImage");
		btnGetnextimage.addActionListener(new GetNextImageAction(false));

		JButton btnGetlastimage = new JButton("getLastImage");
		btnGetlastimage.addActionListener(new GetNextImageAction(true));

		JButton btnAddTestProxy = new JButton("Add test proxy");
		btnAddTestProxy.addActionListener(new GetTestProxyAction());

		JButton btnStartSensor = new JButton("Start");
		btnStartSensor.addActionListener(new StartSensorAction());

		_lblImagedate = new JLabel("ImageDate");

		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(gl_panel
				.createParallelGroup(Alignment.LEADING)
				.addGroup(
						gl_panel.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										gl_panel.createParallelGroup(
												Alignment.LEADING)
												.addComponent(
														panel_1,
														GroupLayout.DEFAULT_SIZE,
														538, Short.MAX_VALUE)
												.addGroup(
														gl_panel.createSequentialGroup()
																.addComponent(
																		lblSensorManager)
																.addPreferredGap(
																		ComponentPlacement.UNRELATED)
																.addComponent(
																		sensorsComboBox,
																		GroupLayout.PREFERRED_SIZE,
																		GroupLayout.DEFAULT_SIZE,
																		GroupLayout.PREFERRED_SIZE)
																.addPreferredGap(
																		ComponentPlacement.RELATED)
																.addComponent(
																		lblDate)
																.addPreferredGap(
																		ComponentPlacement.UNRELATED)
																.addComponent(
																		_sensorRequestDateTextField,
																		GroupLayout.PREFERRED_SIZE,
																		GroupLayout.DEFAULT_SIZE,
																		GroupLayout.PREFERRED_SIZE))
												.addGroup(
														gl_panel.createSequentialGroup()
																.addComponent(
																		btnStartSensor)
																.addPreferredGap(
																		ComponentPlacement.RELATED)
																.addComponent(
																		btnGetimagedate)
																.addPreferredGap(
																		ComponentPlacement.RELATED)
																.addComponent(
																		btnGetnextimage)
																.addPreferredGap(
																		ComponentPlacement.RELATED)
																.addComponent(
																		btnGetlastimage)
																.addPreferredGap(
																		ComponentPlacement.RELATED)
																.addComponent(
																		btnAddTestProxy)
																.addPreferredGap(
																		ComponentPlacement.RELATED)
																.addComponent(
																		_lblImagedate)))
								.addContainerGap()));
		gl_panel.setVerticalGroup(gl_panel
				.createParallelGroup(Alignment.LEADING)
				.addGroup(
						gl_panel.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										gl_panel.createParallelGroup(
												Alignment.BASELINE)
												.addComponent(lblSensorManager)
												.addComponent(
														sensorsComboBox,
														GroupLayout.PREFERRED_SIZE,
														GroupLayout.DEFAULT_SIZE,
														GroupLayout.PREFERRED_SIZE)
												.addComponent(lblDate)
												.addComponent(
														_sensorRequestDateTextField,
														GroupLayout.PREFERRED_SIZE,
														GroupLayout.DEFAULT_SIZE,
														GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(panel_1,
										GroupLayout.DEFAULT_SIZE, 260,
										Short.MAX_VALUE)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addGroup(
										gl_panel.createParallelGroup(
												Alignment.BASELINE)
												.addComponent(btnStartSensor)
												.addComponent(btnGetimagedate)
												.addComponent(btnGetnextimage)
												.addComponent(btnGetlastimage)
												.addComponent(btnAddTestProxy)
												.addComponent(_lblImagedate))
								.addGap(6)));
		panel_1.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		panel_1.add(scrollPane);

		_sensorImage = new JLabel("Image");
		scrollPane.setViewportView(_sensorImage);
		panel.setLayout(gl_panel);

		JPanel panelRecording = new JPanel();
		tabbedPane.addTab("Recording", null, panelRecording, null);

		JLabel lblRecirdingDirectory = new JLabel("Recording directory :");

		_recordingDestinationLabel = new JLabel(_recordingMachine
				.getDirectory().getAbsolutePath());

		JButton btnChooseRecordingDestination = new JButton("Choose");
		btnChooseRecordingDestination
				.addActionListener(new ChooseDestinationDirectory());

		JToggleButton tglbtnRecordMessages = new JToggleButton(
				"Record messages");

		ItemListener itemListener = new ItemListener() {
			public void itemStateChanged(ItemEvent itemEvent) {
				int state = itemEvent.getStateChange();
				if (state == ItemEvent.SELECTED) {
					_recordingMachine.setActive(true);
				} else {
					_recordingMachine.setActive(false);
				}
			}
		};
		tglbtnRecordMessages.addItemListener(itemListener);

		GroupLayout gl_panelRecording = new GroupLayout(panelRecording);
		gl_panelRecording
				.setHorizontalGroup(gl_panelRecording
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								gl_panelRecording
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												gl_panelRecording
														.createParallelGroup(
																Alignment.LEADING)
														.addGroup(
																gl_panelRecording
																		.createSequentialGroup()
																		.addComponent(
																				lblRecirdingDirectory)
																		.addPreferredGap(
																				ComponentPlacement.RELATED)
																		.addComponent(
																				_recordingDestinationLabel)
																		.addPreferredGap(
																				ComponentPlacement.RELATED)
																		.addComponent(
																				btnChooseRecordingDestination))
														.addComponent(
																tglbtnRecordMessages))
										.addContainerGap(274, Short.MAX_VALUE)));
		gl_panelRecording
				.setVerticalGroup(gl_panelRecording
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								gl_panelRecording
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												gl_panelRecording
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(
																lblRecirdingDirectory)
														.addComponent(
																_recordingDestinationLabel)
														.addComponent(
																btnChooseRecordingDestination))
										.addPreferredGap(
												ComponentPlacement.RELATED,
												264, Short.MAX_VALUE)
										.addComponent(tglbtnRecordMessages)
										.addContainerGap()));
		panelRecording.setLayout(gl_panelRecording);

	}

	/**
	 * Filter to select any image file with "jpg" extension, case insensitive.
	 */
	private FileFilter _imageFileFilter = new FileFilter() {

		@Override
		public boolean accept(File pathname) {
			if (pathname != null && pathname.isFile() && pathname.canRead()
					&& pathname.getName().toLowerCase().endsWith("jpg")) {
				return true;
			}
			return false;
		}
	};
	private JLabel _lblModuleActionReturn;

	/**
	 * 
	 * @author F270116
	 * 
	 */
	private class GetTestProxyAction implements ActionListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
		 * )
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			File currentDir = new File(System.getProperty("user.dir"));

			JFileChooser chooser = new JFileChooser(currentDir);
			chooser.setDialogTitle("Choose an image directory.");
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			int returnVal = chooser.showOpenDialog(_frame);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File directory = chooser.getSelectedFile();
				if (!directory.isDirectory()) {
					JOptionPane.showMessageDialog(_frame,
							"Not a directory, action aborted.");
					return;
				}

				if (directory.listFiles(_imageFileFilter).length == 0) {
					JOptionPane.showMessageDialog(_frame,
							"WARNING : no jpeg image file found in directory.");
				}
				SensorManagerProxy proxy = _client
						.getTestSensorManagerProxy(directory.getAbsolutePath());
				SensorManagerProxy old = _testProxies.put(
						directory.getAbsolutePath(), proxy);

				if (old == null) {
					_sensorManagerListComboboxModel
							.onTestSensorManagerRegistered(directory
									.getAbsolutePath());
				}

			} else {
				JOptionPane.showMessageDialog(_frame,
						"No directory selected, action aborted.");
			}
		}

	}

	/**
	 * 
	 * @author F270116
	 * 
	 */
	private class ChooseDestinationDirectory implements ActionListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
		 * )
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			File currentDir = new File(System.getProperty("user.dir"));

			JFileChooser chooser = new JFileChooser(currentDir);
			chooser.setDialogTitle("Choose the destination directory.");
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			int returnVal = chooser.showOpenDialog(_frame);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File directory = chooser.getSelectedFile();
				if (!directory.isDirectory()) {
					JOptionPane.showMessageDialog(_frame,
							"Not a directory, action aborted.");
					return;
				}

				if (directory.listFiles().length > 0) {
					JOptionPane
							.showMessageDialog(_frame,
									"WARNING : not an empty directory, I hope that's what you want.");
				}

				if (_recordingMachine.changeDirectory(directory
						.getAbsolutePath())) {
					_recordingDestinationLabel.setText(directory
							.getAbsolutePath());
				} else {
					JOptionPane.showMessageDialog(_frame,
							"ERROR : could not change directory for "
									+ directory.getAbsolutePath());
				}

			} else {
				JOptionPane.showMessageDialog(_frame,
						"No directory selected, action aborted.");
			}
		}

	}

	/**
	 * Internal method to parse the date in _sensorRequestDateTextField.
	 * 
	 * @return
	 */
	private Date getSelectedDate() {
		String dateStr = _sensorRequestDateTextField.getText();

		Date parsed = null;

		try {
			parsed = _dateFormat.parse(dateStr);

		} catch (ParseException e) {
			// nothing, it may arrive
		}
		if (parsed == null) {
			try {
				parsed = new Date(Long.parseLong(dateStr));
			} catch (NumberFormatException e) {
				// nothing, it may arrive
			}
		}

		if (parsed == null) {
			JOptionPane.showMessageDialog(_frame,
					"Incorrect date format, expecting either a long or "
							+ _dateFormat.toPattern() + " but received : ["
							+ dateStr + "]");
		}
		return parsed;
	}

	private class GetImageAction implements ActionListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
		 * )
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			String target = (String) _sensorManagerListComboboxModel
					.getSelectedItem();
			if (target == null) {
				JOptionPane.showMessageDialog(_frame,
						"Please select a sensor manager.");
				return;
			}

			SensorManagerProxy sensorManager = _testProxies.get(target);
			if (sensorManager == null) {
				sensorManager = _client.getSensorManagerProxy(target);
			}
			if (sensorManager == null) {
				JOptionPane.showMessageDialog(_frame,
						"Invalid sensor manager [" + target + "]");
				return;
			}
			Date selectedDate = getSelectedDate();
			if (selectedDate != null && selectedDate.getTime() != 0) {
				SensorManagerInterface.SMImage imageBuffer = sensorManager
						.getImage(_module.getModuleName(),
								selectedDate.getTime());
				if (imageBuffer.initialized) {
					_sensorImage.setIcon(new ImageIcon(imageBuffer.buffer));
					_lblImagedate.setText(DateFormat.getDateTimeInstance()
							.format(imageBuffer.timeStamp));

				} else {
					JOptionPane.showMessageDialog(_frame,
							"Error in commmunication with sensor.");
				}
			} else {
				final SensorManagerProxy proxy = sensorManager;
				Thread r = new Thread(new Runnable() {

					/*
					 * (non-Javadoc)
					 * 
					 * @see java.lang.Runnable#run()
					 */
					@Override
					public void run() {
						try {

							boolean loopAgain = true;
							int i = 0;
							SensorManagerInterface.SMImage imageBuffer = proxy
									.getImage(_module.getModuleName(), 0);
							do {
								i++;
								System.out.println("Getting image " + i);
								imageBuffer = proxy
										.getNextImage(_module.getModuleName());
								if (imageBuffer.initialized) {
									final SensorManagerInterface.SMImage localBuffer = imageBuffer;
									SwingUtilities.invokeLater(new Runnable() {
										public void run() {
											_sensorImage.setIcon(new ImageIcon(
													localBuffer.buffer));
											_lblImagedate
													.setText(DateFormat
															.getDateTimeInstance()
															.format(localBuffer.timeStamp));
										}

									});

								} else {
									System.err
											.println("Error in commmunication with sensor.");
									loopAgain = false;
								}
								System.gc();
							} while (loopAgain);
						} catch (Throwable e) {
							e.printStackTrace();
						}
					}

				});
				r.start();
			}

		}

	}

	private class StartSensorAction implements ActionListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
		 * )
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			String target = (String) _sensorManagerListComboboxModel
					.getSelectedItem();
			if (target == null) {
				JOptionPane.showMessageDialog(_frame,
						"Please select a sensor manager.");
				return;
			}

			SensorManagerProxy sensorManager = _testProxies.get(target);
			if (sensorManager == null) {
				sensorManager = _client.getSensorManagerProxy(target);
			}
			if (sensorManager == null) {
				JOptionPane.showMessageDialog(_frame,
						"Invalid sensor manager [" + target + "]");
				return;
			}
			sensorManager.onSetSensor(Command.START_SENSOR_RECORD, 0, 0);
		}

	}

	private class GetNextImageAction implements ActionListener {

		private boolean _last = false;

		GetNextImageAction(boolean returnLastImage) {
			_last = returnLastImage;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
		 * )
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			String target = (String) _sensorManagerListComboboxModel
					.getSelectedItem();
			if (target == null) {
				JOptionPane.showMessageDialog(_frame,
						"Please select a sensor manager.");
				return;
			}

			SensorManagerProxy sensorManager = _testProxies.get(target);
			if (sensorManager == null) {
				sensorManager = _client.getSensorManagerProxy(target);
			}
			if (sensorManager == null) {
				JOptionPane.showMessageDialog(_frame,
						"Invalid sensor manager [" + target + "]");
				return;
			}
			SensorManagerInterface.SMImage imageBuffer;
			if (_last) {
				imageBuffer = sensorManager.getLastImage(_module
						.getModuleName());
			} else {
				imageBuffer = sensorManager.getNextImage(_module
						.getModuleName());
			}
			if (imageBuffer.initialized) {
				ImageIcon icon = new ImageIcon(imageBuffer.buffer);
				File f = new File("output.jpg");
				FileOutputStream fo;
				_lblImagedate.setText(DateFormat.getDateTimeInstance().format(
						imageBuffer.timeStamp));

				try {
					fo = new FileOutputStream(f);
					fo.write(imageBuffer.buffer);
					fo.close();
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				_sensorImage.setIcon(icon);
			} else {
				JOptionPane.showMessageDialog(_frame,
						"Error in commmunication with sensor.");
			}

		}

	}

	/**
	 * Action for sending get Data request pushbutton. Shall send a getData
	 * request and push the result to the right table.
	 * 
	 * @author F270116
	 * 
	 */
	private class SendRequestAction implements ActionListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
		 * )
		 */
		@Override
		public void actionPerformed(ActionEvent arg0) {

			String dataSource = (String) _dataProducersComboBoxModel
					.getSelectedItem();
			if (dataSource != null && dataSource.isEmpty()) {
				dataSource = null;
			}
			String dataClass = (String) _classNamesComboBoxModel
					.getSelectedItem();
			if (dataClass != null && dataClass.isEmpty()) {
				dataClass = null;
			}
			String startDateStr = _getDataStartTextField.getText();

			Long startDate = null;
			try {
				if (startDateStr != null && startDateStr.length() > 0) {
					startDate = Long.parseLong(startDateStr);
				}
			} catch (Exception e) {
				// a real GUI will certainly be more user friendly here !
				e.printStackTrace();
			}

			String endDateStr = _getDataEndTextField.getText();
			Long endDate = null;
			try {
				if (endDateStr != null && endDateStr.length() > 0) {
					endDate = Long.parseLong(endDateStr);
				}
			} catch (Exception e) {
				// a real GUI will certainly be more user friendly here !
				e.printStackTrace();
			}
			// For the sake of the demonstration, we look for the first
			// repository to give an answer.
			// A real application could do that, or concatenate the results, or,
			// better, perform a join.
			List<String> repositories = _client.getRepositoriesNames();
			Iterator<String> it = repositories.iterator();
			List<AbstractDataFusionType> dataList = null;
			while (it.hasNext() && (dataList == null)) {
				DataRepositoryProxy proxy = _client.getDataRepositoryProxy(it
						.next());
				if (proxy != null) {
					dataList = proxy.getData(dataClass, dataSource, startDate,
							endDate);
					_dataListTableModel.setData(dataList);
				}
			}
		}

	}
}
