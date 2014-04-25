package itti.com.pl.arena.cm.client.ui;

import itti.com.pl.arena.cm.client.ui.components.ButtonRow;
import itti.com.pl.arena.cm.client.ui.components.ComboBoxButtonRow;
import itti.com.pl.arena.cm.client.ui.components.LabelTextBoxRow;
import itti.com.pl.arena.cm.client.ui.components.TextBoxButtonRow;
import itti.com.pl.arena.cm.dto.Location;
import itti.com.pl.arena.cm.dto.dynamicobj.Platform;
import itti.com.pl.arena.cm.utils.helper.JsonHelperException;
import itti.com.pl.arena.cm.utils.helper.NumbersHelper;
import itti.com.pl.arena.cm.utils.helper.StringHelper;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    private Component imageComponent = null;

    private ComboBoxButtonRow platformsComboBoxRow = null;
    private TextBoxButtonRow addPlatformTextBoxButtonRow = null;

    private LabelTextBoxRow platformWidthRow = null;
    private LabelTextBoxRow platformHeightRow = null;
    private LabelTextBoxRow platformLengthRow = null;

    private LabelTextBoxRow platformLocationX = null;
    private LabelTextBoxRow platformLocationY = null;
    private ButtonRow clearPlatformParamsRow = null;

    /**
     * Create the dialog.
     */
    public PlatformPanel() {
        super();

        final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        add(tabbedPane, BorderLayout.EAST);

        imageComponent = createImagePanel(Messages.getString("TruckPanel.9")); //$NON-NLS-1$
        add(imageComponent, BorderLayout.WEST);

        tabbedPane.addTab(Messages.getString("TruckPanel.0"), createPlatformPanel()); //$NON-NLS-1$
        tabbedPane.addTab(Messages.getString("TruckPanel.1"), createCameraPanel()); //$NON-NLS-1$
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

        platformsComboBoxRow = createComboBoxButtonRow("Remove", null);
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

        addPlatformTextBoxButtonRow = createTextBoxButtonRow(null, "Add platform");
        addPlatformTextBoxButtonRow.setOnButtonClickListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                addPlatform();
            }
        });
        panelPlatform.add(addPlatformTextBoxButtonRow);

        panelPlatform.add(createLabelRow(Messages.getString("TruckPanel.2"))); //$NON-NLS-1$

        platformWidthRow = createLabelTextBoxRow(Messages.getString("TruckPanel.3"), null);
        platformHeightRow = createLabelTextBoxRow(Messages.getString("TruckPanel.4"), null);
        platformLengthRow = createLabelTextBoxRow(Messages.getString("TruckPanel.5"), null);

        panelPlatform.add(platformWidthRow); //$NON-NLS-1$
        panelPlatform.add(platformHeightRow); //$NON-NLS-1$
        panelPlatform.add(platformLengthRow); //$NON-NLS-1$

        panelPlatform.add(createEmptyRow());

        panelPlatform.add(createLabelRow(Messages.getString("TruckPanel.6"))); //$NON-NLS-1$

        platformLocationX = createLabelTextBoxRow(Messages.getString("TruckPanel.7"), null);
        platformLocationY = createLabelTextBoxRow(Messages.getString("TruckPanel.8"), null);
        panelPlatform.add(platformLocationX); //$NON-NLS-1$
        panelPlatform.add(platformLocationY); //$NON-NLS-1$

        clearPlatformParamsRow = createButtonRow("Clear");
        clearPlatformParamsRow.setButtonActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent arg0) {
                clearForm();
            }
        });

        return panelPlatform;
    }

    private void clearForm() {
        platformWidthRow.setText(null);
        platformHeightRow.setText(null);
        platformLengthRow.setText(null);
        platformLocationX.setText(null);
        platformLocationY.setText(null);
    }

    private void addPlatform() {

        String platformName = addPlatformTextBoxButtonRow.getText();
        if (StringHelper.hasContent(platformName)) {

            double width = NumbersHelper.getDoubleFromString(platformWidthRow.getText(), 0);
            double height = NumbersHelper.getDoubleFromString(platformHeightRow.getText(), 0);
            double length = NumbersHelper.getDoubleFromString(platformLengthRow.getText(), 0);

            double locationX = NumbersHelper.getDoubleFromString(platformLocationX.getText(), 0);
            double locationY = NumbersHelper.getDoubleFromString(platformLocationY.getText(), 0);

            try {
                boolean status = getContextModuleAdapter().updatePlatform(platformName, new Location(locationX, locationY), width, height, length, null);
                if(status){
                    showMessage("Successfully added platform to the ontology");
                    onRefreshClick();
                }else{
                    showMessage("Failed to add platform to the ontology");
                }
            } catch (JsonHelperException e) {
                showMessage("Could not update platform: Platform serialization error");
            }
        }else{
            showMessage("Platform name was not specified");
        }
    }

    private void removePlatform() {
        String selectedPlatform = platformsComboBoxRow.getSelectedItem();
        if (StringHelper.hasContent(selectedPlatform)) {
            if (getContextModuleAdapter().removePlatform(selectedPlatform)) {
                showMessage("Platform successfully removed from ontology");
                // update list of platform
                platformSelectionChanged();
            } else {
                showMessage("Could not remove platform from ontology");
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
            platformLocationX.setText((platform != null && platform.getLocation() != null) ? StringHelper.toString(platform
                    .getLocation().getLatitude()) : null);
        }
    }

    private Component createCameraPanel() {
        JPanel panelCamera = createJPanel();

        panelCamera.add(createLabelRow(Messages.getString("TruckPanel.19"))); //$NON-NLS-1$
        panelCamera.add(createComboBoxButtonRow(Messages.getString("TruckPanel.20"), null)); //$NON-NLS-1$
        panelCamera.add(createTextBoxButtonRow(null, Messages.getString("TruckPanel.21"))); //$NON-NLS-1$

        panelCamera.add(createEmptyRow());
        panelCamera.add(createLabelRow(Messages.getString("TruckPanel.12"))); //$NON-NLS-1$
        panelCamera.add(createLabelTextBoxRow(Messages.getString("TruckPanel.13"), null)); //$NON-NLS-1$
        panelCamera.add(createLabelTextBoxRow(Messages.getString("TruckPanel.14"), null)); //$NON-NLS-1$

        panelCamera.add(createEmptyRow());
        panelCamera.add(createLabelRow(Messages.getString("TruckPanel.15"))); //$NON-NLS-1$
        panelCamera.add(createLabelTextBoxRow(Messages.getString("TruckPanel.16"), null)); //$NON-NLS-1$
        panelCamera.add(createLabelTextBoxRow(Messages.getString("TruckPanel.17"), null)); //$NON-NLS-1$
        panelCamera.add(createLabelTextBoxRow(Messages.getString("TruckPanel.18"), null)); //$NON-NLS-1$

        return panelCamera;
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
        List<String> platforms = getContextModuleAdapter().getListOfPlatforms();
        platformsComboBoxRow.setComboBoxContent(platforms);
    }
}
