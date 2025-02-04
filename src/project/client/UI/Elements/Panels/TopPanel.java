package project.client.UI.Elements.Panels;

import project.client.ServerConnection;
import project.client.UiMainWindow;
import project.client.UI.Elements.SimpleButton;
import project.client.UI.Elements.SimpleLabel;
import project.client.UI.Elements.SimpleTextField;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class TopPanel extends SimplePanel {
    public SimpleButton searchButton;
    public SimpleTextField searchTextField = new SimpleTextField(20);
    private SimpleLabel label = new SimpleLabel("Search:");
    private CenterPanel centerPanel;
    private ServerConnection connection;
    public TopPanel(UiMainWindow uiMainWindow) {
        super();
        this.connection = uiMainWindow.connection;
        this.centerPanel = uiMainWindow.centerPanel;
        searchButton = new SimpleButton("Search");
        searchButton.getButton().addActionListener(new onSearchButtonClick());
        searchButton.getButton().setMnemonic(KeyEvent.VK_ENTER);
        panel.setLayout(new FlowLayout(FlowLayout.CENTER));
        panel.add(label.getLabel());
        panel.add(searchTextField.getTextField());
        panel.add(searchButton.getButton());
        setSearchStatus(false);
    }
    public void setSearchStatus(boolean status) {
        setSearchTextFieldState(status);
        setSearchButtonState(status);
    }
    public void setSearchButtonState(boolean state) {
        searchButton.getButton().setEnabled(state);
    }
    public void setSearchTextFieldState(boolean state) {
        searchTextField.getTextField().setEnabled(state);
    }
    private class onSearchButtonClick implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            centerPanel.resetButtons();
            centerPanel.resetFilms();
            if(!searchTextField.getText().isEmpty()) {
                centerPanel.setFilms(connection.sendFilmQuery(searchTextField.getText()).getMovies());
            }
            centerPanel.retrieveNewFilms();

        }
    }

}
