package itti.com.pl.arena.cm.client.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JTabbedPane;

public class ParkingLotDialog extends JDialog {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            ParkingLotDialog dialog = new ParkingLotDialog();
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the dialog.
     */
    public ParkingLotDialog() {
        setTitle("Define parking lot");
        setBounds(100, 100, 600, 500);
        getContentPane().setLayout(new BorderLayout());
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton okButton = new JButton("OK");
                okButton.setActionCommand("OK");
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
            {
                JButton cancelButton = new JButton("Cancel");
                cancelButton.setActionCommand("Cancel");
                buttonPane.add(cancelButton);
            }
        }
        {
            JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.EAST);
            getContentPane().add(tabbedPane, BorderLayout.EAST);

            tabbedPane.addTab("General", createPanelGeneral());
            tabbedPane.addTab("Buildings", createPanelBuildings());
        }
    }

    private Component createPanelGeneral() {

        JPanel panelPlatform = new JPanel();
        panelPlatform.setLayout(new GridLayout(0, 1));

        {
            panelPlatform.add(new JLabel("General information"));
            panelPlatform.add(createTextBoxRow("Description"));
            panelPlatform.add(new JLabel());
            panelPlatform.add(createTextBoxRow("Country"));
            panelPlatform.add(createTextBoxRow("Town"));
            panelPlatform.add(createTextBoxRow("Street"));
            panelPlatform.add(new JLabel());

            panelPlatform.add(new JLabel("Parking lot coordinates"));
            panelPlatform.add(createTextBoxButtonRow("Add"));
            panelPlatform.add(createComboBoxButtonRow("Remove"));
            panelPlatform.add(new JLabel());
        }

            return panelPlatform;
        }

    private Component createTextBoxRow(String string) {
        
        JPanel panel = new JPanel(new GridLayout(1, 2));
        JLabel labelCountry = new JLabel(string);
        panel.add(labelCountry);
        JTextField textLabel = new JTextField();
        panel.add(textLabel);
        return panel;
    }

    private Component createComboBoxButtonRow(String string) {
        
        JPanel panel = new JPanel(new GridLayout(1, 2));
        JComboBox<String> textLabel = new JComboBox<String>();
        panel.add(textLabel);
        JButton button = new JButton(string);
        panel.add(textLabel);
        panel.add(button);
        return panel;
    }

    private Component createComboBoxRow(String string) {
        
        JPanel panel = new JPanel(new GridLayout(1, 2));
        JLabel textLabel = new JLabel(string);
        JComboBox<String> textComboBox = new JComboBox<String>();
        panel.add(textLabel);
        panel.add(textComboBox);
        return panel;
    }
    private Component createTextBoxButtonRow(String string) {
        
        JPanel panel = new JPanel(new GridLayout(1, 2));
        JTextField textField = new JTextField();
        panel.add(textField);
        JButton button = new JButton(string);
        panel.add(button);
        return panel;
    }

    private Component createPanelBuildings() {
        JPanel panelCamera = new JPanel();
        panelCamera.setLayout(new GridLayout(0, 1));
        
        {
            JLabel lblListOfBuildings = new JLabel("List of building");
            panelCamera.add(lblListOfBuildings);
        }
        {
            JComboBox<String> comboBox = new JComboBox<String>();
            comboBox.addItem("Building_A");
            panelCamera.add(comboBox);
        }
        {
            JPanel addRemoveCameraPanel = new JPanel(new GridLayout(1, 2));
            JButton btnNewButton = new JButton("Remove building");
            addRemoveCameraPanel.add(btnNewButton);

            JButton btnNewButton_1 = new JButton("Add building");
            addRemoveCameraPanel.add(btnNewButton_1);
            panelCamera.add(addRemoveCameraPanel);
        }

        {
            panelCamera.add(createComboBoxRow("Type"));
            panelCamera.add(createTextBoxRow("Description"));

            panelCamera.add(new JLabel("Building coordinates"));
            panelCamera.add(createTextBoxButtonRow("Add"));
            panelCamera.add(createComboBoxButtonRow("Remove"));

            return panelCamera;
        }
    }

}
