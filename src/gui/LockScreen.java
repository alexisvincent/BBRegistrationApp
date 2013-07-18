package gui;

import components.BLockButton;
import components.BMenuBar;
import components.BPanel;
import components.BPasswordField;
import components.BRollingLabel;
import java.awt.BorderLayout;
import java.awt.Color;
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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import networking.ASocket;
import networking.Request;
import networking.Responce;
import org.jdom2.Document;
import org.jdom2.Element;
import toolkit.BSettings;
import toolkit.BToolkit;

/**
 * LockScreen
 *
 * @author alexisvincent
 */
public class LockScreen extends BPanel {

    //declare components
    private BMenuBar menubar;
    private LockScreenPanel lockScreenPanel;
    private LockScreenFooter lockScreenFooter;

    public LockScreen() {

        //init components
        menubar = new BMenuBar();
        lockScreenPanel = new LockScreenPanel();
        lockScreenFooter = new LockScreenFooter();

        //set LockScreen properties
        this.setLayout(new BorderLayout());

        //add components to LockScreen
        this.add(menubar, BorderLayout.NORTH);
        this.add(lockScreenPanel, BorderLayout.CENTER);
        this.add(lockScreenFooter, BorderLayout.SOUTH);

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                lockScreenFooter.lockButton.setLocked(true);
                lockScreenPanel.animate("fadeInLogo");
                lockScreenPanel.getPasswordLabel().reset();
            }
        });
    }

    public LockScreenPanel getLockScreenPanel() {
        return lockScreenPanel;
    }

    public LockScreenFooter getLockScreenFooter() {
        return lockScreenFooter;
    }
}

//Center Panel
class LockScreenPanel extends JComponent {

    private static final Point LOCKED_POSITION;
    private static final Point UNLOCKED_POSITION;
    private Point pt;
    private static Image logo;
    private static BPasswordField passwordField;
    private static BRollingLabel passwordLabel;
    private static int textFieldOpacity;
    private static int logoOpacity;
    private GridBagConstraints gc;
    private String passwordGOV;
    private String passwordUN;

    static {
        LOCKED_POSITION = new Point(129, 75);
        UNLOCKED_POSITION = new Point(129, 25);
        textFieldOpacity = 0;
        logoOpacity = 0;
        logo = BToolkit.getImage("logo");
    }

