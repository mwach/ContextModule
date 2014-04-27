package itti.com.pl.arena.cm.client.ui;

import itti.com.pl.arena.cm.client.ui.components.ButtonRow;
import itti.com.pl.arena.cm.client.ui.components.ComboBoxButtonRow;
import itti.com.pl.arena.cm.client.ui.components.LabelComboBoxRow;
import itti.com.pl.arena.cm.client.ui.components.LabelTextBoxRow;
import itti.com.pl.arena.cm.client.ui.components.TextBoxButtonRow;
import itti.com.pl.arena.cm.dto.GeoObject;
import itti.com.pl.arena.cm.dto.Location;
import itti.com.pl.arena.cm.dto.staticobj.Building;
import itti.com.pl.arena.cm.dto.staticobj.Infrastructure;
import itti.com.pl.arena.cm.dto.staticobj.ParkingLot;
import itti.com.pl.arena.cm.utils.helper.JsonHelperException;
import itti.com.pl.arena.cm.utils.helper.LocationHelper;
import itti.com.pl.arena.cm.utils.helper.LocationHelperException;
import itti.com.pl.arena.cm.utils.helper.NumbersHelper;
import itti.com.pl.arena.cm.utils.helper.StringHelper;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class ParkingLotPanel extends ContextModulePanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private JTabbedPane tabbedPane = null;
    private Component panelParkingLot = null;
    private Component panelBuildings = null;

    private ComboBoxButtonRow removeParkingLotComboBox = null;
    private TextBoxButtonRow addParkingLotComboBox = null;

    private LabelTextBoxRow descriptionTextBox = null;
    private LabelTextBoxRow countryTextBox = null;
    private LabelTextBoxRow townTextBox = null;
    private LabelTextBoxRow streetTextBox = null;

    private ComboBoxButtonRow removeParkingLotCoordinateComboBox = null;
    private LabelTextBoxRow parkingLotXCoordinateTextBox = null;
    private LabelTextBoxRow parkingLotYCoordinateTextBox = null;

    private ComboBoxButtonRow removeBuildingComboBox = null;
    private TextBoxButtonRow addBuildingComboBox = null;

    private LabelComboBoxRow typeComboBox = null;
    private LabelComboBoxRow descriptionComboBox = null;

    /**
     * Create the dialog.
     */
    public ParkingLotPanel() {

        super();

        Component imageComponent = createImagePanel(Messages.getString("ParkingLotPanel.10")); //$NON-NLS-1$
        add(imageComponent, BorderLayout.WEST);

        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        add(tabbedPane, BorderLayout.EAST);

        panelParkingLot = createPanelParkingLot();
        panelBuildings = createPanelBuildings();
        tabbedPane.addTab(Messages.getString("ParkingLotPanel.0"), panelParkingLot); //$NON-NLS-1$
        tabbedPane.addTab(Messages.getString("ParkingLotPanel.1"), panelBuildings); //$NON-NLS-1$
    }

    private Component createPanelParkingLot() {

        JPanel panelParkingLot = createJPanel();

        removeParkingLotComboBox = createComboBoxButtonRow("Remove", null);
        panelParkingLot.add(removeParkingLotComboBox); //$NON-NLS-1$
        removeParkingLotComboBox.setOnClickListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                removeParkingLot();
            }
        });
        removeParkingLotComboBox.setOnChangeListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                parkingLotSelectionChanged();
            }
        });

        addParkingLotComboBox = createTextBoxButtonRow(null, "Add parking lot");
        addParkingLotComboBox.setOnButtonClickListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String parkingLotName = addParkingLotComboBox.getText();
                addParkingLot(parkingLotName);
            }
        });
        panelParkingLot.add(addParkingLotComboBox); //$NON-NLS-1$

        panelParkingLot.add(createLabelRow(Messages.getString("ParkingLotPanel.2"))); //$NON-NLS-1$
        descriptionTextBox = createLabelTextBoxRow(Messages.getString("ParkingLotPanel.3"), null);
        panelParkingLot.add(descriptionTextBox); //$NON-NLS-1$
        countryTextBox = createLabelTextBoxRow(Messages.getString("ParkingLotPanel.4"), null);
        panelParkingLot.add(countryTextBox); //$NON-NLS-1$
        townTextBox = createLabelTextBoxRow(Messages.getString("ParkingLotPanel.5"), null);
        panelParkingLot.add(townTextBox); //$NON-NLS-1$
        streetTextBox = createLabelTextBoxRow(Messages.getString("ParkingLotPanel.6"), null);
        panelParkingLot.add(streetTextBox); //$NON-NLS-1$

        descriptionTextBox.setEnabled(false);
        countryTextBox.setEnabled(false);
        townTextBox.setEnabled(false);
        streetTextBox.setEnabled(false);

        panelParkingLot.add(createLabelRow(Messages.getString("ParkingLotPanel.7"))); //$NON-NLS-1$
        removeParkingLotCoordinateComboBox = createComboBoxButtonRow(Messages.getString("ParkingLotPanel.9"), null);
        removeParkingLotCoordinateComboBox.setOnChangeListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                selectedParkingLotCoordinateChanged();
            }
        });
        removeParkingLotCoordinateComboBox.setOnClickListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                removeParkingLotCoordinate();
            }
        });
        panelParkingLot.add(removeParkingLotCoordinateComboBox); //$NON-NLS-1$
        parkingLotXCoordinateTextBox = createLabelTextBoxRow("X", null);
        panelParkingLot.add(parkingLotXCoordinateTextBox); //$NON-NLS-1$
        parkingLotYCoordinateTextBox = createLabelTextBoxRow("Y", null);
        panelParkingLot.add(parkingLotYCoordinateTextBox); //$NON-NLS-1$
        ButtonRow addParkingLotCoordinateButton = createButtonRow("Add");
        addParkingLotCoordinateButton.setButtonActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                addParkingLotCoordinate();
            }
        });
        panelParkingLot.add(addParkingLotCoordinateButton); //$NON-NLS-1$

        ButtonRow clearParkingLotParamsRow = createButtonRow("Clear");
        clearParkingLotParamsRow.setButtonActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                parkingLotXCoordinateTextBox.setText(null);
                parkingLotYCoordinateTextBox.setText(null);
            }
        });
        panelParkingLot.add(clearParkingLotParamsRow);

        return panelParkingLot;
    }

    private void selectedParkingLotCoordinateChanged() {
        if (StringHelper.hasContent(removeParkingLotCoordinateComboBox.getSelectedItem())) {
            try {
                Location location = LocationHelper.getLocationFromString(removeParkingLotCoordinateComboBox.getSelectedItem());
                parkingLotXCoordinateTextBox.setText(StringHelper.toString(location.getLongitude()));
                parkingLotYCoordinateTextBox.setText(StringHelper.toString(location.getLatitude()));
            } catch (LocationHelperException e) {
                showMessage("Could not parse selected location");
            }
        }
    }

    private void removeParkingLotCoordinate() {
        removeParkingLotCoordinateComboBox.removeSelectedItem();
    }

    private void parkingLotSelectionChanged() {
        String selectedParkingLot = removeParkingLotComboBox.getSelectedItem();
        if (StringHelper.hasContent(selectedParkingLot)) {
            ParkingLot parkingLot = getContextModuleAdapter().getParkingLotDefinition(selectedParkingLot);
            descriptionTextBox.setText(parkingLot != null ? null : null);
            countryTextBox.setText(parkingLot != null ? parkingLot.getCountry() : null);
            townTextBox.setText(parkingLot != null ? parkingLot.getTown() : null);
            streetTextBox.setText(parkingLot != null ? parkingLot.getStreet() : null);

            removeParkingLotCoordinateComboBox.setComboBoxContent(LocationHelper
                    .getLocationsFromStrings(parkingLot != null ? parkingLot.getBoundaries() : null));

            if (parkingLot != null) {
                List<String> parkingObjects = new ArrayList<>();
                if(parkingLot.getBuildings() != null){
                    parkingObjects.addAll(parkingLot.getBuildings().keySet());
                }
                if(parkingLot.getInfrastructure() != null){
                    parkingObjects.addAll(parkingLot.getInfrastructure().keySet());
                }
                removeBuildingComboBox.setComboBoxContent(parkingObjects);
            } else {
                clearBuildingsForm();
            }
        } else {
            clearParkingLotForm();
            clearBuildingsForm();
        }
    }

    private void clearParkingLotForm() {
        addParkingLotComboBox.setText(null);
        descriptionTextBox.setText(null);
        countryTextBox.setText(null);
        townTextBox.setText(null);
        streetTextBox.setText(null);
        removeParkingLotCoordinateComboBox.setComboBoxContent(null);
        parkingLotXCoordinateTextBox.setText(null);
        parkingLotYCoordinateTextBox.setText(null);
    }

    private void clearBuildingsForm() {
        addBuildingComboBox.setText(null);
    }

    private void addParkingLotCoordinate() {
        Double locationX = NumbersHelper.getDoubleFromString(parkingLotXCoordinateTextBox.getText());
        Double locationY = NumbersHelper.getDoubleFromString(parkingLotYCoordinateTextBox.getText());
        if (locationX == null || locationY == null) {
            showMessage("Invalid coordinates specified");
        } else {
            removeParkingLotCoordinateComboBox.addItem(LocationHelper
                    .createStringFromLocation(new Location(locationX, locationY)));
            parkingLotXCoordinateTextBox.setText(null);
            parkingLotYCoordinateTextBox.setText(null);
        }
    }

    private void removeParkingLot() {
        String parkingLot = removeParkingLotComboBox.getSelectedItem();
        if (StringHelper.hasContent(parkingLot)) {
            if (getContextModuleAdapter().removeParkingLot(parkingLot)) {
                showMessage("Parking lot successfully removed from ontology");
                // update list of platform
                onRefreshClick();
            } else {
                showMessage("Could not remove parking lot from ontology");
            }
        }
    }

    private void addParkingLot(String parkingLotName) {

        if (StringHelper.hasContent(parkingLotName)) {

            String parkingLotDesc = descriptionTextBox.getText();
            String parkingLotCountry = countryTextBox.getText();
            String parkingLotTown = townTextBox.getText();
            String parkingLotStreet = streetTextBox.getText();

            List<String> locationStrings = removeParkingLotCoordinateComboBox.getItems();
            try {
                List<Location> locations = LocationHelper.getLocationsFromStrings(locationStrings);
                boolean status = getContextModuleAdapter().updateParkingLot(parkingLotName, parkingLotDesc, parkingLotCountry,
                        parkingLotTown, parkingLotStreet, locations, null, null);
                if (status) {
                    showMessage("Successfully added parking lot to the ontology");
                    onRefreshClick();
                    removeParkingLotComboBox.setItem(parkingLotName);
                    addParkingLotComboBox.setText(null);
                } else {
                    showMessage("Failed to add platform to the ontology");
                }
            } catch (JsonHelperException e) {
                showMessage("Could not update platform: Platform serialization error");
            } catch (LocationHelperException e) {
                showMessage("Could not update platform: invalid format of parking lot coordinates");
            }
        } else {
            showMessage("Parking Lot name was not specified");
        }
    }

    private Component createPanelBuildings() {

        JPanel panelBuildings = createJPanel();

        panelBuildings.add(createLabelRow(Messages.getString("ParkingLotPanel.18"))); //$NON-NLS-1$

        removeBuildingComboBox = createComboBoxButtonRow(Messages.getString("ParkingLotPanel.19"), null);
        removeBuildingComboBox.setOnChangeListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedBuildingSelectionChanged();
            }
        });
        removeBuildingComboBox.setOnClickListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                removeBuilding();
            }
        });
        panelBuildings.add(removeBuildingComboBox); //$NON-NLS-1$
        addBuildingComboBox = createTextBoxButtonRow(null, Messages.getString("ParkingLotPanel.20"));
        panelBuildings.add(addBuildingComboBox); //$NON-NLS-1$

        panelBuildings.add(createEmptyRow());

        typeComboBox = createLabelComboBoxRow(Messages.getString("ParkingLotPanel.13"), null);
        List<String> types = new ArrayList<>();
        types.addAll(Building.Type.asList());
        types.addAll(Infrastructure.Type.asList());
        typeComboBox.setComboBoxContent(types);
        panelBuildings.add(typeComboBox); //$NON-NLS-1$

        descriptionComboBox = createLabelComboBoxRow(Messages.getString("ParkingLotPanel.14"), null);
        panelBuildings.add(descriptionComboBox); //$NON-NLS-1$

        panelBuildings.add(createEmptyRow());
        panelBuildings.add(createLabelRow(Messages.getString("ParkingLotPanel.15"))); //$NON-NLS-1$
        panelBuildings.add(createComboBoxButtonRow(Messages.getString("ParkingLotPanel.17"), null)); //$NON-NLS-1$
        panelBuildings.add(createLabelTextBoxRow("X", null)); //$NON-NLS-1$
        panelBuildings.add(createLabelTextBoxRow("Y", null)); //$NON-NLS-1$
        panelBuildings.add(createButtonRow("Add")); //$NON-NLS-1$

        return panelBuildings;
    }

    protected void removeBuilding() {
        String buildingName = removeBuildingComboBox.getSelectedItem();
        if(StringHelper.hasContent(buildingName)){
            getContextModuleAdapter().removeBuilding(buildingName);
            removeParkingLotComboBox.setItem(removeBuildingComboBox.getSelectedItem());
        }
    }

    protected void selectedBuildingSelectionChanged() {
        String buildingName = removeBuildingComboBox.getSelectedItem();
        if (StringHelper.hasContent(buildingName)) {
            GeoObject object = getContextModuleAdapter().getBuildingDefinition(buildingName);
            if(object != null){
                Building building = null;
                Infrastructure infrastructure = null;
                if(object instanceof Building){
                    building = (Building) object;
                }else{
                    infrastructure = (Infrastructure) object;
                    JOptionPane.showMessageDialog(null, infrastructure);
                }
                typeComboBox.setSelectedItem(building != null ? building.getType().name() : 
                    (infrastructure != null ? infrastructure.getType().name() : null));
            }
        }
    }

    @Override
    protected void onCancelClick() {
    }

    @Override
    protected void onSaveClick() {
        if (tabbedPane.getSelectedComponent() == panelParkingLot) {
            if (StringHelper.hasContent(addParkingLotComboBox.getText())) {
                addParkingLot(addParkingLotComboBox.getText());
            } else {
                addParkingLot(removeParkingLotComboBox.getSelectedItem());
            }
        } else if (tabbedPane.getSelectedComponent() == panelBuildings) {
            // if(StringHelper.hasContent(addCameraTextBox.getText())){
            // addCamera(addCameraTextBox.getText());
            // }else{
            // addCamera(camerasComboBoxRow.getSelectedItem());
            // }
        }
    }

    @Override
    protected void onRefreshClick() {
        List<String> parkingLotNames = getContextModuleAdapter().getListOfParkingLots();
        removeParkingLotComboBox.setComboBoxContent(parkingLotNames);
    }

}
