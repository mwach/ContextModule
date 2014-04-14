package itti.com.pl.arena.cm.client.ui.components;

import java.awt.GridLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

public class ComboBoxButtonRow extends JPanel{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public ComboBoxButtonRow(String label, List<String> content)
    {
        super(new GridLayout(1, 2));
        JButton button = new JButton(label);
        JComboBox<String> textComboBox = new JComboBox<String>();
        if(content != null){
            for (String item : content) {
                textComboBox.addItem(item);
            }
        }
        add(button);
        add(textComboBox);
    }
}
