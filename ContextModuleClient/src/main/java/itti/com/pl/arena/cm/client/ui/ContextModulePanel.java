package itti.com.pl.arena.cm.client.ui;

import itti.com.pl.arena.cm.client.service.ContextModuleAdapter;
import itti.com.pl.arena.cm.client.ui.components.ButtonButtonRow;
import itti.com.pl.arena.cm.client.ui.components.ButtonRow;
import itti.com.pl.arena.cm.client.ui.components.ComboBoxButtonRow;
import itti.com.pl.arena.cm.client.ui.components.ComboBoxRow;
import itti.com.pl.arena.cm.client.ui.components.TextBoxButtonRow;
import itti.com.pl.arena.cm.client.ui.components.TextBoxRow;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

public abstract class ContextModulePanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private ContextModuleAdapter contextModuleAdapter = null;

    /**
     * Create the dialog.
     */
    public ContextModulePanel() {
        setLayout(new BorderLayout());
        add(createButtonsMenu(), BorderLayout.SOUTH);
    }

    private Component createButtonsMenu() {
        JPanel buttonsPanel = createJPanel();
        buttonsPanel.setLayout(new GridLayout(1, 3, 5, 5));
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if(getContextModuleAdapter().isConnected()){
                    onRefreshClick();
                }else{
                    JOptionPane.showMessageDialog(null, "Please connect to the CM server first");
                }
                
            }
        });
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if(getContextModuleAdapter().isConnected()){
                    onSaveClick();
                }else{
                    JOptionPane.showMessageDialog(null, "Please connect to the CM server first");
                }
                
            }
        });
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if(getContextModuleAdapter().isConnected()){
                    onCancelClick();
                }else{
                    JOptionPane.showMessageDialog(null, "Please connect to the CM server first");
                }
                
            }
        });

        buttonsPanel.add(refreshButton);
        buttonsPanel.add(saveButton);
        buttonsPanel.add(cancelButton);
        return buttonsPanel;
    }

    protected abstract void onCancelClick();

    protected abstract void onSaveClick();

    protected abstract void onRefreshClick();

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

    public void setContextModule(ContextModuleAdapter cmAdapter){
        this.contextModuleAdapter = cmAdapter;
    }

    protected ContextModuleAdapter getContextModuleAdapter() {
        return contextModuleAdapter;
    }
}
