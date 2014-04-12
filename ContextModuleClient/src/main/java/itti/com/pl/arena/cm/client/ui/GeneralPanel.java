package itti.com.pl.arena.cm.client.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.TextArea;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

public class GeneralPanel extends ContextModulePanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Create the dialog.
     */
    public GeneralPanel() {

        super();
        setLayout(new BorderLayout());
        add(createPanelGeneral(), BorderLayout.CENTER);
    }

    private Component createPanelGeneral() {

        JPanel panelGeneral = new JPanel();
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();

        panelGeneral.setLayout(gbl);
        panelGeneral.setBorder(new EmptyBorder(10, 10, 10, 10));

        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(3,3,3,3);

        Component connectComponent = createTextBoxButtonRow("Connect to the CM");
        gbl.setConstraints(connectComponent, gbc);
        panelGeneral.add(connectComponent);

        Component refreshComponent = createButtonRow("Refresh content");
        gbl.setConstraints(refreshComponent, gbc);
        panelGeneral.add(refreshComponent);

        Component emptyComponent = createEmptyRow();
        gbl.setConstraints(emptyComponent, gbc);
        panelGeneral.add(emptyComponent);

        Component logDescComponent = new JLabel("Connection log");
        gbl.setConstraints(logDescComponent, gbc);
        panelGeneral.add(logDescComponent);

        gbc.weighty = 10.0;
        Component logComponent = new JTextArea("", 30, 100);
        gbl.setConstraints(logComponent, gbc);
        panelGeneral.add(logComponent);

        return panelGeneral;
    }
}
