package itti.com.pl.arena.cm.client.ui;

import itti.com.pl.arena.cm.client.ui.components.ButtonRow;
import itti.com.pl.arena.cm.client.ui.components.TextBoxRow;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

public class GeneralPanel extends ContextModulePanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private boolean connected = false;

    private TextBoxRow brokerUrlRow = null;
    private ButtonRow connectRow = null;
    private JTextArea logComponent = null;

    /**
     * Create the dialog.
     */
    public GeneralPanel() {

        super();
        setLayout(new BorderLayout());
        add(createPanelGeneral(), BorderLayout.CENTER);
    }

    private Component createPanelGeneral() {

        JPanel panelGeneral = new JPanel();
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();

        panelGeneral.setLayout(gbl);
        panelGeneral.setBorder(new EmptyBorder(10, 10, 10, 10));

        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(3,3,3,3);

        brokerUrlRow = createTextBoxRow("URL of the broker");
        brokerUrlRow.setText("127.0.0.1");
        gbl.setConstraints(brokerUrlRow, gbc);
        panelGeneral.add(brokerUrlRow);

        connectRow = createButtonRow("Connect");
        gbl.setConstraints(connectRow, gbc);
        panelGeneral.add(connectRow);
        connectRow.setButtonActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent arg0) {
                connectToCM();
            }
        });

        ButtonRow refreshComponent = createButtonRow("Refresh content");
        refreshComponent.setButtonActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent arg0) {
                refresh();
            }
        });
        gbl.setConstraints(refreshComponent, gbc);
        panelGeneral.add(refreshComponent);

        Component emptyComponent = createEmptyRow();
        gbl.setConstraints(emptyComponent, gbc);
        panelGeneral.add(emptyComponent);

        Component logDescComponent = createLabelRow("Connection log");
        gbl.setConstraints(logDescComponent, gbc);
        panelGeneral.add(logDescComponent);

        gbc.weighty = 10.0;
        logComponent = new JTextArea(30, 100);
        gbl.setConstraints(logComponent, gbc);
        panelGeneral.add(logComponent);

        return panelGeneral;
    }

    private void connectToCM() {
        if(!connected)
        {
            connect();
        }
        else
        {
            disconnect();
        }
    }

    private void connect() {
        String brokerUrl = brokerUrlRow.getText();
        setBrokerUrl(brokerUrl);
        try {
            connectToBroker();
            addLogMessage("Successfully connected\n");
            connected = true;
            connectRow.setButtonText("Disconnect");

        } catch (Exception e) {
            addLogMessage(e.getLocalizedMessage());
        }
    }

    private void disconnect() {
        try {
            disconnectFromBroker();
            addLogMessage("Successfully disconnected\n");
            connected = false;
            connectRow.setButtonText("Connect");

        } catch (Exception e) {
            addLogMessage(e.getLocalizedMessage());
        }
    }

    private void addLogMessage(String message){
        logComponent.append(message + "\n");        
    }

    protected void refresh() {

        getContextModule().getZoneNames(
                getContextModule().createSimpleNamedValue(getName(), ""));
    }
}
