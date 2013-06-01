package gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 *
 * @author alexisvincent
 */
public class MainFrame extends JFrame {
    
    private static LockScreen lockScreen;
    private static QueryScreen queryScreen;
    private static ResultsScreen resultsScreen;
    
    private GridBagConstraints gc;

    public MainFrame() {

        //new instances
        MainFrame.lockScreen = new LockScreen();
        MainFrame.queryScreen = new QueryScreen();
        MainFrame.queryScreen.setVisible(false);
        MainFrame.resultsScreen = new ResultsScreen();
        MainFrame.resultsScreen.setVisible(false);

        //configure this damn FRAME O.o
        this.setResizable(false);
        this.setUndecorated(true);
        this.setSize(400, 400);
        this.setLocationRelativeTo(null);
        this.setBackground(new Color(0, 0, 0, 0));
        this.setLayout(new GridBagLayout());

        //setup layer constraints
        gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 0;
        gc.gridwidth = 1;
        gc.gridheight = 1;
        gc.fill = GridBagConstraints.BOTH;
        gc.anchor = GridBagConstraints.CENTER;
        gc.weightx = 1;
        gc.weighty = 1;

        //begin adding components
        this.addPanel(lockScreen);
        this.addPanel(queryScreen);
        this.addPanel(resultsScreen);
        
        
        //starting animation
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        MainFrame.getLockScreen().getLockScreenPanel().animate("unlock");
    }

    public static LockScreen getLockScreen() {
        return lockScreen;
    }

    public static QueryScreen getQueryScreen() {
        return queryScreen;
    }
    
    public static ResultsScreen getResultsScreen() {
        return resultsScreen;
    }

    public final void addPanel(JComponent panel) {
        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                //check all components on this frame (or rather it's content pane)
                for (int i = 0; i < MainFrame.this.getContentPane().getComponentCount(); i++) {
                    //we only want to hide all OTHER components except the component we set to visible
                    if (!e.getComponent().equals(MainFrame.this.getContentPane().getComponent(i))) {
                        MainFrame.this.getContentPane().getComponent(i).setVisible(false);
                    }
                }
            }
        });
        this.add(panel, gc);
    }
}
