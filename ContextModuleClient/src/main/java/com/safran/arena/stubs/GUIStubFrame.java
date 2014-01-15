package com.safran.arena.stubs;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import com.safran.arena.impl.Client;
import com.safran.arena.impl.DataRepositoryProxy;
import com.safran.arena.impl.ModuleImpl;

import eu.arena_fp7._1.AbstractDataFusionType;

/**
 * This stub has for purpose to test the following features:
 * <ul>
 * <li>subscribe to module register / unregister</li>
 * <li>subscribe to all data</li>
 * <li>get filtered data from the repository</li>
 * </ul>
 * It has been coded using Eclipse Juno built-in GUI editor. It may be used as an example for coding such an
 * application. WARNING: this version does not unregister itself while closing. Unregistering itself before quitting
 * should be mandatory for a real application.
 * 
 * @author F270116
 * 
 */
@SuppressWarnings("rawtypes")
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

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    // The Client is the representative of the Integration
                    // Platform. It has to be instantiated and connected first.
                    Client client = new Client();
                    client.connectToServer();

                    GUIStubFrame window = new GUIStubFrame(client);
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
        _client.registerModuleAsDataConsumer(_module);
        _client.registerModuleAsManager(_module);
        // various model initialization
        // remark: these models here implement one or the other module
        // interface, but they are not registered to the CLient; they are
        // registered to the ModuleImpl, which will perform the dispatching fo
        // events. Why? Because they are not modules, they are <i>parts</i> of a
        // module.
        _eventTableModel = new EventListTableModel(_client, _module);
        _modulesTableModel = new ModuleListTableModel(_client, _module);
        _dataProducersComboBoxModel = new ModuleListComboboxModel(_client, _module);
        _classNamesComboBoxModel = new ClassNamesComboboxModel();
        _dataListTableModel = new DataListeTableModel();
        // standard GUI initialization
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    @SuppressWarnings({ "unchecked" })
    private void initialize() {
        _frame = new JFrame();
        _frame.setBounds(100, 100, 517, 398);
        _frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
        gl_getDataPanel.setHorizontalGroup(gl_getDataPanel.createParallelGroup(Alignment.LEADING).addGroup(
                gl_getDataPanel
                        .createSequentialGroup()
                        .addContainerGap()
                        .addGroup(
                                gl_getDataPanel
                                        .createParallelGroup(Alignment.LEADING)
                                        .addComponent(getDataScrollPane, GroupLayout.DEFAULT_SIZE, 476, Short.MAX_VALUE)
                                        .addGroup(
                                                gl_getDataPanel
                                                        .createSequentialGroup()
                                                        .addComponent(lblDataSource)
                                                        .addPreferredGap(ComponentPlacement.UNRELATED)
                                                        .addComponent(_dataSourceCombo, GroupLayout.PREFERRED_SIZE,
                                                                GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(ComponentPlacement.UNRELATED)
                                                        .addComponent(lblDataClass)
                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                        .addComponent(_classComboBox, GroupLayout.PREFERRED_SIZE,
                                                                GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addGroup(
                                                gl_getDataPanel
                                                        .createSequentialGroup()
                                                        .addComponent(lblStartDate)
                                                        .addPreferredGap(ComponentPlacement.UNRELATED)
                                                        .addComponent(_getDataStartTextField, GroupLayout.PREFERRED_SIZE,
                                                                GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(ComponentPlacement.UNRELATED)
                                                        .addComponent(lblEndDate)
                                                        .addPreferredGap(ComponentPlacement.UNRELATED)
                                                        .addComponent(_getDataEndTextField, GroupLayout.PREFERRED_SIZE,
                                                                GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addComponent(btnSendRequest)).addContainerGap()));
        gl_getDataPanel.setVerticalGroup(gl_getDataPanel.createParallelGroup(Alignment.LEADING).addGroup(
                gl_getDataPanel
                        .createSequentialGroup()
                        .addContainerGap()
                        .addGroup(
                                gl_getDataPanel
                                        .createParallelGroup(Alignment.BASELINE)
                                        .addComponent(lblDataSource)
                                        .addComponent(_dataSourceCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                                GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblDataClass)
                                        .addComponent(_classComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                                GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(
                                gl_getDataPanel
                                        .createParallelGroup(Alignment.BASELINE)
                                        .addComponent(lblStartDate)
                                        .addComponent(_getDataStartTextField, GroupLayout.PREFERRED_SIZE,
                                                GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblEndDate)
                                        .addComponent(_getDataEndTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                                GroupLayout.PREFERRED_SIZE)).addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(btnSendRequest).addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(getDataScrollPane, GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE).addContainerGap()));

        _getDataTable = new JTable();
        _getDataTable.setModel(_dataListTableModel);
        getDataScrollPane.setViewportView(_getDataTable);
        getDataPanel.setLayout(gl_getDataPanel);

    }

    /**
     * Action for sending get Data request pushbutton. Shall send a getData request and push the result to the right
     * table.
     * 
     * @author F270116
     * 
     */
    private class SendRequestAction implements ActionListener {

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent )
         */
        @Override
        public void actionPerformed(ActionEvent arg0) {

            String dataSource = (String) _dataProducersComboBoxModel.getSelectedItem();
            if (dataSource != null && dataSource.isEmpty()) {
                dataSource = null;
            }
            String dataClass = (String) _classNamesComboBoxModel.getSelectedItem();
            if (dataClass != null && dataClass.isEmpty()) {
                dataClass = null;
            }
            String startDateStr = _getDataStartTextField.getText();

            Double startDate = null;
            try {
                startDate = Double.parseDouble(startDateStr);
            } catch (Exception e) {
                // a real GUI will certainly be more user friendly here !
                e.printStackTrace();
            }

            String endDateStr = _getDataEndTextField.getText();
            Double endDate = null;
            try {
                endDate = Double.parseDouble(endDateStr);
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
                DataRepositoryProxy proxy = _client.getDataRepositoryProxy(it.next());
                if (proxy != null) {
                    dataList = proxy.getData(dataClass, dataSource, startDate, endDate);
                    _dataListTableModel.setData(dataList);
                }
            }
        }

    }
}