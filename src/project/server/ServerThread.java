package project.server;

import project.common.Request;
import project.common.RequestType;
import org.json.JSONArray;
import org.json.JSONObject;
import project.common.Movie;
import project.common.Response;
import project.common.Review;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.ArrayList;


public class ServerThread extends Thread {
    private final Socket connection;
    private ObjectOutputStream sendChannel;
    private DBConnection dbConnection;
    private Response currentResponse;
    private boolean loggedIn = false;
    private boolean terminate = false;
    private String username;
    private String password;
    public ServerThread(Socket connection) {
        this.connection = connection;
    }

    public void run() {
        try{
            sendChannel = new ObjectOutputStream(connection.getOutputStream());
            ObjectInputStream receiveChannel = new ObjectInputStream(connection.getInputStream());
            dbConnection = new DBConnection();
            while (!terminate) {//accept requests until disconnect
                Request currentRequest = (Request) receiveChannel.readObject();
                if(handleRequest(currentRequest)){
                    System.out.println("Request handled");
                }else{
                    System.err.println("Request not handled");
                }
            }
            } catch (IOException e) {
                System.err.println("IOException in ServerThread");
                terminate = true;
            } catch (ClassNotFoundException e) {
                System.err.println("Could not receive request");
                terminate = true;
            }
    }

    /**
     * @param request taken from client
     * @return <code>true</code> if request is handled properly otherwise returns <code>false</code>
     */
    private boolean handleRequest(Request request) throws IOException {
        return switch (request.getRequestType()) {
            case RequestType.LOGIN -> handleLoginRequest(request);
            case RequestType.LOGOUT -> handleLogoutRequest();
            case RequestType.DISCONNECT -> handleDisconnectRequest();
            case RequestType.GET_USER_WATCHLIST -> handleGetWatchlistRequest(request);
            case RequestType.ADD_MOVIE_TO_WATCHLIST -> handleAddMovieToWatchlistRequest(request);
            case RequestType.RATE_MOVIE -> handleRateMovieRequest(request);
            case RequestType.SEARCH_MOVIE -> handleSearchMovieRequest(request);
            case RequestType.GET_MOVIE_INFORMATION -> handleGetMovieInformationRequest(request);
            case RequestType.UPDATE_WATCHLIST -> handleUpdateWatchlistRequest(request);
            case RequestType.DELETE_MOVIE_FROM_WATCHLIST -> handleDeleteMovieFromWatchlistRequest(request);
            case RequestType.SIGNUP -> handleSignupRequest(request);
            default -> false;
        };
    }


    /**
     * @param request taken from client
     * @return <code>true</code> if login is successful
     */
    private boolean handleLoginRequest(Request request) throws IOException {
        JSONObject loginData = new JSONObject(request.getData());   // parses json data
        this.password = loginData.getString("password");     // password field
        this.username = loginData.getString("username");     // username field
        try{
            if (connection != null && dbConnection.verifyPassword(username, password)) {
                if (!loggedIn) {
                    loggedIn = true;
                    sendOkResponse();
                    System.out.println(username +" logged in");
                    return true;
                } else {
                    System.err.println(username +" already logged in!");
                    sendErrorResponse();
                    return false;
                }
            } else {
                System.err.println("Invalid username or password for: "+username);
                sendErrorResponse();
                return false;
            }
        } catch (Exception e) {
            System.err.println("Database Error: " + e.getMessage());
            sendErrorResponse();
            return false;
        }
    }

    /**
     * @return <code>true</code> if logout request is successful
     */
    private boolean handleLogoutRequest() throws IOException {
        if (loggedIn) {
            loggedIn = false;
            this.password = null;
            this.username = null;
            System.out.println("Client Logged out");
            sendOkResponse();
            return true;
        }else{
            System.err.println("Client not logged in");
            sendErrorResponse();
            return false;
        }
    }

    /**
     * @return <code>true</code> if disconnect request is successful
     */
    private boolean handleDisconnectRequest() throws IOException {
        System.out.println("Client Disconnected");
        terminate = true;
        sendOkResponse();
        connection.close();
        return true;
    }

