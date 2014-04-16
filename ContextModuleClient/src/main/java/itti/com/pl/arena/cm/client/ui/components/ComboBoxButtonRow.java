package itti.com.pl.arena.cm.client.ui.components;

import itti.com.pl.arena.cm.utils.helper.StringHelper;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

public class ComboBoxButtonRow extends JPanel{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private JComboBox<String> comboBox = null;
    private JButton button = null;

    public ComboBoxButtonRow(String label, List<String> content)
    {
        super(new GridLayout(1, 2));
        button = new JButton(label);
        comboBox = new JComboBox<String>();
        if(content != null){
            for (String item : content) {
                comboBox.addItem(item);
            }
        }
        add(comboBox);
        add(button);
    }

    public void setComboBoxContent(List<String> content){
        comboBox.removeAllItems();
        if(content != null){
            for (String item : content) {
                comboBox.addItem(item);
            }
        }
    }

    public void setOnChangeListener(ActionListener actionListener) {
        if(actionListener != null){
            comboBox.addActionListener(actionListener);
        }
    }

    public String getSelectedItem() {
        return StringHelper.toString(comboBox.getSelectedItem());
    }

	public void setOnClickListener(ActionListener actionListener) {
		if(actionListener != null){
			button.addActionListener(actionListener);
		}
	}
}
