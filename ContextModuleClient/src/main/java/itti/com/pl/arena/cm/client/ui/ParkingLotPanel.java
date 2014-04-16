package itti.com.pl.arena.cm.client.ui;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class ParkingLotPanel extends ContextModulePanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Create the dialog.
     */
    public ParkingLotPanel() {

        super();

        Component imageComponent = createImagePanel(Messages.getString("ParkingLotPanel.10")); //$NON-NLS-1$
        add(imageComponent, BorderLayout.WEST);

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        add(tabbedPane, BorderLayout.EAST);

        tabbedPane.addTab(Messages.getString("ParkingLotPanel.0"), createPanelGeneral()); //$NON-NLS-1$
        tabbedPane.addTab(Messages.getString("ParkingLotPanel.1"), createPanelBuildings()); //$NON-NLS-1$
    }

    private Component createPanelGeneral() {

        JPanel panelPlatform = createJPanel();

        panelPlatform.add(createComboBoxButtonRow("Delete parking lot", null)); //$NON-NLS-1$
        panelPlatform.add(createTextBoxButtonRow(null, "Add parking lot")); //$NON-NLS-1$

        panelPlatform.add(createLabelRow(Messages.getString("ParkingLotPanel.2"))); //$NON-NLS-1$
        panelPlatform.add(createTextBoxRow(Messages.getString("ParkingLotPanel.3"), null)); //$NON-NLS-1$
        panelPlatform.add(createTextBoxRow(Messages.getString("ParkingLotPanel.4"), null)); //$NON-NLS-1$
        panelPlatform.add(createTextBoxRow(Messages.getString("ParkingLotPanel.5"), null)); //$NON-NLS-1$
        panelPlatform.add(createTextBoxRow(Messages.getString("ParkingLotPanel.6"), null)); //$NON-NLS-1$

        panelPlatform.add(createEmptyRow());
        panelPlatform.add(createLabelRow(Messages.getString("ParkingLotPanel.7"))); //$NON-NLS-1$
        panelPlatform.add(createComboBoxButtonRow(Messages.getString("ParkingLotPanel.9"), null)); //$NON-NLS-1$
        panelPlatform.add(createTextBoxRow("X", null)); //$NON-NLS-1$
        panelPlatform.add(createTextBoxRow("Y", null)); //$NON-NLS-1$
        panelPlatform.add(createButtonRow("Add")); //$NON-NLS-1$

        return panelPlatform;
    }

    private Component createPanelBuildings() {

        JPanel panelCamera = createJPanel();

        panelCamera.add(createLabelRow(Messages.getString("ParkingLotPanel.18"))); //$NON-NLS-1$

        panelCamera.add(createComboBoxButtonRow(Messages.getString("ParkingLotPanel.19"), null)); //$NON-NLS-1$
            panelCamera.add(createTextBoxButtonRow(null, Messages.getString("ParkingLotPanel.20"))); //$NON-NLS-1$

            panelCamera.add(createEmptyRow());
            panelCamera.add(createLabelComboBoxRow(Messages.getString("ParkingLotPanel.13"), null)); //$NON-NLS-1$
            panelCamera.add(createTextBoxRow(Messages.getString("ParkingLotPanel.14"), null)); //$NON-NLS-1$

            panelCamera.add(createEmptyRow());
            panelCamera.add(createLabelRow(Messages.getString("ParkingLotPanel.15"))); //$NON-NLS-1$
            panelCamera.add(createComboBoxButtonRow(Messages.getString("ParkingLotPanel.17"), null)); //$NON-NLS-1$
            panelCamera.add(createTextBoxRow("X", null)); //$NON-NLS-1$
            panelCamera.add(createTextBoxRow("Y", null)); //$NON-NLS-1$
            panelCamera.add(createButtonRow("Add")); //$NON-NLS-1$


            return panelCamera;
    }

    @Override
    protected void onCancelClick() {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void onSaveClick() {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void onRefreshClick() {
        // TODO Auto-generated method stub
        
    }

}
