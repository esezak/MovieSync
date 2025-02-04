package project.common;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class Review implements Serializable {
    private String username;
    private int rating;// out of 0 - 10
    private String comment;
    public Review(String username, int rating, String comment) {
        this.username = username;
        this.rating = rating;
        this.comment = comment;
    }
    public Review(String username, int rating) {
        this.username = username;
        this.rating = rating;
        this.comment = "This user has not commented";
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUsername() {
        return username;
    }
    public int getRating() {
        return rating;
    }
    public String getComment() {
        return comment;
    }
    public static Review parseJson(JSONObject json) {
        if(json.has("comment")) {
            return new Review(json.getString("username"), json.getInt("rating"), json.getString("comment"));
        }
        else{
           return new Review(json.getString("username"), json.getInt("rating"));
        }
    }
    public JSONObject toJson() {
        return toJson(this);
    }
    public static JSONObject toJson(Review review) {
        JSONObject json = new JSONObject();
        json.put("username", review.getUsername());
        json.put("rating", review.getRating());
        json.put("comment", review.getComment());
        return json;
    }
    public static ArrayList<Review> fromJsonArray(String json) {
        ArrayList<Review> reviews = new ArrayList<Review>();
        JSONArray jsonArray = new JSONArray(json);
        for (int i = 0; i < jsonArray.length(); i++) {
            reviews.add(Review.parseJson(jsonArray.getJSONObject(i)));
        }
        return reviews;
    }
}
