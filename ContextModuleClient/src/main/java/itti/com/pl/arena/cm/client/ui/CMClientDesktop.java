package itti.com.pl.arena.cm.client.ui;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import java.awt.BorderLayout;

/**
 * Window application serving as a ContextModule basic client
 * 
 * @author cm-admin
 * 
 */
public class CMClientDesktop extends JFrame {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        CMClientDesktop window = new CMClientDesktop();
        window.setVisible(true);
    }

    /**
     * Create the application.
     */
    public CMClientDesktop() {
        super();
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        setBounds(200, 200, 600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle(Messages.getString("MainWindowApp.0")); //$NON-NLS-1$

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        getContentPane().add(tabbedPane, BorderLayout.CENTER);

        tabbedPane.addTab(Messages.getString("MainWindowApp.1"), null); //$NON-NLS-1$
        tabbedPane.addTab(Messages.getString("MainWindowApp.2"), new TruckPanel()); //$NON-NLS-1$
        tabbedPane.addTab(Messages.getString("MainWindowApp.3"), new ParkingLotPanel()); //$NON-NLS-1$
        tabbedPane.addTab(Messages.getString("MainWindowApp.4"), new ZonesPanel()); //$NON-NLS-1$
    }
}
