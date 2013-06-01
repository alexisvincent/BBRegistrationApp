package gui;

import components.BButton;
import components.BLabel;
import components.BLockButton;
import components.BMenuBar;
import components.BPanel;
import components.BTextPane;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;
import toolkit.BSettings;
import toolkit.BToolkit;

/**
 *
 * @author alexisvincent
 */
public class ResultsScreen extends BPanel {
    //declare components

    private BMenuBar menubar;
    private ResultsScreenPanel resultsScreenPanel;
    private ResultsScreenFooter resultsScreenFooter;
    private JComponent logoPanel; 

    public ResultsScreen() {
        //init components
        menubar = new BMenuBar();
        resultsScreenPanel = new ResultsScreenPanel();
        resultsScreenFooter = new ResultsScreenFooter();
        logoPanel = new JComponent() {
            Image logo = BToolkit.getImage("logo");
            Point pt = new Point(270, 20);;
            @Override
            public void paint(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
                g2d.drawImage(logo, pt.x, pt.y, (int) (logo.getWidth(this) / 1.5), (int) (logo.getHeight(this) / 1.5), this);
            }
        };
        //set ResultScreen properties
        this.setLayout(new GridBagLayout());
        this.setVisible(false);
        //add components to ResultScreen
        GridBagConstraints gc = new GridBagConstraints();
        
        gc.gridx = 0;
        gc.gridy = 0;
        gc.gridwidth = 1;
        gc.gridheight = 1;
        gc.weightx = 1;
        gc.weighty = 0;
        gc.fill = GridBagConstraints.BOTH;
        gc.anchor = GridBagConstraints.CENTER;
        this.add(menubar, gc);
        
        gc.gridheight = 3;
        this.add(logoPanel, gc);
        
        gc.gridy = 1;
        gc.weighty = 1;
        gc.gridheight = 1;
        this.add(resultsScreenPanel, gc);
        
        gc.gridy = 2;
        gc.weighty = 0;
        this.add(resultsScreenFooter, gc);

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                MainFrame.getResultsScreen().getResultsScreenFooter().getLockButton().setLocked(false);
                MainFrame.getResultsScreen().getResultsScreenFooter().getLockButton().setMouseOver(false);
                resultsScreenPanel.resetFields();
                resultsScreenPanel.animate("fadeIn");
            }
        });
    }

    public ResultsScreenPanel getResultsScreenPanel() {
        return resultsScreenPanel;
    }

    public ResultsScreenFooter getResultsScreenFooter() {
        return resultsScreenFooter;
    }
}

class ResultsScreenPanel extends JComponent {

    private String votersKey;
    private static Image logo;
    private int panelOpacity;
    private BTextPane votersKeyTextPane;
    private BLabel votersKeyLabel;
    private BButton finished, showQR, print;
    private GridBagConstraints gc;

    static {
        logo = BToolkit.getImage("logo");
    }

    public ResultsScreenPanel() {

        votersKeyLabel = new BLabel("Voters ID");
        votersKeyTextPane = new BTextPane();
        finished = new BButton("Finished");
        showQR = new BButton("Show QR");
        print = new BButton("Print");
        votersKey = "3sgdf8734yiuhiuhysdf9843y093\n09qwepoijw5fkj35";

        finished.setFont(BSettings.getFont("button"));
        showQR.setFont(BSettings.getFont("button"));
        print.setFont(BSettings.getFont("button"));
        votersKeyLabel.setFont(BSettings.getFont("label"));

        MutableAttributeSet set = votersKeyTextPane.getInputAttributes();
        StyleConstants.setFontFamily(set, BSettings.getFont("text").getFamily());
        StyleConstants.setFontSize(set, BSettings.getFont("text").getSize());
        votersKeyTextPane.setParagraphAttributes(set, true);


        finished.setPreferredSize(new Dimension(80, 30));
        showQR.setPreferredSize(new Dimension(80, 30));
        print.setPreferredSize(new Dimension(80, 30));
        votersKeyLabel.setPreferredSize(new Dimension(80, 16));
        votersKeyTextPane.setPreferredSize(new Dimension(270, 55));

        votersKeyTextPane.setEditable(false);
        votersKeyTextPane.setText(votersKey);

        finished.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                MainFrame.getQueryScreen().setVisible(true);
            }
        });

        panelOpacity = 0;

        this.setLayout(new GridBagLayout());
        gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 0;
        gc.gridwidth = 3;
        gc.gridheight = 1;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.ipadx = 0;
        gc.ipady = 0;
        gc.insets = new Insets(0, 15, 0, 0);
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.SOUTHWEST;
        this.add(votersKeyLabel, gc);

        gc.gridy = 1;
        gc.weighty = 0;
        gc.insets = new Insets(10, 15, 0, 0);
        this.add(votersKeyTextPane, gc);

        gc.gridy = 2;
        gc.gridwidth = 1;
        gc.weightx = 0;
        gc.insets = new Insets(20, 15, 90, 0);
        this.add(finished, gc);

        gc.gridx = 1;
        this.add(showQR, gc);

        gc.gridx = 2;
        gc.weightx = 1;
        this.add(print, gc);

    }

    public void animate(String action) {
        switch (action) {
            case "lock":
                MainFrame.getLockScreen().setVisible(true);
                break;

            case "queryScreen":
                MainFrame.getQueryScreen().setVisible(true);
                break;
            case "fadeIn":
                BSettings.STATE_queryScreen_isAnimating = true;

                Thread fadeIn = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            while (panelOpacity < 255) {
                                panelOpacity++;
                                repaint();
                                Thread.sleep(1);
                            }
                        } catch (InterruptedException ex) {
                            Logger.getLogger(LockScreenPanel.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        BSettings.STATE_queryScreen_isAnimating = false;
                    }
                });

                fadeIn.start();
                break;
        }
    }
    
    public void resetFields() {
        panelOpacity = 0;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(BToolkit.makeComposite(panelOpacity));
    }
}

class ResultsScreenFooter extends JComponent {

    private GridBagConstraints gc;
    private static BLockButton lockButton;

    public ResultsScreenFooter() {
        this.setLayout(new GridBagLayout());
        lockButton = new BLockButton();
        lockButton.setLocked(false);
        lockButton.setMouseOver(false);
        lockButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                lockButton.setLocked(true);
                MainFrame.getResultsScreen().getResultsScreenPanel().animate("lock");
            }
        });

        //position and add lockbutton
        gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 0;
        gc.gridwidth = 1;
        gc.gridheight = 1;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.WEST;
        gc.insets = new Insets(0, 10, 5, 0);
        this.add(lockButton, gc);
    }

    public BLockButton getLockButton() {
        return lockButton;
    }
}
