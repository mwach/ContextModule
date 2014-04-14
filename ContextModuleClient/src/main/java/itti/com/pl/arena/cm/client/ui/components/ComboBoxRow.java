package itti.com.pl.arena.cm.client.ui.components;

import java.awt.GridLayout;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ComboBoxRow extends JPanel{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public ComboBoxRow(String label, List<String> content)
    {
        super(new GridLayout(1, 2));
        JLabel textLabel = new JLabel(label);
        JComboBox<String> textComboBox = new JComboBox<String>();
        if(content != null){
            for (String item : content) {
                textComboBox.addItem(item);
            }
        }
        add(textLabel);
        add(textComboBox);
    }
}
