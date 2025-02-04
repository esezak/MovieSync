package project.client.UI.Elements;

import project.client.UI.Elements.Panels.CenterPanel;
import project.client.UI.Elements.Panels.FilmPanel;
import project.common.Movie;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;

public class FilmButton extends SimpleButton {
    private ImageIcon icon;
    private Movie film;
    private CenterPanel centerPanel;
    private String infoString;
    private FilmPanel filmPanel;
    public FilmButton(Movie movie, CenterPanel centerPanel) {
        super("text");
        this.film = movie;
        this.centerPanel = centerPanel;

        infoString = "<html><body><h2>"+FilmPanel.formatLongText(movie.getTitle())+"</h2>"+
                "<p style=\"font-size:11px\">Genres: "+movie.getGenres()+"</p>"+
                "<p style=\"font-size:11px\">Overview: "+ formatOverview()+"</p>"+
                "<p>Rating: "+movie.getAvg_rating()+"</p></body></html>";
        button.setText(infoString);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setVerticalTextPosition(SwingConstants.NORTH);
        button.setHorizontalTextPosition(SwingConstants.RIGHT);
        button.setBackground(Color.gray);
        button.addActionListener(new on_film_button_click());

        setIcon(this.film.getImage_url());
    }
    private void setIcon(String imgpath){
        try{
            URL url = new URL(imgpath);
            icon = new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(170, 250, Image.SCALE_SMOOTH));
            button.setIcon(icon);
        } catch (MalformedURLException e) {
            System.err.println("could not get image URL");
        }
    }
    private String formatOverview(){
        String[] words = film.getOverview().split(" ");
        StringBuilder formatted = new StringBuilder();
        for(int i = 0; i < words.length; i++){
            if(i != 0 && i%10 == 0){
                formatted.append("<br>");
            }
            if(words.length < 20 && words[i].length() > 20){//insert endline for languages like japanese that don't have spaces
                formatted.append("<br>");
            }
            formatted.append(words[i]+" ");
        }
        this.film.setOverview(formatted.toString());
        return formatted.toString();
    }
    private class on_film_button_click implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            centerPanel.getPanel().removeAll();
            if(filmPanel == null){
                filmPanel = new FilmPanel(film,icon, centerPanel.getClientMainWindow());
            }
            filmPanel.initialize();
            filmPanel.setReviewButtonStates();
            centerPanel.getPanel().add(filmPanel.getPanel());
            centerPanel.getPanel().revalidate();
            centerPanel.getPanel().repaint();
        }
    }
}