    private boolean handleSignupRequest(Request request) throws IOException {
        JSONObject requestData = new JSONObject(request.getData());
        String username = requestData.getString("username");
        String password = requestData.getString("password");
        String query1 = "SELECT COUNT(*) FROM Users WHERE username = ?";
        String query2 = "INSERT INTO Users (username, password) VALUES (?, ?)";
        try {
            if (dbConnection != null) {
                try (PreparedStatement pstmt = dbConnection.getDbConnection().prepareStatement(query1)) {
                    pstmt.setString(1, username);
                    ResultSet rs = pstmt.executeQuery();
                    if (rs.getInt(1) > 0) {
                        System.err.println("Username already exists");
                        sendErrorResponse();
                        return false;
                    }
                }
                try (PreparedStatement pstmt = dbConnection.getDbConnection().prepareStatement(query2)) {
                    pstmt.setString(1, username);
                    pstmt.setString(2, password);
                    pstmt.executeUpdate();
                    System.out.println("User signed up");
                }
            }
        } catch (SQLException e) {
            System.err.println("Database Error: " + e.getMessage());
            sendErrorResponse();
            return false;
        }
        sendOkResponse();
        return true;
    }

    /**
     * @param request taken from client
     * @return checks if the user is logged in and creates a response that has the user watchlist as a <code>json</code>  and sets the current response status <code>true</code> if everything is successful
     */
    private boolean handleGetWatchlistRequest(Request request) throws IOException {
        JSONObject requestData = new JSONObject(request.getData());
        String username = requestData.getString("username");
        if (!checkAuth(username)) {
            sendErrorResponse();
            return false;
        }
        String query = """
        SELECT m.movie_id, m.title, w.date_added, w.user_rating, w.status
        FROM Watchlist w
        JOIN Movies m ON w.movie_id = m.movie_id
        WHERE w.username = ?
    """;
        JSONArray watchlistJson = new JSONArray();
        try {
            if (dbConnection != null) {
                try (PreparedStatement pstmt = dbConnection.getDbConnection().prepareStatement(query)) {
                    pstmt.setString(1, username);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        while (rs.next()) {
                            JSONObject movieJson = new JSONObject();
                            movieJson.put("movie_id", rs.getString("movie_id"));
                            movieJson.put("title", rs.getString("title"));
                            movieJson.put("date_added", rs.getString("date_added"));
                            movieJson.put("user_rating", rs.getDouble("user_rating"));
                            movieJson.put("status", rs.getString("status"));
                            watchlistJson.put(movieJson);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Database Error: " + e.getMessage());
            sendErrorResponse();
            return false;
        }
        currentResponse = new Response(true,watchlistJson.toString());
        sendResponse();
        System.out.println("Watchlist returned");
        return true;
    }

    /**
     * checks if the user is logged in and if the movie request is already in the users watchlist.
     * if the user is logged in and the movie is not in the watchlist add the movie to user watchlist
     * @param request taken from client
     * @return true if the movie is added successfully
     */
    private boolean handleAddMovieToWatchlistRequest(Request request) throws IOException {
        JSONObject requestData = new JSONObject(request.getData());
        String movieId = requestData.getString("movie_id");
        String username = requestData.getString("username");
        if(!checkAuth(username)){
            sendErrorResponse();
            return false;
        }
        String query = "INSERT INTO Watchlist (username, movie_id, date_added, user_rating, status) "
                + "VALUES (?, ?, datetime('now'), NULL, 'Plan to Watch')";
        try{
            if (dbConnection != null) {
                if (dbConnection.isMovieInWatchlist(username, movieId)) {
                    System.err.println("Movie is already in the watchlist");
                    sendErrorResponse();
                    return false;
                }
                try (PreparedStatement pstmt = dbConnection.getDbConnection().prepareStatement(query)) {
                    pstmt.setString(1, username);
                    pstmt.setString(2, movieId);
                    pstmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.err.println("Database Error: " + e.getMessage());
            sendErrorResponse();
            return false;
        }
        sendOkResponse();
        System.out.println("Movie added to watchlist");
        return true;
    }
    /**
     * @param request taken from client
     * @return <code>true</code> if rate movie request is successfully completed else false
     */
    private boolean handleRateMovieRequest(Request request) throws IOException {
        JSONObject reviewData = new JSONObject(request.getData());
        Review review = Review.parseJson(reviewData);//Automatically parses json to review object
        String username = reviewData.getString("username");
        String movieId = reviewData.getString("movie_id");

        if(!checkAuth(username)){
            sendErrorResponse();
            return false;
        }
        String comment = review.getComment();
        int rating = review.getRating();
        if (rating < 0 || rating > 10) {
            System.err.println("Invalid rating value for: "+rating);
            sendErrorResponse();
            return false;
        }
        String query = """
        INSERT INTO Reviews (username, movie_id, comment, user_rating, review_date)
        VALUES (?, ?, ?, ?, datetime('now'))
        ON CONFLICT(username, movie_id)
        DO UPDATE SET\s
            comment = excluded.comment,\s
            user_rating = excluded.user_rating,\s
            review_date = excluded.review_date
   \s""";
        try {
            if (dbConnection != null) {
                try (PreparedStatement pstmt = dbConnection.getDbConnection().prepareStatement(query)) {
                    pstmt.setString(1, username);
                    pstmt.setString(2, movieId);
                    pstmt.setString(3, comment);
                    pstmt.setDouble(4, rating);
                    pstmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.err.println("Database Error: " + e.getMessage());
            sendErrorResponse();
            return false;
        }
        sendOkResponse();
        System.out.println("Movie rated");
        setRating(movieId);
        return true;
    }

    /**
     * @param request taken from client
     * @return true if fetching movies is successful
     */
    private boolean handleSearchMovieRequest(Request request) throws IOException {
        JSONObject requestData = new JSONObject(request.getData());
        String movieName = requestData.getString("movie_name");
        try{
            ArrayList<Movie> moviesFromTVDB = TVDBSearcher.queryFromTVDB(movieName);
            for(Movie movie : moviesFromTVDB){
                movie.setAvg_rating(dbConnection.getMovieRating(movie.getId()));
            }
            currentResponse = new Response(true,moviesFromTVDB);
            sendChannel.writeObject(currentResponse);
            return true;
        } catch (IOException e) {
            System.err.println("Could not search movie " + movieName);
            sendErrorResponse();
            return false;
        }
    }

    /**
     * @param request taken from client
     * @return <code>true</code> if getting reviews of movies from the db is successful
     */
    private boolean handleGetMovieInformationRequest(Request request) throws IOException {
        JSONObject json = new JSONObject(request.getData());
        String movieId = json.getString("movie_id");
        String query = """
        SELECT username, comment, user_rating, review_date
        FROM Reviews
        WHERE movie_id = ?
    """;
        ArrayList<Review> reviews = new ArrayList<>();
        try {
            if (dbConnection != null) {
                try (PreparedStatement pstmt = dbConnection.getDbConnection().prepareStatement(query)) {
                    pstmt.setString(1, movieId);
                    try (ResultSet rs = pstmt.executeQuery();) {
                        while (rs.next()) {
                            String username = rs.getString("username");
                            int rating = rs.getInt("user_rating");
                            String comment = rs.getString("comment");
                            System.out.println("Added: "+username + " " + rating + " " + comment);
                            reviews.add(new Review(username, rating, comment));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Database Error: " + e.getMessage());
            sendErrorResponse();
            return false;
        }
        currentResponse = new Response(true,reviews,false);
        sendResponse();
        return true;
    }

    /**
     * @param request taken from client
     * @return <code>true</code> if watchlist rows taken from client is updated in the db successfully
     */
    private boolean handleUpdateWatchlistRequest(Request request) throws IOException {
        JSONArray requestData = new JSONArray(request.getData());
        String username = requestData.getJSONObject(0).getString("username");
        if (!checkAuth(username)) {
            sendErrorResponse();
            return false;
        }
        String query = """
        INSERT INTO Watchlist (username, movie_id, date_added, user_rating, status)
        VALUES (?, ?, ?, ?, ?)
        ON CONFLICT (username, movie_id)
        DO UPDATE SET\s
            date_added = excluded.date_added,\s
            user_rating = excluded.user_rating,\s
            status = excluded.status
   \s""";
        try {
            if (dbConnection != null) {
                for (int i = 1; i < requestData.length(); i++) {
                    JSONObject movieData = requestData.getJSONObject(i);
                    String movieId = movieData.getString("movie_id");
                    String dateAdded = movieData.getString("date_added");
                    int userRating = movieData.getInt("user_rating");
                    String status = movieData.getString("status");
                    try (PreparedStatement pstmt = dbConnection.getDbConnection().prepareStatement(query)) {
                        pstmt.setString(1, username);
                        pstmt.setString(2, movieId);
                        pstmt.setString(3, dateAdded);
                        pstmt.setInt(4, userRating);
                        pstmt.setString(5, status);
                        pstmt.executeUpdate();
                        setRating(movieId);
                        System.out.println("Added: "+movieId+" rating: "+userRating+" status: "+status);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Database Error: " + e.getMessage());
            sendErrorResponse();
            return false;
        }
        System.out.println("Updated watchlist");
        sendOkResponse();
        return true;
    }

    /**
     * @param request taken from client
     * @return <code>true</code> if <code>deleteMovie</code> is successful
     */
    private boolean handleDeleteMovieFromWatchlistRequest(Request request) throws IOException {
        JSONObject requestData = new JSONObject(request.getData());
        String username = requestData.getString("username");
        String movieId = requestData.getString("movie_id");
        if (!checkAuth(username)) {
            sendErrorResponse();
            return false;
        }
        String query = "DELETE FROM Watchlist WHERE username = ? AND movie_id = ?";
        try {
            if (dbConnection != null) {
                try (PreparedStatement pstmt = dbConnection.getDbConnection().prepareStatement(query)) {
                    pstmt.setString(1, username);
                    pstmt.setString(2, movieId);
                    int rows = pstmt.executeUpdate();
                    if (rows > 0) {
                        sendOkResponse();
                        System.out.println("Movie deleted from watchlist");
                        setRating(movieId);
                        return true;
                    } else {
                        System.err.println("Movie not found");
                        sendErrorResponse();
                        return false;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Database Error: " + e.getMessage());
            sendErrorResponse();
            return false;
        }
        return false;
    }

    /**
     * @param movieId gets the moviId
     * calculates the avg rating from the reviews from the database
     */
    private void setRating(String movieId){
        String query = "SELECT AVG(user_rating) AS average_rating FROM Reviews WHERE movie_id = ?";
        String query2 = "UPDATE Movies SET rating = ? WHERE movie_id = ?";
        double averageRating = 0;
        try (PreparedStatement pstmt = dbConnection.getDbConnection().prepareStatement(query)) {
            pstmt.setString(1, movieId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    averageRating = rs.getDouble("average_rating");
                    System.out.println("Avg rating for "+movieId+": "+averageRating);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database Error: " + e.getMessage());
        }
        try(PreparedStatement pstmt = dbConnection.getDbConnection().prepareStatement(query2)) {
            pstmt.setDouble(1, averageRating);
            pstmt.setString(2, movieId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Used to send ok response if everything is in order
     */
    private void sendOkResponse() throws IOException {
        System.out.println("Sent ok response");
        currentResponse = new Response(true);
        sendChannel.writeObject(currentResponse);
    }

    /**
     * Used to send error response if something is not right
     * @throws IOException
     */
    private void sendErrorResponse() throws IOException {
        System.err.println("Sent error response");
        currentResponse = new Response(false);
        sendChannel.writeObject(currentResponse);
    }

    /**
     * Sends the current response directly
     * this is for sending prepared responses.
     * should not be used with error/ok responses.
     */
    private void sendResponse() throws IOException {
        System.out.println("Sent response");
        sendChannel.writeObject(currentResponse);
    }

    /**
     * @param username of the logged-in user
     * @return false if logged-in username == request maker username and user is logged in else true
     * @throws IOException
     */
    private boolean checkAuth(String username) throws IOException {
        if (!loggedIn) {
            System.err.println("Not logged in");
            sendErrorResponse();
            return false;
        }
        if(!username.equals(this.username)){
            System.err.println("User logged in as: "+this.username+" but tried to add movie to watchlist as: "+username);
            return false;
        }
        return true;
    }
    public static void main(String[] args) {
        int port = 12345;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");

                new ServerThread(socket).start();//Start new Thread
            }
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
        }
    }
}
