package itti.com.pl.arena.cm.client.ui;

import javax.swing.JApplet;
import javax.swing.JTabbedPane;

import java.awt.BorderLayout;

/**
 * Window application serving as a ContextModule basic client
 * 
 * @author cm-admin
 * 
 */
public class CMClientApplet extends JApplet {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Launch the application.
     */
    @Override
    public void init() {
        super.init();
        setSize(600, 500);
        initialize();
        setVisible(true);
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        setBounds(200, 200, 600, 500);

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        getContentPane().add(tabbedPane, BorderLayout.CENTER);

        tabbedPane.addTab(Messages.getString("MainWindowApp.1"), new GeneralPanel()); //$NON-NLS-1$
        tabbedPane.addTab(Messages.getString("MainWindowApp.2"), new PlatformPanel()); //$NON-NLS-1$
        tabbedPane.addTab(Messages.getString("MainWindowApp.3"), new ParkingLotPanel()); //$NON-NLS-1$
        tabbedPane.addTab(Messages.getString("MainWindowApp.4"), new ZonesPanel()); //$NON-NLS-1$
    }
}
