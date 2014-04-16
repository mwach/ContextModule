package itti.com.pl.arena.cm.client.ui;

import itti.com.pl.arena.cm.client.ui.components.ButtonRow;
import itti.com.pl.arena.cm.client.ui.components.LabelTextBoxRow;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

public class GeneralPanel extends ContextModulePanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private LabelTextBoxRow brokerUrlRow = null;
    private ButtonRow connectRow = null;
    private JTextArea logComponent = null;

    /**
     * Create the dialog.
     */
    public GeneralPanel() {

        super();
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

        brokerUrlRow = createTextBoxRow(Messages.getString("GeneralPanel.0"), null); //$NON-NLS-1$
        brokerUrlRow.setText(Messages.getString("GeneralPanel.1")); //$NON-NLS-1$
        gbl.setConstraints(brokerUrlRow, gbc);
        panelGeneral.add(brokerUrlRow);

        connectRow = createButtonRow(Messages.getString("GeneralPanel.2")); //$NON-NLS-1$
        gbl.setConstraints(connectRow, gbc);
        panelGeneral.add(connectRow);
        connectRow.setButtonActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent arg0) {
                connectToCM();
            }
        });

        Component emptyComponent = createEmptyRow();
        gbl.setConstraints(emptyComponent, gbc);
        panelGeneral.add(emptyComponent);

        Component logDescComponent = createLabelRow(Messages.getString("GeneralPanel.3")); //$NON-NLS-1$
        gbl.setConstraints(logDescComponent, gbc);
        panelGeneral.add(logDescComponent);

        gbc.weighty = 10.0;
        logComponent = new JTextArea(30, 100);
        gbl.setConstraints(logComponent, gbc);
        panelGeneral.add(logComponent);

        return panelGeneral;
    }

    private void connectToCM() {
        if(!getContextModuleAdapter().isConnected())
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
        try{
            getContextModuleAdapter().connect(brokerUrl);
            addLogMessage(Messages.getString("GeneralPanel.4")); //$NON-NLS-1$
            connectRow.setButtonText(Messages.getString("GeneralPanel.5")); //$NON-NLS-1$
        }catch(RuntimeException exc){
            addLogMessage(Messages.getString("GeneralPanel.6") + exc.getLocalizedMessage());             //$NON-NLS-1$
        }
    }

    private void disconnect() {
        try {
            getContextModuleAdapter().disconnect();
            addLogMessage(Messages.getString("GeneralPanel.7")); //$NON-NLS-1$
            connectRow.setButtonText(Messages.getString("GeneralPanel.8")); //$NON-NLS-1$

        } catch (Exception e) {
            addLogMessage(Messages.getString("GeneralPanel.9") + e.getLocalizedMessage()); //$NON-NLS-1$
        }
    }

    private void addLogMessage(String message){
        logComponent.append(message + Messages.getString("GeneralPanel.10"));         //$NON-NLS-1$
    }

    @Override
    protected void onCancelClick() {
        JOptionPane.showMessageDialog(null, Messages.getString("GeneralPanel.11")); //$NON-NLS-1$
    }

    @Override
    protected void onSaveClick() {
        JOptionPane.showMessageDialog(null, Messages.getString("GeneralPanel.12")); //$NON-NLS-1$
    }

    @Override
    protected void onRefreshClick() {
        JOptionPane.showMessageDialog(null, Messages.getString("GeneralPanel.13")); //$NON-NLS-1$
    }
}
