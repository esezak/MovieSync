package project.client.UI.Elements.Panels;

import project.client.UiMainWindow;
import project.client.UI.Elements.SimpleButton;
import project.client.UI.Elements.SimpleLabel;
import project.common.Response;
import project.common.Movie;
import project.common.Review;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import static project.client.UiMainWindow.GLOBAL_FONT;

public class FilmPanel extends SimplePanel {
    private SimplePanel filmDetailsPanel;
    private JScrollPane scrollPane;
    private SimplePanel reviewsPanel;
    public Movie film;
    private String infoString;
    private CommentHolderPanel commentHolderPanel;
    SimplePanel userInputHolderPanel;
    Integer[] ratings = {0,1,2,3,4,5,6,7,8,9,10};
    private JComboBox<Integer> ratingsComboBox;
    private SimplePanel buttonsPanel;
    public SimpleButton commentButton;
    public SimpleButton addRemoveFromWatchListButton;
    UiMainWindow uiMainWindow;
    int userRating;
    private Review review;
    private boolean movieFound;
    private ImageIcon icon;
    private ImageIcon addIcon = new ImageIcon("src/project/client/UI/Elements/Icons/add.png");
    private ImageIcon deleteIcon = new ImageIcon("src/project/client/UI/Elements/Icons/delete.png");

    public FilmPanel(Movie film, ImageIcon icon, UiMainWindow uiMainWindow) {
        super();
        this.uiMainWindow = uiMainWindow;
        this.ratingsComboBox = new JComboBox<>(ratings);
        ratingsComboBox.setSelectedIndex(0);
        userRating = ratingsComboBox.getItemAt(ratingsComboBox.getSelectedIndex());
        ratingsComboBox.setFont(GLOBAL_FONT);

        this.film = film;
        this.icon = icon;


        infoString = "<html><body><div><h2>"+formatLongText(film.getTitle())+"</h2>"+
                "<p style=\"font-size:11px\">Genres: "+film.getGenres()+"</p>"+
                "<p style=\"font-size:11px\">  Overview: "+film.getOverview()+"</p>"+
                "<p>  Rating: "+film.getAvg_rating()+"</p></div></body></html>";

        scrollPane = new JScrollPane();
        scrollPane.setViewportView(panel);
        scrollPane.createVerticalScrollBar();
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);

        filmDetailsPanel = new SimplePanel();


        userInputHolderPanel = new SimplePanel();

        commentButton = new SimpleButton("Write a comment");
        commentButton.getButton().setBorder(BorderFactory.createLineBorder(Color.black));


