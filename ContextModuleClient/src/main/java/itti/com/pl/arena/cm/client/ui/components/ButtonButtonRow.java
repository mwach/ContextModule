package itti.com.pl.arena.cm.client.ui.components;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

public class ButtonButtonRow extends JPanel{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public ButtonButtonRow(String buttonOneText, String buttonTwoText)
    {
        super(new GridLayout(1, 2));
        JButton buttonOne = new JButton(buttonOneText);
        add(buttonOne);
        JButton buttonTwo = new JButton(buttonTwoText);
        add(buttonTwo);
    }
}
