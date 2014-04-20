package itti.com.pl.arena.cm.client.ui;

import itti.com.pl.arena.cm.client.ui.components.ButtonButtonRow;
import itti.com.pl.arena.cm.client.ui.components.ButtonRow;
import itti.com.pl.arena.cm.client.ui.components.ComboBoxButtonRow;
import itti.com.pl.arena.cm.client.ui.components.ComboBoxRow;
import itti.com.pl.arena.cm.client.ui.components.LabelComboBoxRow;
import itti.com.pl.arena.cm.client.ui.components.TextBoxButtonRow;
import itti.com.pl.arena.cm.client.ui.components.LabelTextBoxRow;
import itti.com.pl.arena.cm.dto.Location;
import itti.com.pl.arena.cm.dto.Zone;
import itti.com.pl.arena.cm.utils.helper.LocationHelper;
import itti.com.pl.arena.cm.utils.helper.LocationHelperException;
import itti.com.pl.arena.cm.utils.helper.NumbersHelper;
import itti.com.pl.arena.cm.utils.helper.StringHelper;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
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

    private LabelComboBoxRow parkingLotsComboBoxRow = null;
    private ComboBoxButtonRow zonesComboBoxButtonRow = null;
    private TextBoxButtonRow createZoneButtonRow = null;
    private ComboBoxRow zoneCoordinateComboBoxRow = null;
    private LabelTextBoxRow planceNameRow = null;
    private ButtonRow zoneCoordinateButtonRow = null;
    private LabelTextBoxRow coordinateXRow = null;
    private LabelTextBoxRow coordinateYRow = null;
    private LabelTextBoxRow coordinateZRow = null;
    private ButtonButtonRow clearAddCoordinateRow = null;

    /**
     * Create the dialog.
     */
    public ZonesPanel() {
        super();

        Component imageComponent = createImagePanel(Messages.getString("ZonesPanel.6")); //$NON-NLS-1$
        add(imageComponent, BorderLayout.WEST);

        Component zonesPane = createPanelCamera();
        zonesPane.setMaximumSize(new Dimension(getWidth() / 2, getHeight() / 2));
        add(zonesPane, BorderLayout.EAST);
    }

    private Component createPanelCamera() {

        JPanel panelZones = createJPanel();

        // parking combo box
        parkingLotsComboBoxRow = createLabelComboBoxRow("Parking lots", null); //$NON-NLS-1$
        parkingLotsComboBoxRow.setOnChangeListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                parkingLotSelectionChanged();
            }
        });
        panelZones.add(parkingLotsComboBoxRow); //$NON-NLS-1$

        // zones combo box
        panelZones.add(createLabelRow(Messages.getString("ZonesPanel.1"))); //$NON-NLS-1$
        zonesComboBoxButtonRow = createComboBoxButtonRow("Remove zone"  , null); //$NON-NLS-1$
        zonesComboBoxButtonRow.setOnChangeListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                zoneSelectionChanged();
            }
        });
        zonesComboBoxButtonRow.setOnClickListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                removeSelectedZone();
            }
        });
        panelZones.add(zonesComboBoxButtonRow); //$NON-NLS-1$

        createZoneButtonRow = createTextBoxButtonRow(null, Messages.getString("ZonesPanel.3")); //$NON-NLS-1$
        createZoneButtonRow.setOnButtonClickListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                addZone();
            }
        });
        panelZones.add(createZoneButtonRow);
        panelZones.add(createEmptyRow());

        planceNameRow = new LabelTextBoxRow("Plane name", null);
        panelZones.add(planceNameRow);

        panelZones.add(createLabelRow("Zone coordinates"));
        zoneCoordinateComboBoxRow = createComboBoxRow(null);
        zoneCoordinateComboBoxRow.setOnChangeListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent arg0) {
                coordinateSelectionChanged();
            }
        });
        panelZones.add(zoneCoordinateComboBoxRow);

        zoneCoordinateButtonRow = createButtonRow(Messages.getString("ZonesPanel.4")); //$NON-NLS-1$
        zoneCoordinateButtonRow.setButtonActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                removeCoordinate();
            }
        });
        panelZones.add(zoneCoordinateButtonRow);

        coordinateXRow = createTextBoxRow("X", null);
        coordinateYRow = createTextBoxRow("Y", null);
        coordinateZRow = createTextBoxRow("Z", null);
        panelZones.add(coordinateXRow);
        panelZones.add(coordinateYRow);
        panelZones.add(coordinateZRow);

        clearAddCoordinateRow = createButtonButtonRow("Clear", "Add");
        clearAddCoordinateRow.setOnFirstButtonClickListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent arg0) {
                clearCoordinateFields();
            }
        });
        clearAddCoordinateRow.setOnSecondButtonClickListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent arg0) {
                addCoordinate();
            }
        });
        panelZones.add(clearAddCoordinateRow);

        return panelZones;
    }

    private void clearCoordinateFields() {
       coordinateXRow.setText(null);
       coordinateYRow.setText(null);
       coordinateZRow.setText(null);
    }

    protected void addCoordinate() {
        if(!NumbersHelper.isDouble(coordinateXRow.getText())){
            showMessage("Value for the X coordinate is not a valid number");
        }
        if(!NumbersHelper.isDouble(coordinateYRow.getText())){
            showMessage("Value for the Y coordinate is not a valid number");
        }
        if(!NumbersHelper.isDouble(coordinateZRow.getText())){
            showMessage("Value for the Z coordinate is not a valid number");
        }
        zoneCoordinateComboBoxRow.addItem(
        LocationHelper.createStringFromLocation(new Location(
                NumbersHelper.getDoubleFromString(coordinateXRow.getText()),
                NumbersHelper.getDoubleFromString(coordinateYRow.getText()),
                0,
                NumbersHelper.getDoubleFromString(coordinateZRow.getText())
                )));
        clearCoordinateFields();
    }

    @Override
    protected void onCancelClick() {
    }

    @Override
    protected void onSaveClick() {
        String parkingLot = parkingLotsComboBoxRow.getSelectedItem();
        String zoneName = zonesComboBoxButtonRow.getSelectedItem();
        if(!StringHelper.hasContent(zoneName)){
            showMessage("Please specify zone name first");
        }else{
            String[] coordinates = zoneCoordinateComboBoxRow.getItems();
            getContextModuleAdapter().updateZone(zoneName, parkingLot, coordinates);
        }
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

    private void zoneSelectionChanged() {
        String selectedZone = zonesComboBoxButtonRow.getSelectedItem();
        if (StringHelper.hasContent(selectedZone)) {
            Zone zone = getContextModuleAdapter().getZoneDefinition(selectedZone);
            if (zone != null) {
                createZoneButtonRow.setText(zone.getId());
                String[] locations = LocationHelper.createStringsFromLocations(zone.getLocations());
                zoneCoordinateComboBoxRow.setItems(Arrays.asList(locations));
            }else{
                createZoneButtonRow.setText(null);  
                zoneCoordinateComboBoxRow.setItems(null);
            }
        }else{
            createZoneButtonRow.setText(null);  
            zoneCoordinateComboBoxRow.setItems(null);            
        }
    }

    private void removeSelectedZone() {
        removeZone();
    }

    private void removeZone(){
        String selectedZone = zonesComboBoxButtonRow.getSelectedItem();
        if (StringHelper.hasContent(selectedZone)) {
            if (getContextModuleAdapter().removeZone(selectedZone)) {
                showMessage("Zone successfully removed from ontology");
                // update list of zones
                parkingLotSelectionChanged();
            } else {
                showMessage("Could not remove zone from ontology");
            }
        }
    }

    private void addZone() {
        String parkingLotName = parkingLotsComboBoxRow.getSelectedItem();
        String zoneName = createZoneButtonRow.getText();
        if (StringHelper.hasContent(parkingLotName) && StringHelper.hasContent(zoneName)) {
            if (getContextModuleAdapter().updateZone(zoneName, parkingLotName, null)) {
                showMessage("Zone successfully added to the ontology");
                parkingLotSelectionChanged();
            } else {
                showMessage("Could not add zone to the ontology");
            }
        }
    }

    private void removeCoordinate() {
        String coordinate = zoneCoordinateComboBoxRow.getSelectedItem();
        zoneCoordinateComboBoxRow.removeItem(coordinate);
    }

    private void coordinateSelectionChanged() {
        String selectedCoordinate = zoneCoordinateComboBoxRow.getSelectedItem();

        Double x = null;
        Double y = null;
        Double z = null;
        if (StringHelper.hasContent(selectedCoordinate)) {
            try {
                Location location = LocationHelper.getLocationFromString(selectedCoordinate);
                x = location.getLongitude();
                y = location.getLatitude();
                z = location.getAltitude();
            } catch (LocationHelperException e) {
                showMessage("Cannot parse selected value into location");
            }
        }
        coordinateXRow.setText(x == null ? null : StringHelper.toString(x));
        coordinateYRow.setText(y == null ? null : StringHelper.toString(y));
        coordinateZRow.setText(y == null ? null : StringHelper.toString(z));
    }

}
