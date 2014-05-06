package itti.com.pl.arena.cm.client.ui;

import itti.com.pl.arena.cm.client.ui.components.ButtonRow;
import itti.com.pl.arena.cm.client.ui.components.ComboBoxButtonRow;
import itti.com.pl.arena.cm.client.ui.components.LabelTextBoxRow;
import itti.com.pl.arena.cm.utils.helper.StringHelper;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

public class SwrlPanel extends ContextModulePanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private ComboBoxButtonRow swrlComboBoxRow = null;
    private LabelTextBoxRow ruleNameTextBoxRow = null;
    private ButtonRow addRuleRow = null;
    private ButtonRow applyRuleBox = null;
    private JTextArea ruleContentBox = null;

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
        gbc.insets = new Insets(3, 3, 3, 3);

        swrlComboBoxRow = createComboBoxButtonRow(Messages.getString("SwrlPanel.0"), null); //$NON-NLS-1$
        swrlComboBoxRow.setOnChangeListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ruleSelectionChanged();
            }
        });
        swrlComboBoxRow.setOnClickListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                removeRule();
            }
        });
        gbl.setConstraints(swrlComboBoxRow, gbc);
        swrlPanel.add(swrlComboBoxRow); //$NON-NLS-1$

        ruleNameTextBoxRow = createLabelTextBoxRow("Rule name", null); //$NON-NLS-1$
        gbl.setConstraints(ruleNameTextBoxRow, gbc);
        swrlPanel.add(ruleNameTextBoxRow); //$NON-NLS-1$

        addRuleRow = createButtonRow("Add rule"); //$NON-NLS-1$
        gbl.setConstraints(addRuleRow, gbc);
        addRuleRow.setOnClickListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                addRule();
            }
        });
        swrlPanel.add(addRuleRow); //$NON-NLS-1$

        gbc.weighty = 10.0;
        ruleContentBox = new JTextArea(30, 100);
        gbl.setConstraints(ruleContentBox, gbc);
        swrlPanel.add(ruleContentBox);

        gbc.weighty = 1;
        applyRuleBox = createButtonRow(Messages.getString("SwrlPanel.2")); //$NON-NLS-1$
        applyRuleBox.setOnClickListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                applyRules();
            }
        });
        gbl.setConstraints(applyRuleBox, gbc);
        swrlPanel.add(applyRuleBox); //$NON-NLS-1$

        return swrlPanel;
    }

    protected void applyRules() {
        if (getContextModuleAdapter().applyRules()) {
            showMessage(Messages.getString("SwrlPanel.3")); //$NON-NLS-1$
        } else {
            showMessage(Messages.getString("SwrlPanel.4")); //$NON-NLS-1$
        }
    }

    protected void addRule() {

        String ruleName = ruleNameTextBoxRow.getText();
        updateRule(ruleName);
    }

    private void updateRule(String ruleName) {

        String ruleContent = ruleContentBox.getText();

        if (StringHelper.hasContent(ruleName)) {
            if (StringHelper.hasContent(ruleContent)) {
                if (getContextModuleAdapter().updateRule(ruleName, ruleContent)) {

                    showMessage(Messages.getString("SwrlPanel.5")); //$NON-NLS-1$
                    clearRuleForm();
                } else {
                    showMessage(Messages.getString("SwrlPanel.6")); //$NON-NLS-1$
                }
            } else {
                showMessage(Messages.getString("SwrlPanel.7")); //$NON-NLS-1$
            }
        } else {
            showMessage(Messages.getString("SwrlPanel.8")); //$NON-NLS-1$
        }

    }

    private void clearRuleForm() {
        ruleContentBox.setText(Messages.getString("SwrlPanel.9")); //$NON-NLS-1$
        ruleNameTextBoxRow.setText(null);
    }

    protected void removeRule() {
        String ruleName = swrlComboBoxRow.getSelectedItem();
        if (StringHelper.hasContent(ruleName)) {
            if (getContextModuleAdapter().removeRule(ruleName)) {
                showMessage(Messages.getString("SwrlPanel.10")); //$NON-NLS-1$
                onRefreshClick();
            } else {
                showMessage(Messages.getString("SwrlPanel.11")); //$NON-NLS-1$
            }
        } else {
            showMessage(Messages.getString("SwrlPanel.12")); //$NON-NLS-1$
        }

    }

    protected void ruleSelectionChanged() {
        String ruleName = swrlComboBoxRow.getSelectedItem();
        if (StringHelper.hasContent(ruleName)) {
            ruleContentBox.setText(getContextModuleAdapter().getRule(ruleName));
        }
    }

    @Override
    protected void onCancelClick() {
    }

    @Override
    protected void onSaveClick() {
        if (StringHelper.hasContent(ruleNameTextBoxRow.getText())) {
            updateRule(ruleNameTextBoxRow.getText());
        } else {
            updateRule(swrlComboBoxRow.getSelectedItem());
        }
    }

    @Override
    protected void onRefreshClick() {
        List<String> listOfRules = getContextModuleAdapter().getListOfRules();
        clearRuleForm();
        swrlComboBoxRow.setItems(listOfRules);
    }
}