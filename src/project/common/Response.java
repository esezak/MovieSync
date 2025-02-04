package project.common;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class Response implements Serializable {
    private boolean status;
    private ArrayList<Movie> movies = null;
    private transient JSONArray jsonArray;
    private String data;
    private transient Movie movie = null;
    public Response(boolean status) {
        this.status = status;
    }

    public Response(boolean status, ArrayList<Review> reviews, boolean withMovies) {
        this.status = status;
        setReviews(reviews,withMovies);
    }
    public Response(boolean status, String data) {
        this.status = status;
        this.data = data;
    }
    public Response(boolean status, ArrayList<Movie> movies) {
        this.status = status;
        this.movies = movies;
    }
    public boolean getStatus() {
        return status;
    }
    public ArrayList<Movie> getMovies() {
        return movies;
    }
    public ArrayList<Review> getReviews() {
        return Review.fromJsonArray(data);

    }
    public Movie getMovie() {
        return movie;
    }
    public void setReviews(ArrayList<Review> reviews,boolean withMovie) {
        if(!withMovie){
            jsonArray = new JSONArray();
        }
        JSONObject row;
        for (Review review : reviews) {
            row = new JSONObject();
            row.put("username", review.getUsername());
            row.put("rating", review.getRating());
            row.put("comment", review.getComment());
            jsonArray.put(row);
        }
        this.data = jsonArray.toString();
        System.out.println(data);
    }
    public String getData() {
        return data;
    }
}
