package itti.com.pl.arena.cm.client.ui;

import itti.com.pl.arena.cm.client.ui.components.ButtonRow;
import itti.com.pl.arena.cm.client.ui.components.ComboBoxButtonRow;
import itti.com.pl.arena.cm.client.ui.components.TextBoxButtonRow;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

public class SwrlPanel extends ContextModulePanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private ComboBoxButtonRow swrlComboBoxRow = null;
    private TextBoxButtonRow addRuleComboBoxRow = null;
    private ButtonRow applyRuleBox = null;

    /**
     * Create the dialog.
     */
    public SwrlPanel() {
        super();

        Component zonesPane = createSwrlPanel();
        add(zonesPane, BorderLayout.CENTER);
    }

    private Component createSwrlPanel() {

        JPanel swrlPanel = createJPanel();
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();

        swrlPanel.setLayout(gbl);
        swrlPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 0;
        gbc.weightx = 1.0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(3,3,3,3);

        swrlComboBoxRow = createComboBoxButtonRow(Messages.getString("SwrlPanel.0"), null); //$NON-NLS-1$
        gbl.setConstraints(swrlComboBoxRow, gbc);
        swrlPanel.add(swrlComboBoxRow); //$NON-NLS-1$

        addRuleComboBoxRow = createTextBoxButtonRow(null, Messages.getString("SwrlPanel.1")); //$NON-NLS-1$
        gbl.setConstraints(addRuleComboBoxRow, gbc);
        swrlPanel.add(addRuleComboBoxRow); //$NON-NLS-1$

        gbc.weighty = 10.0;
        Component logComponent = new JTextArea(30, 100);
        gbl.setConstraints(logComponent, gbc);
        swrlPanel.add(logComponent);

        gbc.weighty = 1;
        applyRuleBox = createButtonRow(Messages.getString("SwrlPanel.2")); //$NON-NLS-1$
        gbl.setConstraints(applyRuleBox, gbc);
        swrlPanel.add(applyRuleBox); //$NON-NLS-1$


        

        return swrlPanel;
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
    }
}