            //--------------Add to watchlist button--------------
        addRemoveFromWatchListButton = new SimpleButton("");
        addRemoveFromWatchListButton.getButton().setBorder(BorderFactory.createLineBorder(Color.black));
        addRemoveFromWatchListButton.getButton().setEnabled(uiMainWindow.isLoggedIn);
        addRemoveFromWatchListButton.getButton().addActionListener(new addOrRemoveFromWatchList());
        buttonsPanel = new SimplePanel();
        buttonsPanel.getPanel().setLayout(new GridLayout(1,2));
        reviewsPanel = new SimplePanel();
        reviewsPanel.getPanel().setLayout(new GridLayout(0,1));
        addRemoveFromWatchListButton.getButton().setIcon(addIcon);
        commentButton.getButton().addActionListener(new onReviewButtonClick());
        initialize();
    }
    public void initialize(){
        SimpleLabel movieBanner = new SimpleLabel(icon,infoString);//Movie details
        movieBanner.getLabel().setIconTextGap(10);
        filmDetailsPanel.getPanel().add(movieBanner.getLabel(),BorderLayout.NORTH);
        panel.add(filmDetailsPanel.getPanel(),BorderLayout.NORTH);
        buttonsPanel.getPanel().add(commentButton.getButton());
        buttonsPanel.getPanel().add(addRemoveFromWatchListButton.getButton());
        userInputHolderPanel.getPanel().add(buttonsPanel.getPanel(),BorderLayout.NORTH);
        panel.add(userInputHolderPanel.getPanel(),BorderLayout.CENTER);
        movieFound = uiMainWindow.leftPanel.isMovieInWatchlist(film.getId());
        System.out.println("current movie found: "+movieFound);
        updateReviews();
        setReviewButtonStates();
        panel.revalidate();
        panel.repaint();
    }
    public void setReviewButtonStates(){
        if(!uiMainWindow.isLoggedIn){//not logged in
            commentButton.getButton().setEnabled(false);
            addRemoveFromWatchListButton.getButton().setEnabled(false);
            commentButton.getButton().setToolTipText("You are not logged in");
            addRemoveFromWatchListButton.getButton().setToolTipText("You are not logged in");
            addRemoveFromWatchListButton.getButton().setText("Add to watch list");
            addRemoveFromWatchListButton.getButton().setIcon(addIcon);
        }else if(!movieFound){// movie not in watchlist
            commentButton.getButton().setEnabled(false);
            commentButton.getButton().setToolTipText("You must add to watchlist to write a comment");
            addRemoveFromWatchListButton.getButton().setEnabled(true);
            addRemoveFromWatchListButton.getButton().setText("Add to watch list");
            addRemoveFromWatchListButton.getButton().setIcon(addIcon);
            addRemoveFromWatchListButton.getButton().setToolTipText(null);
        }else{// movie in watchlist
            commentButton.getButton().setEnabled(true);
            commentButton.getButton().setToolTipText(null);
            addRemoveFromWatchListButton.getButton().setText("Remove from Watchlist");
            addRemoveFromWatchListButton.getButton().setIcon(deleteIcon);
            addRemoveFromWatchListButton.getButton().setEnabled(true);
            addRemoveFromWatchListButton.getButton().setToolTipText(null);
        }
    }
    private void addReview(Review review) {
        String htmlReview = "<html><body><h2>User: "+review.getUsername()+"</h2>"+
                "<p style=\"font-size:12px\">Score: "+review.getRating()+"</p>"+
                "<p style=\"font-size:12px\">Comment: "+formatLongText(review.getComment())+"</p></body></html>";
        SimpleLabel label = new SimpleLabel(htmlReview);
        reviewsPanel.getPanel().add(label.getLabel());
    }
    public static String formatLongText(String longText) {
        String[] words = longText.split(" ");
        StringBuilder formatted = new StringBuilder();
        for(int i = 0; i < words.length; i++){
            if(i != 0 && i%10 == 0){
                formatted.append("<br>");
            }
            formatted.append(words[i]).append(" ");
        }
        return formatted.toString();
    }
    public void updateReviews(){
        reviewsPanel.getPanel().removeAll();
        Response response = uiMainWindow.connection.getFilmInformation(film.getId());
        System.out.println(response.getData());
        ArrayList<Review> reviews = response.getReviews();
        for(int i = 0; i < reviews.size(); i++){
            addReview(reviews.get(i));
        }
        panel.add(reviewsPanel.getPanel(),BorderLayout.SOUTH);
    }
    private class CommentHolderPanel {
        private SimplePanel reviewPanel;
        private SimplePanel submitAndRatingPanel;
        private SimpleButton submitButton;
        private SimpleLabel reviewLabel;
        private JTextArea reviewTextArea;
        public CommentHolderPanel(){
            reviewPanel = new SimplePanel();
            submitAndRatingPanel = new SimplePanel();
            submitButton = new SimpleButton("Submit");
            submitButton.getButton().addActionListener(new onSubmitButtonClick());
            reviewLabel = new SimpleLabel("Review:");
            reviewTextArea = new JTextArea();
            reviewTextArea.setFont(GLOBAL_FONT);
            submitAndRatingPanel.getPanel().add(submitButton.getButton(), BorderLayout.EAST);
            submitAndRatingPanel.getPanel().add(ratingsComboBox, BorderLayout.CENTER);
            reviewPanel.getPanel().add(reviewLabel.getLabel(),BorderLayout.NORTH);
            reviewPanel.getPanel().add(reviewTextArea,BorderLayout.CENTER);
            reviewPanel.getPanel().add(submitAndRatingPanel.getPanel(),BorderLayout.EAST);
        }
        public JPanel getPanel(){
            return reviewPanel.getPanel();
        }
    }
    private class onReviewButtonClick implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(commentHolderPanel != null){
                commentHolderPanel.getPanel().removeAll();
                userInputHolderPanel.getPanel().remove(commentHolderPanel.getPanel());
                initialize();
                userInputHolderPanel.getPanel().revalidate();
                userInputHolderPanel.getPanel().repaint();
                commentHolderPanel = null;
                addRemoveFromWatchListButton.getButton().setEnabled(true);
                addRemoveFromWatchListButton.getButton().setToolTipText(null);
                uiMainWindow.centerPanel.getPanel().revalidate();
                uiMainWindow.centerPanel.getPanel().repaint();
            }else{
                commentHolderPanel = new CommentHolderPanel();
                userInputHolderPanel.getPanel().add(commentHolderPanel.getPanel(),BorderLayout.CENTER);
                addRemoveFromWatchListButton.getButton().setEnabled(false);
                addRemoveFromWatchListButton.getButton().setToolTipText("You must stop writing a comment to remove from Watchlist");
                uiMainWindow.centerPanel.getPanel().revalidate();
                uiMainWindow.centerPanel.getPanel().repaint();
            }
        }
    }
    private class addOrRemoveFromWatchList implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(addRemoveFromWatchListButton.getButton().getText().equals("Remove from Watchlist")){
                if (uiMainWindow.connection.sendDeleteFromWatchListRequest(film.getId(), uiMainWindow.getUsername())) {
                    System.out.println("Succesfully removed from watchlist");
                    addRemoveFromWatchListButton.getButton().setText("Add to watch list");
                    addRemoveFromWatchListButton.getButton().setIcon(addIcon);
                    movieFound = false;
                    setReviewButtonStates();
                    uiMainWindow.centerPanel.getPanel().revalidate();
                    uiMainWindow.centerPanel.getPanel().repaint();
                }else{
                    System.out.println("Failed to remove from watchlist");
                }
            }else{
                if (uiMainWindow.connection.sendAddToWatchListRequest(film.getId(), uiMainWindow.getUsername())) {
                    System.out.println("Added to watch list");
                    addRemoveFromWatchListButton.getButton().setText("Remove from Watchlist");
                    addRemoveFromWatchListButton.getButton().setIcon(deleteIcon);
                    movieFound = true;
                    setReviewButtonStates();
                    uiMainWindow.centerPanel.getPanel().revalidate();
                    uiMainWindow.centerPanel.getPanel().repaint();
                }else{
                    System.out.println("Failed to add to watch list");
                }
            }
        }
    }
    private class onSubmitButtonClick implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            review = getReview();
            System.out.println("Review: "+review.toJson());
            uiMainWindow.connection.sendRateMovieRequest(film.getId(),review);
            commentHolderPanel.getPanel().removeAll();
            userInputHolderPanel.getPanel().remove(commentHolderPanel.getPanel());
//            initialize();
            updateReviews();
            userInputHolderPanel.getPanel().revalidate();
            userInputHolderPanel.getPanel().repaint();

        }

        public Review getReview() {
            String comment = commentHolderPanel.reviewTextArea.getText();
            int rating = ratingsComboBox.getSelectedIndex();
            String username = uiMainWindow.getUsername();
            return new Review(username,rating,comment);
        }

    }


}
