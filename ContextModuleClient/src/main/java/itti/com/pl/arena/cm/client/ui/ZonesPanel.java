package itti.com.pl.arena.cm.client.ui;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class ZonesPanel extends ContextModulePanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Create the dialog.
     */
    public ZonesPanel() {
        super();
        setLayout(new BorderLayout());
        {
            Component zonesPane = createPanelCamera();
            add(zonesPane, BorderLayout.EAST);
        }
    }

    private Component createPanelCamera() {

        JPanel panelZones = createJPanel();

        panelZones.add(createComboBoxRow(Messages.getString("ZonesPanel.0"))); //$NON-NLS-1$
        panelZones.add(new JLabel(Messages.getString("ZonesPanel.1"))); //$NON-NLS-1$
        panelZones.add(createComboBoxButtonRow(Messages.getString("ZonesPanel.2"))); //$NON-NLS-1$
        panelZones.add(createTextBoxButtonRow(Messages.getString("ZonesPanel.3"))); //$NON-NLS-1$

        panelZones.add(createEmptyRow());
        panelZones.add(createComboBoxButtonRow(Messages.getString("ZonesPanel.4"))); //$NON-NLS-1$
        panelZones.add(createTextBoxButtonRow(Messages.getString("ZonesPanel.5"))); //$NON-NLS-1$

        return panelZones;
    }
}
