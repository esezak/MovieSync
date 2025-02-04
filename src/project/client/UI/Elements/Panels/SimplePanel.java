package project.client.UI.Elements.Panels;

import javax.swing.*;
import java.awt.*;

public class SimplePanel {
    JPanel panel;
    public SimplePanel() {
        panel = new JPanel();
        panel.setBackground(Color.GRAY);
        panel.setLayout(new BorderLayout());
    }
    public JPanel getPanel() {
        return panel;
    }
}
