package itti.com.pl.arena.cm.client.ui;

import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class ContextModulePanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Create the dialog.
     */
    public ContextModulePanel() {
    }

    protected Component createTextBoxRow(String string) {
        
        JPanel panel = new JPanel(new GridLayout(1, 2));
        JLabel labelCountry = new JLabel(string);
        panel.add(labelCountry);
        JTextField textLabel = new JTextField();
        panel.add(textLabel);
        return panel;
    }

    protected Component createComboBoxButtonRow(String string) {
        
        JPanel panel = new JPanel(new GridLayout(1, 2));
        JComboBox<String> textLabel = new JComboBox<String>();
        panel.add(textLabel);
        JButton button = new JButton(string);
        panel.add(textLabel);
        panel.add(button);
        return panel;
    }

    protected Component createComboBoxRow(String string) {
        
        JPanel panel = new JPanel(new GridLayout(1, 2));
        JLabel textLabel = new JLabel(string);
        JComboBox<String> textComboBox = new JComboBox<String>();
        panel.add(textLabel);
        panel.add(textComboBox);
        return panel;
    }
    protected Component createTextBoxButtonRow(String string) {
        
        JPanel panel = new JPanel(new GridLayout(1, 2));
        JTextField textField = new JTextField();
        panel.add(textField);
        JButton button = new JButton(string);
        panel.add(button);
        return panel;
    }

    protected Component createButtonButtonRow(String string, String string2) {
        JPanel panel = new JPanel(new GridLayout(1, 2));
        JButton buttonOne = new JButton(string);
        panel.add(buttonOne);
        JButton buttonTwo = new JButton(string2);
        panel.add(buttonTwo);
        return panel;
    }

    protected JPanel createJPanel(){
        JPanel panelPlatform = new JPanel();
        panelPlatform.setLayout(new GridLayout(12, 1, 10, 10));
        panelPlatform.setBorder(new EmptyBorder(10, 10, 10, 10));
        return panelPlatform;
    }

    protected Component createEmptyRow() {
        return new JLabel();
    }

}
