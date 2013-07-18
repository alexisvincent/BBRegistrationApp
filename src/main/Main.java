package main;

import components.BMenuBar;
import gui.MainFrame;
import gui.SplashScreen;
import javax.swing.JFrame;
import networking.ElectionProfile;
import networking.NetworkingClient;
import networking.Server;
import settingsEngine.SettingsEngine;

/**
 *
 * @author alexisvincent
 */
public class Main {
    
    private static Main INSTANCE;

    private static MainFrame mainFrame;
    private static SplashScreen splash;
    private static String serverName = "RegistrationServer";
    
    //Engines and such nonsense
    SettingsEngine settingsEngine;
    ElectionProfile electionProfile;
    Server server;
    NetworkingClient networkingClient;

    public static void main(String[] args) {
        INSTANCE = new Main();
    }

    public Main() {

        //set global properties
        System.setProperty("apple.awt.graphics.EnableQ2DX", "true");
        System.setProperty("apple.awt.graphics.UseQuartz", "true");
        
        //splashScreen
        splash = new SplashScreen();
        splash.setVisible(true);
        
        //StartEngines etc...
        settingsEngine = new SettingsEngine();
        setElectionProfile(new ElectionProfile(settingsEngine.getRegistrationServer()));

        //init mainFrame
        mainFrame = new MainFrame();
        BMenuBar.setMainFrame(mainFrame);
        mainFrame.setVisible(true);
        splash.setVisible(false);
    }

    public static JFrame getMainFrame() {
        return mainFrame;
    }

    public ElectionProfile getElectionProfile() {
        return electionProfile;
    }

    public void setElectionProfile(ElectionProfile electionProfile) {
        this.electionProfile = electionProfile;
        this.server = electionProfile.getServer(serverName);
        this.networkingClient = new NetworkingClient(server);
    }

    public Server getServer() {
        return server;
    }

    public NetworkingClient getNetworkingClient() {
        return networkingClient;
    }

    public static Main getINSTANCE() {
        return INSTANCE;
    }
    
}
