package project.server;

import project.common.Movie;

import java.sql.*;

public class DBConnection {
    private Connection dbConnection;
    private String url = "jdbc:sqlite:mydb.db";

    public DBConnection() {
        try {
            dbConnection = DriverManager.getConnection(url);
            System.out.println("Connection Successful");
        } catch (SQLException e) {
            System.out.println("Error Connecting to Database");
            e.printStackTrace();
        }
    }

    public Connection getDbConnection() {
        return dbConnection;
    }

    public void closeConnection() {
        try {
            if (dbConnection != null) {
                System.out.println("Connection Closed");
                dbConnection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addToDatabase(Movie movie) {
        String query = "INSERT INTO Movies (movie_id, title, release_year, genre, director, overview, image_url, rating) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            if (dbConnection != null && !isDuplicate(movie)) {
                try (PreparedStatement statement = dbConnection.prepareStatement(query)) {
                    statement.setString(1, movie.getId());
                    statement.setString(2, movie.getTitle());
                    statement.setString(3, movie.getRelease_date());
                    statement.setString(4, movie.getGenres());
                    statement.setString(5, movie.getDirector());
                    statement.setString(6, movie.getOverview());
                    statement.setString(7, movie.getImage_url());

                    double rating = movie.getAvg_rating();
                    if (rating >= 0) {
                        statement.setDouble(8, rating);
                    } else {
                        statement.setNull(8, java.sql.Types.REAL);
                    }

                    statement.executeUpdate();
                    System.out.println("Movie added to database: " + movie.getTitle());
                }
            } else {
                System.out.println("Duplicate entry: " + movie.getTitle());
            }
        } catch (SQLException e) {
            System.err.println("Database Error: " + e.getMessage());
        }/* finally {
            closeConnection();
        }*/
    }

    public boolean isDuplicate(Movie movie) throws SQLException {
        String checkQuery = "SELECT 1 FROM Movies WHERE movie_id = ?";
        try (PreparedStatement pstmt = dbConnection.prepareStatement(checkQuery)) {
            pstmt.setString(1, movie.getId());
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }
    public Double getMovieRating(String movie_id){
        String query = "SELECT rating FROM Movies WHERE movie_id = ?";
        try (PreparedStatement pstmt = dbConnection.prepareStatement(query)) {
            pstmt.setString(1, movie_id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("rating");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return -1.0;
    }

    public boolean verifyPassword(String username, String password) {
        String query = "SELECT password FROM Users WHERE username = ?";
        try (PreparedStatement pstmt = dbConnection.prepareStatement(query)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("password");
                    return password.equals(storedPassword);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error verifying credentials: " + e.getMessage());
        }
        return false;
    }

    public boolean isMovieInWatchlist(String username, String movieId) throws SQLException {
        String query = "SELECT 1 FROM Watchlist WHERE username = ? AND movie_id = ?";
        try (PreparedStatement pstmt = dbConnection.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, movieId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }
    private void dbInit(){
        if (dbConnection != null) {
            try {
                Statement statement = dbConnection.createStatement();
                String createUsersTable = """
            CREATE TABLE IF NOT EXISTS Users (
                username TEXT PRIMARY KEY,
                password TEXT NOT NULL
            );
        """;

                String createMoviesTable = """
            CREATE TABLE IF NOT EXISTS Movies (
                movie_id TEXT PRIMARY KEY,
                title TEXT NOT NULL,
                release_year TEXT NOT NULL,
                genre TEXT,
                director TEXT,
                overview TEXT,
                image_url TEXT,
                rating REAL
            );
        """;

                String createReviewsTable = """
            CREATE TABLE IF NOT EXISTS Reviews (
                username TEXT,
                movie_id TEXT,
                comment TEXT,
                user_rating REAL,
                review_date TEXT,
                PRIMARY KEY (username, movie_id),
                FOREIGN KEY (username) REFERENCES Users(username),
                FOREIGN KEY (movie_id) REFERENCES Movies(movie_id)
            );
        """;

                String createWatchlistTable = """
            CREATE TABLE IF NOT EXISTS Watchlist (
                username TEXT,
                movie_id INTEGER,
                date_added TEXT,
                user_rating REAL,
                status TEXT,
                PRIMARY KEY (username, movie_id),
                FOREIGN KEY (username) REFERENCES Users(username),
                FOREIGN KEY (movie_id) REFERENCES Movies(movie_id)
            );
        """;
                String createTriggerWatchlistOnReview1 = """
                DROP TRIGGER IF EXISTS UpdateWatchlistOnReviewChange1;
                CREATE TRIGGER IF NOT EXISTS UpdateWatchlistOnReviewChange1
                AFTER INSERT ON Reviews
                FOR EACH ROW
                BEGIN
                    UPDATE Watchlist
                    SET user_rating = NEW.user_rating
                    WHERE username = NEW.username AND movie_id = NEW.movie_id;
                END;
            """;
                String createTriggerWatchlistOnReview2 = """
                DROP TRIGGER IF EXISTS UpdateWatchlistOnReviewChange2;
                CREATE TRIGGER IF NOT EXISTS UpdateWatchlistOnReviewChange2
                AFTER UPDATE ON Reviews
                FOR EACH ROW
                BEGIN
                    UPDATE Watchlist
                    SET user_rating = NEW.user_rating
                    WHERE username = NEW.username AND movie_id = NEW.movie_id;
                END;
            """;
//                String createTriggerReviewOnWatchlist1 = """
//                CREATE TRIGGER IF NOT EXISTS UpdateReviewOnWatchlistChange1
//                AFTER INSERT ON Watchlist
//                FOR EACH ROW
//                BEGIN
//                    INSERT INTO Reviews (username, movie_id, user_rating, review_date, comment)
//                    VALUES (NEW.username, NEW.movie_id, NEW.user_rating, DATE('now'), 'From Watchlist')
//                    ON CONFLICT (username, movie_id)
//                    DO UPDATE SET user_rating = NEW.user_rating;
//                END; DROP TRIGGER UpdateReviewOnWatchlistChange1;
//            """;
                String createTriggerReviewOnWatchlist2 = """
                DROP TRIGGER IF EXISTS UpdateReviewOnWatchlistChange2;
                CREATE TRIGGER IF NOT EXISTS UpdateReviewOnWatchlistChange2
                AFTER UPDATE ON Watchlist
                FOR EACH ROW
                BEGIN
                    INSERT INTO Reviews (username, movie_id, user_rating, review_date, comment)
                    VALUES (NEW.username, NEW.movie_id, NEW.user_rating, DATE('now'), 'Not Commented')
                    ON CONFLICT (username, movie_id)
                    DO UPDATE SET user_rating = NEW.user_rating;
                END;
            """;
                String createTriggerDeleteFromReviews = """
                DROP TRIGGER IF EXISTS DeleteFromReviews;
                CREATE TRIGGER IF NOT EXISTS DeleteFromReviews
                AFTER DELETE ON Watchlist
                FOR EACH ROW
                BEGIN
                    DELETE FROM Reviews
                    WHERE movie_id = OLD.movie_id AND username = OLD.username;
                END;
            """;

                statement.executeUpdate(createUsersTable);
                statement.executeUpdate(createMoviesTable);
                statement.executeUpdate(createReviewsTable);
                statement.executeUpdate(createWatchlistTable);
                statement.executeUpdate(createTriggerWatchlistOnReview1);
                statement.executeUpdate(createTriggerWatchlistOnReview2);
                //statement.executeUpdate(createTriggerReviewOnWatchlist1);
                statement.executeUpdate(createTriggerReviewOnWatchlist2);
                statement.executeUpdate(createTriggerDeleteFromReviews);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Connection failed.");
        }
    }
    public static void main(String[] args) {
        DBConnection dbc = new DBConnection();
        try{
            dbc.dbInit();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
