package project.client.UI.Elements;

import javax.swing.*;

import static project.client.UiMainWindow.GLOBAL_FONT;

public class SimpleTextField {
    JTextField textField;
    public SimpleTextField() {
        textField = new JTextField(10);
        textField.setEditable(true);
        textField.setFont(GLOBAL_FONT);
    }
    public SimpleTextField(int column) {
        textField = new JTextField(column);
        textField.setEditable(true);
        textField.setFont(GLOBAL_FONT);
    }

    public JTextField getTextField() {
        return textField;
    }
    public String getText() {
        return textField.getText();
    }
}
