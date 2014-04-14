package itti.com.pl.arena.cm.client.ui;

import itti.com.pl.arena.cm.client.service.ContextModuleClientException;
import itti.com.pl.arena.cm.client.ui.components.ButtonButtonRow;
import itti.com.pl.arena.cm.client.ui.components.ButtonRow;
import itti.com.pl.arena.cm.client.ui.components.ComboBoxButtonRow;
import itti.com.pl.arena.cm.client.ui.components.ComboBoxRow;
import itti.com.pl.arena.cm.client.ui.components.TextBoxButtonRow;
import itti.com.pl.arena.cm.client.ui.components.TextBoxRow;
import itti.com.pl.arena.cm.service.LocalContextModule;

import java.awt.Component;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

public class ContextModulePanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private LocalContextModule contextModuleFacade = null;

    /**
     * Create the dialog.
     */
    public ContextModulePanel() {
    }

    protected TextBoxRow createTextBoxRow(String labelText) {

        return new TextBoxRow(labelText);
    }

    protected ComboBoxButtonRow createComboBoxButtonRow(String buttonText, List<String> content) {

        return new ComboBoxButtonRow(buttonText, content);
    }

    protected ComboBoxRow createComboBoxRow(String label, List<String> items) {
        return new ComboBoxRow(label, items);
    }

    protected TextBoxButtonRow createTextBoxButtonRow(String textBoxText, String buttonText) {
 
        return new TextBoxButtonRow(textBoxText, buttonText);
    }

    protected ButtonRow createButtonRow(String buttonText) {

        return new ButtonRow(buttonText);
    }

    protected ButtonButtonRow createButtonButtonRow(String buttonOneText, String buttonTwoText) {
        return new ButtonButtonRow(buttonOneText, buttonTwoText);
    }

    protected JPanel createJPanel() {
        JPanel panelPlatform = new JPanel();
        panelPlatform.setLayout(new GridLayout(12, 1, 10, 10));
        panelPlatform.setBorder(new EmptyBorder(10, 10, 10, 10));
        return panelPlatform;
    }

    protected Component createEmptyRow() {
        return new JLabel();
    }

    protected Component createLabelRow(String message) {
        return new JLabel(message);
    }

    public void setContextModule(LocalContextModule cmFacade){
        this.contextModuleFacade = cmFacade;
    }

    protected void setBrokerUrl(String brokerUrl) {
        contextModuleFacade.setBrokerUrl(brokerUrl);
    }

    protected void connectToBroker() throws ContextModuleClientException {
        contextModuleFacade.init();
    }

    protected void disconnectFromBroker() {
        contextModuleFacade.shutdown();
    }

    protected LocalContextModule getContextModule() {
        return contextModuleFacade;
    }
}
