package itti.com.pl.arena.cm.client.ui.components;

import itti.com.pl.arena.cm.utils.helper.StringHelper;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class LabelComboBoxRow extends JPanel{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private JLabel textLabel = null;
    private JComboBox<String> comboBox = null;

    public LabelComboBoxRow(String label, List<String> content)
    {
        super(new GridLayout(1, 2));
        textLabel = new JLabel();
        comboBox = new JComboBox<String>();
        setLabelText(label);
        setComboBoxContent(content);
        add(textLabel);
        add(comboBox);
    }
    
    public void setLabelText(String label){
        if(label != null){
            textLabel.setText(label);
        }
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

}
