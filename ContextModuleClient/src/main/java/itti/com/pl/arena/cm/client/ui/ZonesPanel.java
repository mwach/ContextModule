package itti.com.pl.arena.cm.client.ui;

import itti.com.pl.arena.cm.client.ui.components.ComboBoxButtonRow;
import itti.com.pl.arena.cm.client.ui.components.ComboBoxRow;
import itti.com.pl.arena.cm.utils.helper.StringHelper;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JPanel;

public class ZonesPanel extends ContextModulePanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private ComboBoxRow parkingLotsComboBoxRow = null;
    private ComboBoxButtonRow zonesComboBoxButtonRow = null;

    /**
     * Create the dialog.
     */
    public ZonesPanel() {
        super();

        Component imageComponent = createImagePanel(Messages.getString("ZonesPanel.6")); //$NON-NLS-1$
        add(imageComponent, BorderLayout.WEST);

        Component zonesPane = createPanelCamera();
        add(zonesPane, BorderLayout.EAST);
    }

    private Component createPanelCamera() {

        JPanel panelZones = createJPanel();

        parkingLotsComboBoxRow = createComboBoxRow(Messages.getString(Messages.getString("ZonesPanel.7")), null); //$NON-NLS-1$
        parkingLotsComboBoxRow.setOnChangeListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                parkingLotSelectionChanged();
            }
        });

        panelZones.add(parkingLotsComboBoxRow); //$NON-NLS-1$
        panelZones.add(createLabelRow(Messages.getString("ZonesPanel.1"))); //$NON-NLS-1$
        zonesComboBoxButtonRow = createComboBoxButtonRow(Messages.getString(Messages.getString("ZonesPanel.8")), null); //$NON-NLS-1$
        panelZones.add(zonesComboBoxButtonRow); //$NON-NLS-1$
        panelZones.add(createTextBoxButtonRow(null, Messages.getString("ZonesPanel.3"))); //$NON-NLS-1$

        panelZones.add(createEmptyRow());
        panelZones.add(createComboBoxButtonRow(Messages.getString("ZonesPanel.4"), null)); //$NON-NLS-1$
        panelZones.add(createTextBoxRow("X")); //$NON-NLS-1$
        panelZones.add(createTextBoxRow("Y")); //$NON-NLS-1$
        panelZones.add(createButtonRow("Add")); //$NON-NLS-1$


        return panelZones;
    }

    @Override
    protected void onCancelClick() {
    }

    @Override
    protected void onSaveClick() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onRefreshClick() {
        List<String> parkingLots = getContextModuleAdapter().getListOfParkingLots();
        parkingLotsComboBoxRow.setComboBoxContent(parkingLots);
    }

    private void parkingLotSelectionChanged() {
        String selectedParkingLot = parkingLotsComboBoxRow.getSelectedItem();
        if (StringHelper.hasContent(selectedParkingLot)) {
            List<String> zones = getContextModuleAdapter().getListOfZones(selectedParkingLot);
            zonesComboBoxButtonRow.setComboBoxContent(zones);
        }
    }

}
