package project.client.UI.Elements.Panels;

import project.client.UiMainWindow;
import project.client.UI.Elements.FilmButton;
import project.common.Movie;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class CenterPanel extends SimplePanel implements Runnable{
    private JScrollPane scrollPane;
    private ArrayList<Movie> films = new ArrayList<>();
    private ArrayList<FilmButton> buttons = new ArrayList<>();
    private Movie toBeAdded;
    private UiMainWindow uiMainWindow;
    public CenterPanel(UiMainWindow uiMainWindow) {
        super();
        this.uiMainWindow = uiMainWindow;
        panel.setLayout(new GridLayout(0,1));
        scrollPane = new JScrollPane();
        scrollPane.setViewportView(panel);
        scrollPane.createVerticalScrollBar();
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    public void retrieveNewFilms() {
        panel.removeAll();
        Thread t = null;
        for(Movie c : films){
            toBeAdded = c;
            t = new Thread(this);
            t.start();
            try {
                t.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }
    public void retrieveOldFilms() {
        panel.removeAll();
        for(FilmButton f : buttons){
            panel.add(f.getButton());
        }
        panel.revalidate();
        panel.repaint();
    }
    @Override
    public void run() {
        FilmButton filmButton = new FilmButton(toBeAdded,this);
        buttons.add(filmButton);
        panel.add(filmButton.getButton());
        panel.revalidate();
        panel.repaint();
    }
    public void setFilms(ArrayList<Movie> films){
        this.films = films;
    }
    public void resetFilms(){
        films.clear();
    }
    public void resetButtons(){
        buttons.clear();
    }
    public UiMainWindow getClientMainWindow() {
        return uiMainWindow;
    }
}
