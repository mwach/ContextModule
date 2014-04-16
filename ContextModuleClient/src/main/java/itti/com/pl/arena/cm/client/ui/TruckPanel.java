package itti.com.pl.arena.cm.client.ui;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class TruckPanel extends ContextModulePanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private Component imageComponent = null;

    /**
     * Create the dialog.
     */
    public TruckPanel() {
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
				imageComponent = createImagePanel(String.format(Messages.getString("TruckPanel.10"), tabbedPane.getSelectedIndex() + 1)); //$NON-NLS-1$
		        add(imageComponent, BorderLayout.WEST);
			}
		});
    }

	private Component createPlatformPanel() {
        JPanel panelPlatform = createJPanel();
        panelPlatform.add(createLabelRow(Messages.getString("TruckPanel.2"))); //$NON-NLS-1$

        panelPlatform.add(createTextBoxRow(Messages.getString("TruckPanel.3"), null)); //$NON-NLS-1$
        panelPlatform.add(createTextBoxRow(Messages.getString("TruckPanel.4"), null)); //$NON-NLS-1$
        panelPlatform.add(createTextBoxRow(Messages.getString("TruckPanel.5"), null)); //$NON-NLS-1$

        panelPlatform.add(createEmptyRow());

        panelPlatform.add(createLabelRow(Messages.getString("TruckPanel.6"))); //$NON-NLS-1$

        panelPlatform.add(createTextBoxRow(Messages.getString("TruckPanel.7"), null)); //$NON-NLS-1$
        panelPlatform.add(createTextBoxRow(Messages.getString("TruckPanel.8"), null)); //$NON-NLS-1$

        return panelPlatform;
    }

    private Component createCameraPanel() {
        JPanel panelCamera = createJPanel();

        panelCamera.add(createLabelRow(Messages.getString("TruckPanel.19"))); //$NON-NLS-1$
        panelCamera.add(createComboBoxButtonRow(Messages.getString("TruckPanel.20"), null)); //$NON-NLS-1$
        panelCamera.add(createTextBoxButtonRow(null, Messages.getString("TruckPanel.21"))); //$NON-NLS-1$

        panelCamera.add(createEmptyRow());
        panelCamera.add(createLabelRow(Messages.getString("TruckPanel.12"))); //$NON-NLS-1$
        panelCamera.add(createTextBoxRow(Messages.getString("TruckPanel.13"), null)); //$NON-NLS-1$
        panelCamera.add(createTextBoxRow(Messages.getString("TruckPanel.14"), null)); //$NON-NLS-1$

        panelCamera.add(createEmptyRow());
        panelCamera.add(createLabelRow(Messages.getString("TruckPanel.15"))); //$NON-NLS-1$
        panelCamera.add(createTextBoxRow(Messages.getString("TruckPanel.16"), null)); //$NON-NLS-1$
        panelCamera.add(createTextBoxRow(Messages.getString("TruckPanel.17"), null)); //$NON-NLS-1$
        panelCamera.add(createTextBoxRow(Messages.getString("TruckPanel.18"), null)); //$NON-NLS-1$
        
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
