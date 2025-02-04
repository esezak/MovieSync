package project.client;
import project.common.Request;
import project.common.Response;
import project.common.Review;

import java.io.*;
import java.net.*;

public class ServerConnection {
    private String host;
    private int port;
    private Socket connection = null;
//    public ArrayList<Movie> movies;
//    public ArrayList<Review> reviews;
//    public Movie movie;

    public Request currentRequest;
    public Response currentResponse;
    private ObjectOutputStream sendChannel;
    private ObjectInputStream receiveChannel;
    public ServerConnection(String host, int port) {
        this.host = host;
        this.port = port;
        currentRequest = new Request();
    }
    public boolean setConnection(){
        try{
            connection = new Socket(this.host,this.port);
            sendChannel = new ObjectOutputStream(connection.getOutputStream());
            receiveChannel = new ObjectInputStream(connection.getInputStream());

            return true;
        } catch (IOException e) {

            return false;
        }
    }


    /**
     * Sets the current response so that the data from the response can be used from the ui
     * <br> Request types: LOGIN, LOGOUT, DISCONNECT, GET_USER_WATCHLIST, ADD_MOVIE_TO_WATCHLIST
     * @return true if request is successful
     */
    private boolean responseHandler(Request request){
        currentRequest = request;
        currentResponse = null;
        try{
            //send request to server
            sendChannel.writeObject(currentRequest);
            //receive request from server
            currentResponse = (Response) receiveChannel.readObject();
            if(!currentResponse.getStatus()){
                System.err.println("Request failed");
                return false;
            }else{
                System.out.println(currentRequest.getRequestType()+" Successfully Sent");
                return true;
            }
        } catch (IOException e) {
            System.err.println("Could not send "+ currentRequest.getRequestType() +" request");
            return false;
        } catch (ClassNotFoundException e) {
            System.err.println("Received class not found");
            return false;
        }
    }

    /**
     * @param request passes the request to request handler
     * @return returns the current response if the request is sent successfully returns null if request fails
     */
    private Response requestHandler(Request request){
        if(responseHandler(request)){
            return currentResponse;
        }
        return null;
    }
    public Response sendFilmQuery(String movieName){
        currentRequest = new Request();
        currentRequest.search_movie(movieName);
        return requestHandler(currentRequest);
    }
    public Response getWatchlistRequest(String username){
        currentRequest = new Request();
        currentRequest.get_user_watchlist(username);
        return requestHandler(currentRequest);
    }

    /**
     * @param movie_id
     *
     * @return a Response that has the movie reviews
     */
    public Response getFilmInformation(String movie_id){ // returns the response filled with reviews
        currentRequest = new Request();
        currentRequest.get_movie_information(movie_id);
        return requestHandler(currentRequest);
    }
    public boolean sendRateMovieRequest(String movie_id,Review review){
        currentRequest = new Request();
        currentRequest.rate_movie(movie_id,review);
        return responseHandler(currentRequest);
    }

    public boolean sendAddToWatchListRequest(String movie_id, String username){
        currentRequest = new Request();
        currentRequest.add_movie_to_watchlist(movie_id, username);
        return responseHandler(currentRequest);
    }
    public boolean sendDisconnectRequest(){
        currentRequest = new Request();
        currentRequest.disconnect();
        return responseHandler(currentRequest);
    }
    public boolean sendLogoutRequest(){
        currentRequest = new Request();
        currentRequest.logout();
        return responseHandler(currentRequest);
    }
    public boolean sendLoginRequest(String username, String password){
        currentRequest = new Request();
        currentRequest.login(username,password);
        return responseHandler(currentRequest);
    }
    public boolean sendUpdateWatchListRequest(String data){
        currentRequest = new Request();
        currentRequest.update_watchlist(data);
        return responseHandler(currentRequest);
    }
    public boolean sendDeleteFromWatchListRequest(String movie_id, String username){
        currentRequest = new Request();
        currentRequest.delete_from_watchlist(movie_id,username);
        return responseHandler(currentRequest);
    }
    public boolean sendSignupRequest(String username, String password){
        currentRequest = new Request();
        currentRequest.sign_up(username,password);
        return responseHandler(currentRequest);
    }

}
