package itti.com.pl.arena.cm.client.ui.components;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class TextBoxRow extends JPanel{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private JTextField textField;

    public TextBoxRow(String text)
    {
        super(new GridLayout(1, 2));
        JLabel label = new JLabel(text);
        add(label);
        textField = new JTextField();
        add(textField);
    }

    public String getText() {
        return textField.getText();
    }
    public void setText(String text) {
        textField.setText(text);
    }
}
