package project.common;

public enum RequestType {
    //Requests
        //User Requests
            LOGIN,
            //Send -> Username + Password + Request type
            // Response -> ok | fail
            LOGOUT,
            //Send -> Request Type
            // Response -> ok | fail
            DISCONNECT,
            //Send -> Request Type
            // Response -> ok | fail
            SIGNUP,
            // Send -> username + password + Request type
            // Response -> ok | fail
            GET_USER_WATCHLIST,
            //Send -> Request Type + username
            // Response -> ArrayList<Movie>, ok | fail if not logged in
            ADD_MOVIE_TO_WATCHLIST,
            //Send -> Request Type + "MovieID"
            //Response -> ok | fail if not logged in
            RATE_MOVIE,
            //Send -> Request Type + "MovieID" + Review
            //Response -> ok | fail if not logged in
        SEARCH_MOVIE,
        //Send -> "Movie name"
        // Response -> ArrayList<Movie>, ok | fail
        GET_MOVIE_INFORMATION,
        //Send -> "MovieID"
        // Response -> Movie + Json with reviews, ok | fail
        UPDATE_WATCHLIST,
        //Send -> "username" + watchlistJson
        //response -> ok | fail
        DELETE_MOVIE_FROM_WATCHLIST,
        //Send -> "username" + movieID
        //response -> ok |fail

}