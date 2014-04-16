package itti.com.pl.arena.cm.client.ui;

import itti.com.pl.arena.cm.client.ui.components.ButtonRow;
import itti.com.pl.arena.cm.client.ui.components.ComboBoxButtonRow;
import itti.com.pl.arena.cm.client.ui.components.ComboBoxRow;
import itti.com.pl.arena.cm.client.ui.components.TextBoxButtonRow;
import itti.com.pl.arena.cm.client.ui.components.TextBoxRow;
import itti.com.pl.arena.cm.dto.Zone;
import itti.com.pl.arena.cm.utils.helper.LocationHelper;
import itti.com.pl.arena.cm.utils.helper.StringHelper;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.JPanel;

public class ZonesPanel extends ContextModulePanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private ComboBoxRow parkingLotsComboBoxRow = null;
    private ComboBoxButtonRow zonesComboBoxButtonRow = null;
    private TextBoxButtonRow createZoneButtonRow = null;
    private ComboBoxButtonRow zoneCoordinateComboBoxButtonRow = null;
    private TextBoxRow coordinateXRow = null;
    private TextBoxRow coordinateYRow = null;
    private TextBoxRow coordinateZRow = null;
    private ButtonRow addCoordinateRow = null;

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

        //parking combo box
        parkingLotsComboBoxRow = createComboBoxRow(Messages.getString(Messages.getString("ZonesPanel.7")), null); //$NON-NLS-1$
        parkingLotsComboBoxRow.setOnChangeListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                parkingLotSelectionChanged();
            }
        });
        panelZones.add(parkingLotsComboBoxRow); //$NON-NLS-1$

        //zones combo box
        panelZones.add(createLabelRow(Messages.getString("ZonesPanel.1"))); //$NON-NLS-1$
        zonesComboBoxButtonRow = createComboBoxButtonRow(Messages.getString(Messages.getString("ZonesPanel.8")), null); //$NON-NLS-1$
        zonesComboBoxButtonRow.setOnChangeListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent event) {
				zoneSelectionChanged();
			}
		});
        zonesComboBoxButtonRow.setOnClickListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteSelectedZone();
			}
		});
        panelZones.add(zonesComboBoxButtonRow); //$NON-NLS-1$

        createZoneButtonRow = createTextBoxButtonRow(null, Messages.getString("ZonesPanel.3")); //$NON-NLS-1$
        panelZones.add(createZoneButtonRow);
        panelZones.add(createEmptyRow());

        zoneCoordinateComboBoxButtonRow = createComboBoxButtonRow(Messages.getString("ZonesPanel.4"), null); //$NON-NLS-1$
        panelZones.add(zoneCoordinateComboBoxButtonRow);
        coordinateXRow = createTextBoxRow("X");
        coordinateYRow = createTextBoxRow("Y");
        coordinateZRow = createTextBoxRow("Z");
        panelZones.add(coordinateXRow);
        panelZones.add(coordinateYRow);
        panelZones.add(coordinateZRow);

        addCoordinateRow = createButtonRow("Add");
        panelZones.add(addCoordinateRow);

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
        zonesComboBoxButtonRow.setComboBoxContent(null);
        populatePanelWithZoneData(null);
    }

    private void parkingLotSelectionChanged() {
        String selectedParkingLot = parkingLotsComboBoxRow.getSelectedItem();
        if (StringHelper.hasContent(selectedParkingLot)) {
            List<String> zones = getContextModuleAdapter().getListOfZones(selectedParkingLot);
            zonesComboBoxButtonRow.setComboBoxContent(zones);
            populatePanelWithZoneData(null);
        }
    }

    private void zoneSelectionChanged() {
        String selectedZone = zonesComboBoxButtonRow.getSelectedItem();
        if (StringHelper.hasContent(selectedZone)) {
            Zone zone = getContextModuleAdapter().getZoneDefinition(selectedZone);
            populatePanelWithZoneData(zone);
        }
    }

	private void deleteSelectedZone() {
        String selectedZone = zonesComboBoxButtonRow.getSelectedItem();
        if (StringHelper.hasContent(selectedZone)) {
            if(getContextModuleAdapter().removeZone(selectedZone)){
            	showMessage("Zone successfully removed from ontology");
            	//update list of zones
            	parkingLotSelectionChanged();
            }else{
            	showMessage("Could not remove zone from ontology");
            }
        }
	}

	private void populatePanelWithZoneData(Zone zone) {
		if(zone != null){
			String[] locations = LocationHelper.createStringsFromLocations(zone.getLocations());
			zoneCoordinateComboBoxButtonRow.setComboBoxContent(Arrays.asList(locations));
			if(locations.length > 0){
				createZoneButtonRow.setText(locations[0]);
				coordinateXRow.setText(StringHelper.toString(zone.getLocations()[0].getLongitude()));
				coordinateYRow.setText(StringHelper.toString(zone.getLocations()[0].getLatitude()));
				coordinateZRow.setText(StringHelper.toString(zone.getLocations()[0].getAltitude()));
			}else{
				createZoneButtonRow.setText(null);
				coordinateXRow.setText(null);
				coordinateYRow.setText(null);
				coordinateZRow.setText(null);
			}
		}else{
			zoneCoordinateComboBoxButtonRow.setComboBoxContent(null);
			createZoneButtonRow.setText(null);
			coordinateXRow.setText(null);
			coordinateYRow.setText(null);
			coordinateZRow.setText(null);
		}
	}
}
