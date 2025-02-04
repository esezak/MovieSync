package project.common;

import org.json.JSONObject;

import java.io.Serializable;

public class Request implements Serializable {
    private RequestType requestType;
    private transient JSONObject json;
    private String data;

    /**
     * @param username
     * @param Password
     * prepares a login request
     */
    public void login(String username, String Password){
        json = new JSONObject();
        requestType = RequestType.LOGIN;
        json.put("username", username);
        json.put("password", Password);
        data = json.toString();
    }

    /**
     * prepares a logout request
     */
    public void logout(){
        requestType = RequestType.LOGOUT;
        json = null;
        data = null;
    }

    /**
     * prepares a disconnect request
     */
    public void disconnect(){
        requestType = RequestType.DISCONNECT;
        json = null;
        data = null;
    }

    /**
     * @param username
     * prepares a request that requests the user watchlist
     */
    public void get_user_watchlist(String username){
        requestType = RequestType.GET_USER_WATCHLIST;
        json = new JSONObject();
        json.put("username", username);
        data = json.toString();
    }

    /**
     * @param movie_id
     * @param username
     * prepares a request to add a movie to a specified users watchlist
     */
    public void add_movie_to_watchlist(String movie_id, String username){
        json = new JSONObject();
        requestType = RequestType.ADD_MOVIE_TO_WATCHLIST;
        json.put("username", username);
        json.put("movie_id", movie_id);
        data = json.toString();
    }

    /**
     * @param movie_id
     * @param review
     * prepares a request to rate a movie
     */
    public void rate_movie(String movie_id, Review review){
        json = new JSONObject();
        requestType = RequestType.RATE_MOVIE;
        json.put("movie_id", movie_id);
        json.put("username", review.getUsername());
        json.put("rating", review.getRating());
        json.put("comment", review.getComment());
        data = json.toString();
    }

    /**
     * @param movie_name
     * prepares a request to search for a movie in tvdb
     */
    public void search_movie(String movie_name){
        json = new JSONObject();
        requestType = RequestType.SEARCH_MOVIE;
        json.put("movie_name", movie_name);
        data = json.toString();
    }

    /**
     * @param movie_id
     * prepares a request that gets movie information from the server
     */
    public void get_movie_information(String movie_id){
        json = new JSONObject();
        requestType = RequestType.GET_MOVIE_INFORMATION;
        json.put("movie_id", movie_id);
        data = json.toString();
    }

    /**
     * @param movie_data
     * prepares a request that updates the user watchlist
     */
    public void update_watchlist(String movie_data){
        requestType = RequestType.UPDATE_WATCHLIST;
        data = movie_data;
    }

    /**
     * @param movie_id
     * @param username
     * prepares a request to delete a specific movie from the user watchlist
     */
    public void delete_from_watchlist(String movie_id, String username){
        requestType = RequestType.DELETE_MOVIE_FROM_WATCHLIST;
        json = new JSONObject();
        json.put("movie_id", movie_id);
        json.put("username", username);
        data = json.toString();
    }

    /**
     * @param username
     * @param password
     * prepares a request to signup at the server database
     */
    public void sign_up(String username, String password){
        requestType = RequestType.SIGNUP;
        json = new JSONObject();
        json.put("username", username);
        json.put("password", password);
        data = json.toString();
    }
    public RequestType getRequestType() {
        return requestType;
    }
    public String getData() {
        return data;
    }
}