    public LockScreenPanel() {

        pt = new Point(LOCKED_POSITION.x, LOCKED_POSITION.y);

        //init text field
        passwordField = new BPasswordField() {
            @Override
            public void paint(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setComposite(BToolkit.makeComposite(textFieldOpacity));
                super.paint(g2d);
            }
        };
        passwordField.setEditable(false);
        passwordField.setPreferredSize(new Dimension(200, 30));
        passwordField.setFont(BSettings.getFont("text"));

        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && passwordGOV == null && passwordField.isEditable()) {
                    animate("rollText");
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER && !(passwordGOV == null) && passwordUN == null && passwordField.isEditable()) {
                    animate("connection");
                    //init FAKE Connection
                    Thread t1 = new Thread(new Runnable() {
                        @Override
                        public void run() {

//                            try {
//                                Thread.sleep(1200);
//                            } catch (InterruptedException ex) {
//                                Logger.getLogger(LockScreenPanel.class.getName()).log(Level.SEVERE, null, ex);
//                            }

                            ASocket socket = main.Main.getINSTANCE().getNetworkingClient().getSocket();

                            if (socket != null) {
                                Element rootElement = new Element("Request");
                                rootElement.setAttribute("RequestType", "Login");
                                rootElement.setAttribute("From", "RegistrationApp");
                                rootElement.setAttribute("UNPassword", passwordUN);
                                rootElement.setAttribute("GOVPassword", passwordGOV);

                                Request request = new Request(new Document(rootElement), socket);
                                Responce responce = request.getSocket().postRequest(request);

                                if (responce == null || responce.getResponceCode() == null) {
                                    BSettings.STATE_lockScreen_loginSuccess = false;
                                } else if (responce.getResponceCode().equals("200")) {
                                    BSettings.STATE_lockScreen_loginSuccess = true;
                                } else {
                                    BSettings.STATE_lockScreen_loginSuccess = false;
                                }
                            } else {
                                System.out.println("Failed");
                                BSettings.STATE_lockScreen_loginSuccess = false;
                            }
                            
                            BSettings.STATE_lockScreen_isConnecting = false;
                            passwordGOV = null;
                            passwordUN = null;
                        }
                    });
                    t1.start();
                }
            }
        });

        passwordLabel = new BRollingLabel("Governmental Password", "UnitedNations Password") {
            @Override
            public void paint(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setComposite(BToolkit.makeComposite(textFieldOpacity));
                super.paint(g2d);
            }
        };
        passwordLabel.setPreferredSize(new Dimension(200, 16));
        passwordLabel.setFont(BSettings.getFont("label"));

        //position and add passwordField
        this.setLayout(new GridBagLayout());
        gc = new GridBagConstraints();

        gc.gridx = 0;
        gc.gridy = 0;
        gc.gridheight = 1;
        gc.gridwidth = 1;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.SOUTH;
        gc.insets = new Insets(0, 0, 10, 0);
        this.add(passwordLabel, gc);

        gc.gridx = 0;
        gc.gridy = 1;
        gc.gridheight = 1;
        gc.gridwidth = 1;
        gc.weightx = 1;
        gc.weighty = 0;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.SOUTH;
        gc.insets = new Insets(0, 0, 40, 0);
        this.add(passwordField, gc);

    }

    public void animate(String action) {
        switch (action) {
            case "unlock":
                BSettings.STATE_lockScreen_isAnimating = true;
                BSettings.COLOR_lockScreen_passwordField = Color.WHITE;
                passwordField.setForeground(BSettings.COLOR_lockScreen_passwordField);
                passwordField.setEditable(true);
                passwordField.getCaret().setVisible(true);
                passwordField.setText("");
                passwordField.requestFocus();
                passwordLabel.reset();
                passwordGOV = null;
                passwordUN = null;

                Thread unlockThread;
                unlockThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            while (logoOpacity < 255) {
                                logoOpacity++;
                                repaint();
                                Thread.sleep(1);
                            }
                        } catch (InterruptedException ex) {
                            Logger.getLogger(LockScreenPanel.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        //animation: shift logo up
                        Thread shiftLogoThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    while (pt.y > UNLOCKED_POSITION.y) {
                                        pt.y--;
                                        repaint();
                                        Thread.sleep(7);
                                    }
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(LockScreen.class
                                            .getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        });
                        //animation: fade in passwordField
                        Thread fadeInThread;
                        fadeInThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(100);
                                    while (textFieldOpacity < 255) {
                                        textFieldOpacity++;
                                        repaint();
                                        Thread.sleep(1);
                                    }
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(LockScreen.class
                                            .getName()).log(Level.SEVERE, null, ex);
                                }
                                BSettings.STATE_lockScreen_isAnimating = false;
                            }
                        });

                        fadeInThread.start();
                        shiftLogoThread.start();
                    }
                });
                unlockThread.start();
                break;

            case "lock":
                BSettings.STATE_lockScreen_isAnimating = true;
                passwordField.setEditable(false);

                //animation: move logo down
                Thread lockThread1 = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(100);
                            while (pt.y < LOCKED_POSITION.y) {
                                pt.y++;
                                repaint();
                                Thread.sleep(7);
                            }
                        } catch (InterruptedException ex) {
                            Logger.getLogger(LockScreen.class
                                    .getName()).log(Level.SEVERE, null, ex);
                        }
                        BSettings.STATE_lockScreen_isAnimating = false;
                    }
                });
                //animation: fade passwordField out
                Thread lockThread2 = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            while (textFieldOpacity > 0) {
                                textFieldOpacity--;
                                repaint();
                                Thread.sleep(1);
                            }
                        } catch (InterruptedException ex) {
                            Logger.getLogger(LockScreen.class
                                    .getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });

                lockThread1.start();
                lockThread2.start();
                break;

            case "rollText":
                passwordGOV = BToolkit.getPass(passwordField.getPassword());
                passwordField.setText("");
                passwordLabel.roll();
                break;

            case "fadeInLogo":
                BSettings.STATE_requestScreen_isAnimating = true;

                Thread fadeInLogoThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            while (logoOpacity < 255) {
                                logoOpacity++;
                                repaint();
                                Thread.sleep(1);
                            }
                        } catch (InterruptedException ex) {
                            Logger.getLogger(LockScreenPanel.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        BSettings.STATE_requestScreen_isAnimating = false;
                    }
                });

                fadeInLogoThread.start();
                break;

            case "fadeOutLogo":
                BSettings.STATE_requestScreen_isAnimating = true;

                Thread fadeOutLogoThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            while (logoOpacity > 0) {
                                logoOpacity--;
                                repaint();
                                Thread.sleep(1);
                            }
                        } catch (InterruptedException ex) {
                            Logger.getLogger(LockScreenPanel.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        BSettings.STATE_requestScreen_isAnimating = false;
                    }
                });
                fadeOutLogoThread.start();
                break;

            case "connection":
                BSettings.STATE_lockScreen_isConnecting = true;
                BSettings.STATE_lockScreen_isAnimating = true;
                passwordField.setEditable(false);
                passwordField.getCaret().setVisible(false);
                passwordUN = BToolkit.getPass(passwordField.getPassword());
                BSettings.COLOR_lockScreen_passwordField = new Color(20, 200, 112, 255);
                passwordField.setForeground(BSettings.COLOR_lockScreen_passwordField);

                Thread connectionThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //fade out
                            while (textFieldOpacity > 0) {
                                textFieldOpacity--;
                                repaint();
                                Thread.sleep(2);
                            }
                            //flash while connection has not been initiated
                            while (BSettings.STATE_lockScreen_isConnecting) {
                                while (textFieldOpacity < 255) {
                                    textFieldOpacity++;
                                    repaint();
                                    Thread.sleep(2);
                                }
                                while (textFieldOpacity > 0) {
                                    textFieldOpacity--;
                                    repaint();
                                    Thread.sleep(2);
                                }
                            }
                        } catch (InterruptedException ex) {
                            Logger.getLogger(LockScreen.class
                                    .getName()).log(Level.SEVERE, null, ex);
                        }
                        BSettings.STATE_lockScreen_isAnimating = false;
                        if (BSettings.STATE_lockScreen_loginSuccess) {
                            animate("queryScreen");
                        } else {
                            animate("unlock");
                        }

                        BSettings.STATE_lockScreen_loginSuccess = false;

                    }
                });

                Thread dots = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String text = "";
                        while (BSettings.STATE_lockScreen_isConnecting) {
                            text = ".";
                            passwordField.setText(text);
                            while (!BToolkit.getPass(passwordField.getPassword()).equals("......................")) {
                                passwordField.setText(text);
                                text += ".";
                                try {
                                    Thread.sleep(150);
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(LockScreenPanel.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                if (!BSettings.STATE_lockScreen_isConnecting) {
                                    passwordField.setText("");
                                    break;
                                }
                            }
                        }
                    }
                });

                connectionThread.start();
                dots.start();

                break;

            case "queryScreen":
                BSettings.STATE_lockScreen_isAnimating = true;
                Thread nextPanelThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //animation: move logo down
                            while (pt.y < LOCKED_POSITION.y) {
                                pt.y++;
                                repaint();
                                Thread.sleep(7);
                            }
                            while (logoOpacity > 0) {
                                logoOpacity--;
                                repaint();
                                Thread.sleep(1);
                            }
                        } catch (InterruptedException ex) {
                            Logger.getLogger(LockScreen.class
                                    .getName()).log(Level.SEVERE, null, ex);
                        }
                        BSettings.STATE_lockScreen_isAnimating = false;
                        MainFrame.getQueryScreen().setVisible(true);
                    }
                });
                nextPanelThread.start();
                break;
        }
    }

    public BRollingLabel getPasswordLabel() {
        return passwordLabel;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(BToolkit.makeComposite(logoOpacity));
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.drawImage(logo, pt.x, pt.y, logo.getWidth(this), logo.getHeight(this), this);
    }
}

class LockScreenFooter extends JComponent {

    private GridBagConstraints gc;
    public static BLockButton lockButton;

    public LockScreenFooter() {
        setLayout(new GridBagLayout());
        lockButton = new BLockButton();
        lockButton.setLocked(false);
        lockButton.setMouseOver(false);
        lockButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (lockButton.isLocked() && !BSettings.STATE_lockScreen_isAnimating) {
                    lockButton.setLocked(false);
                    MainFrame.getLockScreen().getLockScreenPanel().animate("unlock");
                } else if (!lockButton.isLocked() && !BSettings.STATE_lockScreen_isAnimating) {
                    lockButton.setLocked(true);
                    MainFrame.getLockScreen().getLockScreenPanel().animate("lock");
                }
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
}