package project.client;

import project.client.UI.Elements.Panels.*;
import project.client.UI.Elements.SimpleLabel;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
public class UiMainWindow {
    private JFrame frame;
    public TopPanel topPanel;
    public LeftPanel leftPanel;
    public RightPanel rightPanel;
    public CenterPanel centerPanel;
    public static final Font GLOBAL_FONT = new Font("Arial", Font.BOLD, 16);
    public static final Border GLOBAL_BORDER = BorderFactory.createEmptyBorder(5,5,5,5);
    public ServerConnection connection;
    public boolean isConnected = false;
    public boolean isLoggedIn = false;
    private String username;
    private String password;


    public UiMainWindow() {
        initialize();
    }

    /**
     * initializes elements
     */
    private void initialize() {
        connection = new ServerConnection("localhost",12345);
        setFrame();
        setCenterPanel();
        setLeftPanel();
        setTopPanel();
        setRightPanel();
        setBottomPanel();

    }

    /**
     * Sets up the frame
     */
    private void setFrame(){
        frame = new JFrame();
        frame.setSize(1280, 720);
        frame.setTitle("Movie Sync");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout(5,5));
        frame.getContentPane().setBackground(Color.DARK_GRAY);
        frame.setVisible(true);
    }
    private void setTopPanel(){
        topPanel = new TopPanel(this);
        frame.getContentPane().add(topPanel.getPanel(), BorderLayout.NORTH);
    }
    private void setCenterPanel(){
        centerPanel = new CenterPanel(this);
        frame.getContentPane().add(centerPanel.getScrollPane(), BorderLayout.CENTER);
    }

    private void setLeftPanel(){
        leftPanel = new LeftPanel(this);
        frame.add(leftPanel.getPanel(), BorderLayout.WEST);
    }
    private void setRightPanel(){
        rightPanel = new RightPanel(this);
        frame.getContentPane().add(rightPanel.getPanel(), BorderLayout.EAST);
    }

    private void setBottomPanel(){
        frame.getContentPane().add(new BottomPanel().getPanel(), BorderLayout.SOUTH);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    private static class BottomPanel extends SimplePanel {
        public BottomPanel() {
            super();
            getPanel().setLayout(new FlowLayout(FlowLayout.RIGHT));
            SimpleLabel label = new SimpleLabel("Powered By: TVDB.com");
            label.getLabel().setForeground(Color.BLUE);
            getPanel().add(label.getLabel());
        }
    }
    public static class Launcher {
        public static void main(String[] args) {
            SwingUtilities.invokeLater(() -> {
                UiMainWindow main = new UiMainWindow();});
        }
    }
}
