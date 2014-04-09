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

public class TruckDialog extends JDialog {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            TruckDialog dialog = new TruckDialog();
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the dialog.
     */
    public TruckDialog() {
        setTitle("Define platform");
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

            tabbedPane.addTab("Platform", createPlatformCamera());
            tabbedPane.addTab("Camera", createPanelCamera());
            
        }
    }

    private Component createPlatformCamera() {
        JPanel panelPlatform = new JPanel();
        panelPlatform.setLayout(new GridLayout(0, 1));
        
        {
            JLabel labelAngle = new JLabel("Platform dimensions");
            panelPlatform.add(labelAngle);

            JPanel widthPanel = new JPanel(new GridLayout(1, 2));
            JLabel labelAngleX = new JLabel("Width");
            widthPanel.add(labelAngleX);
            JTextField textAngleX = new JTextField();
            widthPanel.add(textAngleX);
            panelPlatform.add(widthPanel);

            JPanel heightPanel = new JPanel(new GridLayout(1, 2));
            JLabel labelAngleY = new JLabel("Height");
            heightPanel.add(labelAngleY);
            JTextField textAngleY = new JTextField();
            heightPanel.add(textAngleY);
            panelPlatform.add(heightPanel);

            JPanel lenghtPanel = new JPanel(new GridLayout(1, 2));
            JLabel labelLength = new JLabel("Lenght");
            lenghtPanel.add(labelLength);
            JTextField textLength = new JTextField();
            lenghtPanel.add(textLength);
            panelPlatform.add(lenghtPanel);
        }

            return panelPlatform;
        }

    private Component createPanelCamera() {
        JPanel panelCamera = new JPanel();
        panelCamera.setLayout(new GridLayout(0, 1));
        
        {
            JLabel lblListOfCameras = new JLabel("List of cameras");
            panelCamera.add(lblListOfCameras);
        }
        {
            JComboBox<String> comboBox = new JComboBox<String>();
            comboBox.addItem("CameraA");
            panelCamera.add(comboBox);
        }
        {
            JPanel addRemoveCameraPanel = new JPanel(new GridLayout(1, 2));
            JButton btnNewButton = new JButton("Remove camera");
            addRemoveCameraPanel.add(btnNewButton);

            JButton btnNewButton_1 = new JButton("Add camera");
            addRemoveCameraPanel.add(btnNewButton_1);
            panelCamera.add(addRemoveCameraPanel);
        }

        {
            JLabel labelAngle = new JLabel("Camera angle of view");
            panelCamera.add(labelAngle);

            JPanel widthPanel = new JPanel(new GridLayout(1, 2));
            JLabel labelAngleX = new JLabel("Width");
            widthPanel.add(labelAngleX);
            JTextField textAngleX = new JTextField();
            widthPanel.add(textAngleX);
            panelCamera.add(widthPanel);

            JPanel heightPanel = new JPanel(new GridLayout(1, 2));
            JLabel labelAngleY = new JLabel("Height");
            heightPanel.add(labelAngleY);
            JTextField textAngleY = new JTextField();
            heightPanel.add(textAngleY);
            panelCamera.add(heightPanel);
        }
        {
            JLabel labelPosition = new JLabel("Position on platform");
            panelCamera.add(labelPosition);

            JPanel xPanel = new JPanel(new GridLayout(1, 2));
            JLabel labelPositionX = new JLabel("X");
            xPanel.add(labelPositionX);
            JTextField textPositionX = new JTextField();
            xPanel.add(textPositionX);
            panelCamera.add(xPanel);

            JPanel yPanel = new JPanel(new GridLayout(1, 2));
            JLabel labelPositionY = new JLabel("Y");
            yPanel.add(labelPositionY);
            JTextField textPositionY = new JTextField();
            yPanel.add(textPositionY);
            panelCamera.add(yPanel);

            JPanel anglePanel = new JPanel(new GridLayout(1, 2));
            JLabel labelPositionAngle = new JLabel("Angle");
            anglePanel.add(labelPositionAngle);
            JComboBox<Integer> comboBoxAngle = new JComboBox<Integer>();
            comboBoxAngle.addItem(90);
            anglePanel.add(comboBoxAngle);
            panelCamera.add(anglePanel);
            return panelCamera;
        }
    }

}
