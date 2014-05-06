package itti.com.pl.arena.cm.client.ui;

import itti.com.pl.arena.cm.client.ui.components.ButtonRow;
import itti.com.pl.arena.cm.client.ui.components.ComboBoxButtonRow;
import itti.com.pl.arena.cm.client.ui.components.LabelTextBoxRow;
import itti.com.pl.arena.cm.client.ui.components.TextBoxButtonRow;
import itti.com.pl.arena.cm.dto.Location;
import itti.com.pl.arena.cm.dto.coordinates.CartesianCoordinate;
import itti.com.pl.arena.cm.dto.dynamicobj.Camera;
import itti.com.pl.arena.cm.dto.dynamicobj.CameraType;
import itti.com.pl.arena.cm.dto.dynamicobj.Platform;
import itti.com.pl.arena.cm.utils.helper.JsonHelperException;
import itti.com.pl.arena.cm.utils.helper.NumbersHelper;
import itti.com.pl.arena.cm.utils.helper.StringHelper;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class PlatformPanel extends ContextModulePanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private JTabbedPane tabbedPane = null;
    private Component platformPanel = null;
    private Component cameraPanel = null;

    private Component imageComponent = null;

    private ComboBoxButtonRow platformsComboBoxRow = null;
    private TextBoxButtonRow addPlatformTextBoxButtonRow = null;

    private LabelTextBoxRow platformWidthRow = null;
    private LabelTextBoxRow platformHeightRow = null;
    private LabelTextBoxRow platformLengthRow = null;

    private LabelTextBoxRow platformLocationX = null;
    private LabelTextBoxRow platformLocationY = null;
    private ButtonRow clearPlatformParamsRow = null;

    private ComboBoxButtonRow camerasComboBoxRow = null;
    private TextBoxButtonRow addCameraTextBox = null;

    private LabelTextBoxRow cameraVerticalAngleRow = null;
    private LabelTextBoxRow cameraHorizontalAngleRow = null;

    private LabelTextBoxRow cameraXRow = null;
    private LabelTextBoxRow cameraYRow = null;
    private LabelTextBoxRow cameraAngleRow = null;
    private ButtonRow clearCameraParamsRow = null;

    /**
     * Create the dialog.
     */
    public PlatformPanel() {
        super();

        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        add(tabbedPane, BorderLayout.EAST);

        imageComponent = createImagePanel(Messages.getString("TruckPanel.9")); //$NON-NLS-1$
        add(imageComponent, BorderLayout.WEST);

        platformPanel = createPlatformPanel();
        cameraPanel = createCameraPanel();
        tabbedPane.addTab(Messages.getString("TruckPanel.0"), platformPanel); //$NON-NLS-1$
        tabbedPane.addTab(Messages.getString("TruckPanel.1"), cameraPanel); //$NON-NLS-1$
        tabbedPane.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent arg0) {
                remove(imageComponent);
                imageComponent = createImagePanel(String.format(
                        Messages.getString("TruckPanel.10"), tabbedPane.getSelectedIndex() + 1)); //$NON-NLS-1$
                add(imageComponent, BorderLayout.WEST);
            }
        });
    }

    private Component createPlatformPanel() {
        JPanel panelPlatform = createJPanel();

        platformsComboBoxRow = createComboBoxButtonRow(Messages.getString("PlatformPanel.0"), null); //$NON-NLS-1$
        platformsComboBoxRow.setOnChangeListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                platformSelectionChanged();
            }
        });
        platformsComboBoxRow.setOnClickListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                removePlatform();
            }
        });
        panelPlatform.add(platformsComboBoxRow);

        addPlatformTextBoxButtonRow = createTextBoxButtonRow(null, Messages.getString("PlatformPanel.1")); //$NON-NLS-1$
        addPlatformTextBoxButtonRow.setOnButtonClickListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                addPlatform(addPlatformTextBoxButtonRow.getText());
            }
        });
        panelPlatform.add(addPlatformTextBoxButtonRow);

        panelPlatform.add(createLabelRow(Messages.getString("TruckPanel.2"))); //$NON-NLS-1$

        platformWidthRow = createLabelTextBoxRow(Messages.getString(Messages.getString("PlatformPanel.2")), null); //$NON-NLS-1$
        platformHeightRow = createLabelTextBoxRow(Messages.getString(Messages.getString("PlatformPanel.3")), null); //$NON-NLS-1$
        platformLengthRow = createLabelTextBoxRow(Messages.getString(Messages.getString("PlatformPanel.4")), null); //$NON-NLS-1$

        panelPlatform.add(platformWidthRow); //$NON-NLS-1$
        panelPlatform.add(platformHeightRow); //$NON-NLS-1$
        panelPlatform.add(platformLengthRow); //$NON-NLS-1$

        panelPlatform.add(createEmptyRow());

        panelPlatform.add(createLabelRow(Messages.getString("TruckPanel.6"))); //$NON-NLS-1$

        platformLocationX = createLabelTextBoxRow(Messages.getString(Messages.getString("PlatformPanel.5")), null); //$NON-NLS-1$
        platformLocationY = createLabelTextBoxRow(Messages.getString(Messages.getString("PlatformPanel.6")), null); //$NON-NLS-1$
        panelPlatform.add(platformLocationX); //$NON-NLS-1$
        panelPlatform.add(platformLocationY); //$NON-NLS-1$

        clearPlatformParamsRow = createButtonRow(Messages.getString("PlatformPanel.7")); //$NON-NLS-1$
        clearPlatformParamsRow.setOnClickListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                clearPlatformForm();
            }
        });
        panelPlatform.add(clearPlatformParamsRow);

        return panelPlatform;
    }

    private void clearPlatformForm() {
        platformWidthRow.setText(null);
        platformHeightRow.setText(null);
        platformLengthRow.setText(null);
        platformLocationX.setText(null);
        platformLocationY.setText(null);
    }

    private void clearCameraForm() {
        cameraVerticalAngleRow.setText(null);
        cameraHorizontalAngleRow.setText(null);
        cameraXRow.setText(null);
        cameraYRow.setText(null);
        cameraAngleRow.setText(null);
    }

    private void addPlatform(String platformName) {

        if (StringHelper.hasContent(platformName)) {

            double width = NumbersHelper.getDoubleFromString(platformWidthRow.getText(), 0);
            double height = NumbersHelper.getDoubleFromString(platformHeightRow.getText(), 0);
            double length = NumbersHelper.getDoubleFromString(platformLengthRow.getText(), 0);

            double locationX = NumbersHelper.getDoubleFromString(platformLocationX.getText(), 0);
            double locationY = NumbersHelper.getDoubleFromString(platformLocationY.getText(), 0);

            try {
                boolean status = getContextModuleAdapter().updatePlatform(platformName, new Location(locationX, locationY),
                        width, height, length, null);
                if (status) {
                    showMessage(Messages.getString("PlatformPanel.8")); //$NON-NLS-1$
                    onRefreshClick();
                    platformsComboBoxRow.setSelectedItem(platformName);
                    addPlatformTextBoxButtonRow.setText(null);
                } else {
                    showMessage(Messages.getString("PlatformPanel.9")); //$NON-NLS-1$
                }
            } catch (JsonHelperException e) {
                showMessage(Messages.getString("PlatformPanel.10")); //$NON-NLS-1$
            }
        } else {
            showMessage(Messages.getString("PlatformPanel.11")); //$NON-NLS-1$
        }
    }

    private void removePlatform() {
        String selectedPlatform = platformsComboBoxRow.getSelectedItem();
        if (StringHelper.hasContent(selectedPlatform)) {
            if (getContextModuleAdapter().removePlatform(selectedPlatform)) {
                showMessage(Messages.getString("PlatformPanel.12")); //$NON-NLS-1$
                // update list of platform
                onRefreshClick();
            } else {
                showMessage(Messages.getString("PlatformPanel.13")); //$NON-NLS-1$
            }
        }
    }

    private void platformSelectionChanged() {
        String selectedPlatform = platformsComboBoxRow.getSelectedItem();
        if (StringHelper.hasContent(selectedPlatform)) {
            Platform platform = getContextModuleAdapter().getPlatformDefinition(selectedPlatform);
            platformWidthRow.setText(platform != null ? StringHelper.toString(platform.getWidth()) : null);
            platformHeightRow.setText(platform != null ? StringHelper.toString(platform.getHeight()) : null);
            platformLengthRow.setText(platform != null ? StringHelper.toString(platform.getLength()) : null);

            platformLocationX.setText((platform != null && platform.getLocation() != null) ? StringHelper.toString(platform
                    .getLocation().getLongitude()) : null);
            platformLocationY.setText((platform != null && platform.getLocation() != null) ? StringHelper.toString(platform
                    .getLocation().getLatitude()) : null);

            if (platform != null && platform.getCameras() != null) {
                camerasComboBoxRow.setItems(new ArrayList<>(platform.getCameras().keySet()));
            } else {
                camerasComboBoxRow.setItems(null);
            }
        } else {
            clearPlatformForm();
            clearCameraForm();
        }
    }

    private Component createCameraPanel() {
        JPanel panelCamera = createJPanel();

        panelCamera.add(createLabelRow(Messages.getString("TruckPanel.19"))); //$NON-NLS-1$

        camerasComboBoxRow = createComboBoxButtonRow(Messages.getString(Messages.getString("PlatformPanel.14")), null); //$NON-NLS-1$
        camerasComboBoxRow.setOnChangeListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                cameraSelectionChanged();
            }
        });
        camerasComboBoxRow.setOnClickListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                removeCamera();
            }
        });
        panelCamera.add(camerasComboBoxRow); //$NON-NLS-1$

        addCameraTextBox = createTextBoxButtonRow(null, Messages.getString(Messages.getString("PlatformPanel.15"))); //$NON-NLS-1$
        addCameraTextBox.setOnButtonClickListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                addCamera(addCameraTextBox.getText());
            }
        });
        panelCamera.add(addCameraTextBox); //$NON-NLS-1$

        panelCamera.add(createLabelRow(Messages.getString("TruckPanel.12"))); //$NON-NLS-1$

        cameraVerticalAngleRow = createLabelTextBoxRow(Messages.getString(Messages.getString("PlatformPanel.16")), null); //$NON-NLS-1$
        cameraHorizontalAngleRow = createLabelTextBoxRow(Messages.getString(Messages.getString("PlatformPanel.17")), null); //$NON-NLS-1$
        panelCamera.add(cameraVerticalAngleRow); //$NON-NLS-1$
        panelCamera.add(cameraHorizontalAngleRow); //$NON-NLS-1$

        panelCamera.add(createEmptyRow());
        panelCamera.add(createLabelRow(Messages.getString("TruckPanel.15"))); //$NON-NLS-1$

        cameraXRow = createLabelTextBoxRow(Messages.getString(Messages.getString("PlatformPanel.18")), null); //$NON-NLS-1$
        cameraYRow = createLabelTextBoxRow(Messages.getString(Messages.getString("PlatformPanel.19")), null); //$NON-NLS-1$
        cameraAngleRow = createLabelTextBoxRow(Messages.getString(Messages.getString("PlatformPanel.20")), null); //$NON-NLS-1$
        panelCamera.add(cameraXRow); //$NON-NLS-1$
        panelCamera.add(cameraYRow); //$NON-NLS-1$
        panelCamera.add(cameraAngleRow); //$NON-NLS-1$

        clearCameraParamsRow = createButtonRow(Messages.getString("PlatformPanel.21")); //$NON-NLS-1$
        clearCameraParamsRow.setOnClickListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                clearCameraForm();
            }
        });
        panelCamera.add(clearCameraParamsRow);

        return panelCamera;
    }

    protected void addCamera(String cameraName) {
        if (StringHelper.hasContent(cameraName)) {
            String platformName = platformsComboBoxRow.getSelectedItem();
            if (StringHelper.hasContent(platformName)) {
                addCamera(cameraName, platformName);
            } else {
                showMessage(Messages.getString("PlatformPanel.22")); //$NON-NLS-1$
            }
        }
    }

    private void removeCamera() {
        String selectedCamera = camerasComboBoxRow.getSelectedItem();
        String selectedPlatform = platformsComboBoxRow.getSelectedItem();
        if (StringHelper.hasContent(selectedCamera)) {
            if (getContextModuleAdapter().removeCamera(selectedCamera)) {
                showMessage(Messages.getString("PlatformPanel.23")); //$NON-NLS-1$
                // update list of platform
                onRefreshClick();
                platformsComboBoxRow.setSelectedItem(selectedPlatform);
            } else {
                showMessage(Messages.getString("PlatformPanel.24")); //$NON-NLS-1$
            }
        }
    }

    private void addCamera(String cameraName, String platformName) {

        double cameraX = NumbersHelper.getDoubleFromString(cameraXRow.getText(), 0);
        double cameraY = NumbersHelper.getDoubleFromString(cameraYRow.getText(), 0);
        int cameraAngle = NumbersHelper.getIntegerFromString(cameraAngleRow.getText(), 0);

        double horizontalAngle = NumbersHelper.getDoubleFromString(cameraHorizontalAngleRow.getText(), 0);
        double verticalAngle = NumbersHelper.getDoubleFromString(cameraVerticalAngleRow.getText(), 0);

        try {
            boolean status = getContextModuleAdapter().updateCamera(cameraName, platformName, CameraType.Other.name(),
                    horizontalAngle, verticalAngle, new CartesianCoordinate(cameraX, cameraY), cameraAngle);
            if (status) {
                showMessage(Messages.getString("PlatformPanel.25")); //$NON-NLS-1$
                onRefreshClick();
                platformsComboBoxRow.setSelectedItem(platformName);
                addCameraTextBox.setText(null);
            } else {
                showMessage(Messages.getString("PlatformPanel.26")); //$NON-NLS-1$
            }
        } catch (JsonHelperException e) {
            showMessage(Messages.getString("PlatformPanel.27")); //$NON-NLS-1$
        }
    }

    private void cameraSelectionChanged() {
        String selectedCamera = camerasComboBoxRow.getSelectedItem();
        if (StringHelper.hasContent(selectedCamera)) {
            Camera camera = getContextModuleAdapter().getCameraDefinition(selectedCamera);
            cameraVerticalAngleRow.setText(camera != null ? StringHelper.toString(camera.getAngleY()) : null);
            cameraHorizontalAngleRow.setText(camera != null ? StringHelper.toString(camera.getAngleX()) : null);
            cameraXRow.setText(camera != null ? StringHelper.toString(camera.getOnPlatformPosition().getX()) : null);
            cameraYRow.setText(camera != null ? StringHelper.toString(camera.getOnPlatformPosition().getY()) : null);
            cameraAngleRow.setText(camera != null ? StringHelper.toString(camera.getDirectionAngle()) : null);
        } else {
            clearCameraForm();
        }
    }

    @Override
    protected void onCancelClick() {
    }

    @Override
    protected void onSaveClick() {
        if (tabbedPane.getSelectedComponent() == platformPanel) {
            if (StringHelper.hasContent(addPlatformTextBoxButtonRow.getText())) {
                addPlatform(addPlatformTextBoxButtonRow.getText());
            } else {
                addPlatform(platformsComboBoxRow.getSelectedItem());
            }
        } else if (tabbedPane.getSelectedComponent() == cameraPanel) {
            if (StringHelper.hasContent(addCameraTextBox.getText())) {
                addCamera(addCameraTextBox.getText());
            } else {
                addCamera(camerasComboBoxRow.getSelectedItem());
            }
        }
    }

    @Override
    protected void onRefreshClick() {
        List<String> platforms = getContextModuleAdapter().getListOfPlatforms();
        platformsComboBoxRow.setItems(platforms);
    }
}
