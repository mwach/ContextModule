package itti.com.pl.arena.cm.client.ui.components;

import itti.com.pl.arena.cm.utils.helper.StringHelper;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JPanel;

public class ComboBoxRow extends JPanel{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private JComboBox<String> comboBox = null;

    public ComboBoxRow(List<String> content)
    {
        super(new GridLayout(1, 1));
        comboBox = new JComboBox<String>();
        setComboBoxContent(content);
        add(comboBox);
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
        return comboBox.getSelectedItem() != null ? StringHelper.toString(comboBox.getSelectedItem()) : null;
    }

}
