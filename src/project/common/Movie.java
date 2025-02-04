package project.common;

import org.json.JSONObject;
import java.io.Serializable;

public class Movie implements Serializable {
    private final String id;
    private final String title;
    private final String release_date;
    private final String genres;
    private final String director;
    private String overview;
    private final String image_url;
    private double avg_rating;

    public Movie(String id, String title, String image_url, String overview, String release_date, String genres, String director) {
        this.id = id;
        this.title = title;
        this.image_url = image_url;
        this.overview = overview;
        this.release_date = release_date;
        this.genres = genres;
        this.director = director;
        this.avg_rating = 0;

    }
    public Movie(JSONObject json) {
        this.id = json.getString("id");
        this.title = json.getString("title");
        this.release_date = json.getString("release_date");
        this.genres = json.getString("genres");
        this.director = json.getString("director");
        this.overview = json.getString("overview");
        this.image_url = json.getString("image_url");
        this.avg_rating = json.getFloat("avg_rating");
    }
    public String getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getRelease_date() {
        return release_date;
    }
    public String getGenres() {
        return genres;
    }
    public String getDirector() {
        return director;
    }
    public String getOverview() {
        return overview;
    }
    public String getImage_url() {
        return image_url;
    }
    public double getAvg_rating() {
        return avg_rating;
    }
    public void setOverview(String overview) {
        this.overview = overview;
    }
    public static JSONObject toJsonObject(Movie movie) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", movie.getId());
        jsonObject.put("title", movie.getTitle());
        jsonObject.put("release_date", movie.getRelease_date());
        jsonObject.put("genres", movie.getGenres());
        jsonObject.put("director", movie.getDirector());
        jsonObject.put("overview", movie.getOverview());
        jsonObject.put("image_url", movie.getImage_url());
        jsonObject.put("avg_rating", movie.getAvg_rating());
        return jsonObject;
    }
    public String toJsonString() {
        return toJsonObject(this).toString();
    }
    //These were not used
//    public static ArrayList<Movie> fromJsonArray(String data) {
//        JSONArray jsonArray = new JSONArray(data);
//        ArrayList<Movie> movies = new ArrayList<>();
//        for (int i = 0; i < jsonArray.length(); i++) {
//            movies.add(new Movie(jsonArray.getJSONObject(i)));
//        }
//        return movies;
//    }
//    public static String toJsonArray(ArrayList<Movie> movies) {
//        JSONArray jsonArray = new JSONArray();
//        for (int i = 0; i < movies.size(); i++) {
//            jsonArray.put(i,toJsonObject(movies.get(i)));
//        }
//        return jsonArray.toString();
//    }
    public void setAvg_rating(double avg_rating) {
        this.avg_rating = avg_rating;
    }
}
