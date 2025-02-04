package project.client.UI.Elements.Panels;

import project.client.ServerConnection;
import project.client.UiMainWindow;
import project.client.UI.Elements.SimpleButton;
import project.client.UI.Elements.SimpleLabel;
import project.client.UI.Elements.SimpleTextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static project.client.UiMainWindow.GLOBAL_FONT;

public class RightPanel extends SimplePanel{
    private SimpleLabel usernameLabel = new SimpleLabel("Username:");
    private SimpleLabel passwordLabel = new SimpleLabel("Password:");
    private SimpleLabel connectionLabel = new SimpleLabel("Server:");
    public SimpleLabel connectionStatusLabel = new SimpleLabel("Not Connected");
    private SimpleLabel loginLabel = new SimpleLabel("Login Status:");
    public SimpleLabel loginStatusLabel = new SimpleLabel("Not Logged in");
    public SimpleTextField usernameTextField = new SimpleTextField();
    private JPasswordField passwordField;
    public SimpleButton connectButton = new SimpleButton("Connect");
    public SimpleButton disconnectButton = new SimpleButton("Disconnect");
    public SimpleButton loginButton = new SimpleButton("Login");
    public SimpleButton logoutButton = new SimpleButton("Logout");
    public SimpleButton signUpButton = new SimpleButton("Sign up");
    private TopPanel topPanel;
    private LeftPanel leftPanel;
    private ServerConnection connection;
    private UiMainWindow uiMainWindow;
    public RightPanel(UiMainWindow uiMainWindow){
        super();
        SimplePanel rightFormPanel = new SimplePanel();
        panel.setLayout(new GridLayout(3,1));
        passwordField = new JPasswordField();
        passwordField.setFont(GLOBAL_FONT);
        this.connection = uiMainWindow.connection;
        this.topPanel = uiMainWindow.topPanel;
        this.leftPanel = uiMainWindow.leftPanel;
        this.uiMainWindow = uiMainWindow;
        rightFormPanel.getPanel().setPreferredSize(new Dimension(250, 10));
        rightFormPanel.getPanel().setLayout(new GridLayout(6,2,5,5));
        rightFormPanel.getPanel().add(usernameLabel.getLabel());
        rightFormPanel.getPanel().add(usernameTextField.getTextField());
        rightFormPanel.getPanel().add(passwordLabel.getLabel());
        rightFormPanel.getPanel().add(passwordField);
        rightFormPanel.getPanel().add(connectButton.getButton());
        rightFormPanel.getPanel().add(disconnectButton.getButton());
        rightFormPanel.getPanel().add(connectionLabel.getLabel());
        rightFormPanel.getPanel().add(connectionStatusLabel.getLabel());
        rightFormPanel.getPanel().add(loginButton.getButton());
        rightFormPanel.getPanel().add(logoutButton.getButton());
        rightFormPanel.getPanel().add(loginLabel.getLabel());
        rightFormPanel.getPanel().add(loginStatusLabel.getLabel());
        loginStatusLabel.getLabel().setForeground(Color.RED);
        connectionStatusLabel.getLabel().setForeground(Color.red);
        connectButton.getButton().addActionListener(new RightPanel.ConnectButtonListener());
        disconnectButton.getButton().addActionListener(new RightPanel.DisconnectButtonListener());
        disconnectButton.getButton().setEnabled(false);
        loginButton.getButton().addActionListener(new RightPanel.LoginButtonListener());
        loginButton.getButton().setEnabled(false);
        logoutButton.getButton().addActionListener(new RightPanel.LogoutButtonListener());
        logoutButton.getButton().setEnabled(false);
        panel.add(rightFormPanel.getPanel());
        SimplePanel signupButtonHolderPanel = new SimplePanel();
        signUpButton.getButton().addActionListener(new SignUpButtonListener());
        signUpButton.getButton().setEnabled(false);
        signupButtonHolderPanel.getPanel().setLayout(new GridLayout(6,1));
        signupButtonHolderPanel.getPanel().add(signUpButton.getButton());
        panel.add(signupButtonHolderPanel.getPanel());
    }
    private class ConnectButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(connection.setConnection()){
                System.out.println("Connection established");
                connectionStatusLabel.getLabel().setForeground(Color.GREEN);
                connectionStatusLabel.getLabel().setText("Connected");
                connectButton.getButton().setEnabled(false);
                signUpButton.getButton().setEnabled(true);
                logoutButton.getButton().setEnabled(false);
                disconnectButton.getButton().setEnabled(true);
                loginButton.getButton().setEnabled(true);
                leftPanel.setFilmsButtonState(true);
                topPanel.setSearchStatus(true);
                uiMainWindow.isConnected = true;

            }else{
                System.err.println("Could not connect to server");
            }
        }
    }
    private class DisconnectButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(connection.sendDisconnectRequest()){
                System.out.println("Disconnected from server");
                connectButton.getButton().setEnabled(true);
                disconnectButton.getButton().setEnabled(false);
                loginButton.getButton().setEnabled(false);
                logoutButton.getButton().setEnabled(false);
                signUpButton.getButton().setEnabled(false);
                connectionStatusLabel.getLabel().setForeground(Color.red);
                connectionStatusLabel.getLabel().setText("Disconnected");
                topPanel.setSearchStatus(false);
                leftPanel.setFilmsButtonState(false);
                uiMainWindow.isConnected = false;
                uiMainWindow.isLoggedIn = false;
            }else{
                System.err.println("Could not disconnect from server");
            }
        }
    }
    private class LoginButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(connection.sendLoginRequest(usernameTextField.getText(), getPassword())){
                System.out.println("Logged in");
                uiMainWindow.setUsername(usernameTextField.getText());
                uiMainWindow.setPassword(getPassword());
                disconnectButton.getButton().setEnabled(false);
                loginButton.getButton().setEnabled(false);
                logoutButton.getButton().setEnabled(true);
                connectButton.getButton().setEnabled(false);
                leftPanel.setWatchlistButtonState(true);
                signUpButton.getButton().setEnabled(false);
                loginStatusLabel.getLabel().setForeground(Color.green);
                loginStatusLabel.getLabel().setText("Logged in");
                usernameTextField.getTextField().setEditable(false);
                passwordField.setEditable(false);
                uiMainWindow.isConnected = true;
                uiMainWindow.isLoggedIn = true;
                uiMainWindow.leftPanel.setWatchlist();
            }else{
                System.err.println("Could not login");
            }
        }
    }
    private class LogoutButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(connection.sendLogoutRequest()){
                System.out.println("Logged out");
                uiMainWindow.setUsername(null);
                uiMainWindow.setPassword(null);
                disconnectButton.getButton().setEnabled(true);
                loginButton.getButton().setEnabled(true);
                logoutButton.getButton().setEnabled(false);
                leftPanel.setWatchlistButtonState(false);
                loginStatusLabel.getLabel().setText("Not Logged in");
                loginStatusLabel.getLabel().setForeground(Color.red);
                usernameTextField.getTextField().setEditable(true);
                passwordField.setEditable(true);
                signUpButton.getButton().setEnabled(true);
                uiMainWindow.isConnected = true;
                uiMainWindow.isLoggedIn = false;
                uiMainWindow.leftPanel.deleteOldUserData();
                uiMainWindow.centerPanel.getPanel().removeAll();
                uiMainWindow.centerPanel.getPanel().revalidate();
                uiMainWindow.centerPanel.getPanel().repaint();
            }
        }
    }
    private class SignUpButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if(uiMainWindow.connection.sendSignupRequest(usernameTextField.getText(), getPassword())){
                System.out.println("Signed up");
            }else{
                System.err.println("Could not signup");
            }
        }
    }
    public String getPassword() {
        return passwordField.getText();
    }
}
