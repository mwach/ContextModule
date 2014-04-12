package itti.com.pl.arena.cm.client.ui;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;

public class TruckPanel extends ContextModulePanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Create the dialog.
     */
    public TruckPanel() {
        super();
        setLayout(new BorderLayout());
        {
            JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
            add(tabbedPane, BorderLayout.EAST);

            tabbedPane.addTab(Messages.getString("TruckPanel.0"), createPlatformPanel()); //$NON-NLS-1$
            tabbedPane.addTab(Messages.getString("TruckPanel.1"), createCameraPanel()); //$NON-NLS-1$

        }
    }

    private Component createPlatformPanel() {
        JPanel panelPlatform = createJPanel();
        panelPlatform.add(new JLabel(Messages.getString("TruckPanel.2"))); //$NON-NLS-1$

        panelPlatform.add(createTextBoxRow(Messages.getString("TruckPanel.3"))); //$NON-NLS-1$
        panelPlatform.add(createTextBoxRow(Messages.getString("TruckPanel.4"))); //$NON-NLS-1$
        panelPlatform.add(createTextBoxRow(Messages.getString("TruckPanel.5"))); //$NON-NLS-1$

        panelPlatform.add(new JLabel());

        panelPlatform.add(new JLabel(Messages.getString("TruckPanel.6"))); //$NON-NLS-1$

        panelPlatform.add(createTextBoxRow(Messages.getString("TruckPanel.7"))); //$NON-NLS-1$
        panelPlatform.add(createTextBoxRow(Messages.getString("TruckPanel.8"))); //$NON-NLS-1$

        return panelPlatform;
    }

    private Component createCameraPanel() {
        JPanel panelCamera = createJPanel();

        panelCamera.add(new JLabel(Messages.getString("TruckPanel.19"))); //$NON-NLS-1$
        panelCamera.add(createComboBoxButtonRow(Messages.getString("TruckPanel.20"))); //$NON-NLS-1$
        panelCamera.add(createTextBoxButtonRow(Messages.getString("TruckPanel.21"))); //$NON-NLS-1$

        panelCamera.add(new JLabel());
        panelCamera.add(new JLabel(Messages.getString("TruckPanel.12"))); //$NON-NLS-1$
        panelCamera.add(createTextBoxRow(Messages.getString("TruckPanel.13"))); //$NON-NLS-1$
        panelCamera.add(createTextBoxRow(Messages.getString("TruckPanel.14"))); //$NON-NLS-1$

        panelCamera.add(new JLabel());
        panelCamera.add(new JLabel(Messages.getString("TruckPanel.15"))); //$NON-NLS-1$
        panelCamera.add(createTextBoxRow(Messages.getString("TruckPanel.16"))); //$NON-NLS-1$
        panelCamera.add(createTextBoxRow(Messages.getString("TruckPanel.17"))); //$NON-NLS-1$
        panelCamera.add(createTextBoxRow(Messages.getString("TruckPanel.18"))); //$NON-NLS-1$
        
        return panelCamera;
    }
}
