package gui;

import components.BFormattedTextField;
import components.BLabel;
import components.BLockButton;
import components.BMenuBar;
import components.BPanel;
import components.BTextField;
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
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.text.MaskFormatter;
import networking.ASocket;
import networking.Request;
import networking.Responce;
import org.jdom2.Document;
import org.jdom2.Element;
import toolkit.BSettings;
import toolkit.BToolkit;

/**
 * @about Da place you puta da money... I mean ID...
 * @author alexisvincent
 */
public class QueryScreen extends BPanel {
    //declare components

    private BMenuBar menubar;
    protected QueryScreenPanel queryScreenPanel;
    private QueryScreenFooter queryScreenFooter;
    private JComponent logoPanel;

    public QueryScreen() {
        //init components
        menubar = new BMenuBar();
        queryScreenPanel = new QueryScreenPanel();
        queryScreenFooter = new QueryScreenFooter();
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
        //set QueryScreen properties
        this.setLayout(new GridBagLayout());
        this.setVisible(false);
        //add components to QueryScreen
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
        this.add(queryScreenPanel, gc);
        
        gc.gridy = 2;
        gc.weighty = 0;
        this.add(queryScreenFooter, gc);
        
        
        
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                queryScreenPanel.resetFields();
                queryScreenFooter.getLockButton().setLocked(false);
                queryScreenFooter.getLockButton().setMouseOver(false);
                queryScreenPanel.animate("fadeIn");
            }
        });

    }

    public QueryScreenPanel getQueryScreenPanel() {
        return queryScreenPanel;
    }
}

class QueryScreenPanel extends JComponent {

    private Image logo;
    private int panelOpacity;
    protected BFormattedTextField idTextField;
    protected BTextField nameTextField;
    private BLabel idLabel;
    private BLabel nameLabel;
    private String idNumber;
    private String name;
    private GridBagConstraints gc;

    public QueryScreenPanel() {

        idLabel = new BLabel("ID Number");
        nameLabel = new BLabel("First Name");
        nameTextField = new BTextField();
        try {
            idTextField = new BFormattedTextField(new MaskFormatter("#############"));
            //so that the last good ID is not shown when an invalid one is set
            idTextField.setFocusLostBehavior(JFormattedTextField.COMMIT);
        } catch (ParseException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }

        KeyAdapter keyAdapter = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && !idTextField.equals("") && !nameTextField.equals("")) {
                    idNumber = idTextField.getText();
                    name = nameTextField.getText();
                    
                    Element rootElement = new Element("Request");
                    rootElement.setAttribute("idNumber", idNumber);
                    rootElement.setAttribute("firstName", name);
                    rootElement.setAttribute("RequestType", "VotersKey");
                    rootElement.setAttribute("From", "RegistrationApp");
                    Document document = new Document(rootElement);
                    
                    ASocket socket = main.Main.getINSTANCE().getNetworkingClient().getSocket();
                    Request request = new Request(document, socket);
                    Responce responce = socket.postRequest(request);
                    
                    if (responce.getResponceCode().equals("200")) {
                        rootElement = responce.getRootElement();
                        String votersKey = rootElement.getAttributeValue("VotersKey");
                        MainFrame.getResultsScreen().setVotersKey(votersKey);
                    } else {
                        MainFrame.getResultsScreen().setVotersKey("You entered invalid voter details\nPlease try again.");
                    }
                    
                    animate("resultsScreen");
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    System.out.println("Please make sure that you have entered in valid information");
                }
            }
        };
        idTextField.addKeyListener(keyAdapter);
        nameTextField.addKeyListener(keyAdapter);

        idLabel.setPreferredSize(new Dimension(100, 16));
        nameLabel.setPreferredSize(new Dimension(100, 16));
        idTextField.setPreferredSize(new Dimension(130, 25));
        nameTextField.setPreferredSize(new Dimension(200, 25));

        idLabel.setFont(BSettings.getFont("label"));
        nameLabel.setFont(BSettings.getFont("label"));
        idTextField.setFont(BSettings.getFont("text"));
        nameTextField.setFont(BSettings.getFont("text"));

        panelOpacity = 0;
        logo = BToolkit.getImage("logo");

        this.setLayout(new GridBagLayout());
        gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 0;
        gc.gridwidth = 1;
        gc.gridheight = 1;
        gc.weightx = 1;
        gc.weighty = 3;
        gc.insets = new Insets(30, 30, 0, 0);
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.SOUTHWEST;
        this.add(idLabel, gc);

        gc.gridy = 1;
        gc.weighty = 1;
        gc.insets = new Insets(5, 30, 0, 0);
        gc.anchor = GridBagConstraints.NORTHWEST;
        this.add(idTextField, gc);

        gc.gridy = 2;
        gc.weighty = 0.5;
        gc.insets = new Insets(15, 30, 0, 0);
        gc.anchor = GridBagConstraints.SOUTHWEST;
        this.add(nameLabel, gc);

        gc.gridy = 3;
        gc.weighty = 6;
        gc.insets = new Insets(5, 30, 0, 0);
        gc.anchor = GridBagConstraints.NORTHWEST;
        this.add(nameTextField, gc);

    }

    public void resetFields() {
        idTextField.setText("");
        nameTextField.setText("");
        panelOpacity = 0;
    }

    public void animate(String action) {
        switch (action) {
            case "lock":
                MainFrame.getLockScreen().setVisible(true);
                MainFrame.getLockScreen().getLockScreenPanel().animate("fadeInLogo");
                break;

            case "resultsScreen":
                nameTextField.setText("");
                MainFrame.getResultsScreen().setVisible(true);
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

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(BToolkit.makeComposite(panelOpacity));
    }
}

class QueryScreenFooter extends JComponent {

    private GridBagConstraints gc;
    private static BLockButton lockButton;

    public QueryScreenFooter() {

        lockButton = new BLockButton();
        lockButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                lockButton.setLocked(true);
                MainFrame.getQueryScreen().queryScreenPanel.animate("lock");
            }
        });

        //position and add lockbutton
        setLayout(new GridBagLayout());
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
