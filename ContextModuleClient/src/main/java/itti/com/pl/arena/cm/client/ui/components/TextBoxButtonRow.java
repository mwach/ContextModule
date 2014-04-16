package itti.com.pl.arena.cm.client.ui.components;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class TextBoxButtonRow extends JPanel{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private JTextField textField = null;
    private JButton button = null;
    
    public TextBoxButtonRow(String textFieldText, String buttonText)
    {
        super(new GridLayout(1, 2));
        textField = new JTextField();
        add(textField);
        button = new JButton(buttonText);
        add(button);
        
        setText(textFieldText);
        setButtonText(buttonText);
    }

    public void setText(String text) {
    	textField.setText(text == null ? "" : text);
    }
    public void setButtonText(String buttonText) {
    	textField.setText(buttonText == null ? "" : buttonText);
    }
}
