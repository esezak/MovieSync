# MovieSync

MovieSync is a Java desktop application for searching movies, managing a personal watchlist, and writing user ratings and comments. It uses a Swing-based client, a socket-based Java server, SQLite for persistence, and TheTVDB API as the movie metadata source.

## Overview

The project is split into three main parts:

- **Client (`project.client`)**: a Swing GUI where users connect to the server, sign up, log in, search for movies, open movie details, manage their watchlist, and submit reviews.
- **Server (`project.server`)**: a threaded socket server that accepts serialized requests from clients, performs authentication and watchlist/review operations, queries TheTVDB, and returns serialized responses.
- **Shared models (`project.common`)**: serializable request/response objects and domain models shared by both client and server.

At a high level, MovieSync works like this:

1. The desktop client connects to a local server over TCP.
2. The client sends a serialized `Request` object.
3. The server handles the request in a per-client `ServerThread`.
4. The server reads/writes SQLite data and, for movie search, queries TheTVDB API.
5. The server sends back a serialized `Response` object.
6. The client updates the UI based on the response.

## Main Features

### User account flow
- Connect and disconnect from the server
- Sign up with a username and password
- Log in and log out
- Session-aware UI state changes

### Movie search
- Search for movies by title using TheTVDB API
- Display returned search results in the center panel
- Cache movie metadata into the local SQLite database

### Movie details and reviews
- Open a movie details view
- Show title, genres, overview, artwork, and average rating
- Load existing user reviews from the server
- Submit a numeric rating from **0 to 10** with an optional comment

### Watchlist management
- Add movies to a personal watchlist
- Remove movies from the watchlist
- View watchlist entries in a table
- Update watchlist status such as:
  - `Watching`
  - `Plan to Watch`
  - `Dropped`
  - `Watched`
- Update stored personal rating directly from the watchlist view

## Technologies Used

### Language and platform
- **Java**
- **Java Swing** for the desktop UI
- **Java sockets** (`Socket`, `ServerSocket`) for client/server communication
- **Java serialization** for sending `Request` and `Response` objects

### Data and storage
- **SQLite** via JDBC
- Local database file: `mydb.db`

### External libraries
The project includes its dependencies under `lib/`:

- **SQLite JDBC** (`sqlite-jdbc-3.47.0.0.jar`)
- **JSON-java / org.json** (`json-20210307.jar`)
- **Apache HttpClient 4.5.13**
- **Apache HttpCore 4.4.13**
- **Apache Commons Logging 1.2**

### External API
- **TheTVDB API v4** for movie lookup and metadata retrieval

## Project Structure

```text
src/
└── project/
    ├── client/
    │   ├── ServerConnection.java
    │   ├── UiMainWindow.java
    │   └── UI/
    │       └── Elements/
    │           ├── FilmButton.java
    │           ├── SimpleButton.java
    │           ├── SimpleLabel.java
    │           ├── SimpleTextField.java
    │           └── Panels/
    │               ├── CenterPanel.java
    │               ├── FilmPanel.java
    │               ├── LeftPanel.java
    │               ├── RightPanel.java
    │               ├── SimplePanel.java
    │               ├── TopPanel.java
    │               └── WatchlistPanel.java
    ├── common/
    │   ├── Movie.java
    │   ├── Request.java
    │   ├── RequestType.java
    │   ├── Response.java
    │   └── Review.java
    └── server/
        ├── DBConnection.java
        ├── ServerThread.java
        └── TVDBSearcher.java
```

## Architecture

### 1. Client layer
The client is launched from:
- `project.client.UiMainWindow.Launcher`

The main window creates and wires together four panels:

- `TopPanel` – search input and search button
- `LeftPanel` – navigation between search results and watchlist
- `CenterPanel` – search results container
- `RightPanel` – connection, login, logout, and signup controls

Important client classes:

- `UiMainWindow` manages the frame and high-level app state such as `isConnected`, `isLoggedIn`, and the current username.
- `ServerConnection` wraps all networking logic and exposes methods like:
  - `sendLoginRequest(...)`
  - `sendSignupRequest(...)`
  - `sendFilmQuery(...)`
  - `getWatchlistRequest(...)`
  - `getFilmInformation(...)`
  - `sendAddToWatchListRequest(...)`
  - `sendUpdateWatchListRequest(...)`
  - `sendDeleteFromWatchListRequest(...)`
  - `sendRateMovieRequest(...)`

### 2. Shared protocol and models
The `project.common` package defines the contract between the client and server.

#### Request/response flow
- `Request` builds the payload for each client action.
- `RequestType` enumerates the supported operations.
- `Response` carries a success flag plus either movie data, review data, or raw JSON string data.

Supported request types:
- `LOGIN`
- `LOGOUT`
- `DISCONNECT`
- `SIGNUP`
- `GET_USER_WATCHLIST`
- `ADD_MOVIE_TO_WATCHLIST`
- `RATE_MOVIE`
- `SEARCH_MOVIE`
- `GET_MOVIE_INFORMATION`
- `UPDATE_WATCHLIST`
- `DELETE_MOVIE_FROM_WATCHLIST`

#### Domain models
- `Movie` stores movie metadata such as id, title, genres, overview, release year, image URL, director, and average rating.
- `Review` stores a username, rating, and comment.

The project mixes **serialized Java objects** and **JSON strings**:
- Java serialization is used for transport between client and server.
- JSON is used inside the request/response payloads and for parsing API/database-oriented data.

### 3. Server layer
The server entry point is:
- `project.server.ServerThread.main`

Server behavior:
- Opens a `ServerSocket` on port `12345`
- Accepts client connections
- Spawns a new `ServerThread` for each connected client
- Processes requests in a loop until disconnect

Core server methods include:
- `handleLoginRequest(...)`
- `handleLogoutRequest()`
- `handleDisconnectRequest()`
- `handleSignupRequest(...)`
- `handleGetWatchlistRequest(...)`
- `handleAddMovieToWatchlistRequest(...)`
- `handleRateMovieRequest(...)`
- `handleSearchMovieRequest(...)`
- `handleGetMovieInformationRequest(...)`
- `handleUpdateWatchlistRequest(...)`
- `handleDeleteMovieFromWatchlistRequest(...)`

The server also includes:
- `checkAuth(...)` to enforce that requests come from the logged-in user
- `setRating(...)` to recalculate and persist average movie ratings after review changes

### 4. Database layer
`DBConnection` is responsible for:
- opening the SQLite connection
- creating the schema
- verifying credentials
- inserting movies
- checking duplicates
- checking whether a movie is already in a watchlist
- reading stored movie ratings

Database URL in code:
- `jdbc:sqlite:mydb.db`

## Database Schema

The database is initialized by `project.server.DBConnection.main` and creates these tables:

### `Users`
- `username` (primary key)
- `password`

### `Movies`
- `movie_id` (primary key)
- `title`
- `release_year`
- `genre`
- `director`
- `overview`
- `image_url`
- `rating`

### `Reviews`
- `username`
- `movie_id`
- `comment`
- `user_rating`
- `review_date`
- composite primary key: `(username, movie_id)`

### `Watchlist`
- `username`
- `movie_id`
- `date_added`
- `user_rating`
- `status`
- composite primary key: `(username, movie_id)`

## Database Triggers and Derived Data

The project uses SQLite triggers to keep data synchronized:

- When a review is inserted, the matching watchlist row gets its `user_rating` updated.
- When a review is updated, the matching watchlist row gets its `user_rating` updated.
- When a watchlist row is updated, a corresponding review row is inserted or updated with a default comment if needed.
- When a watchlist row is deleted, the matching review row is deleted.

Additionally, the server recalculates the movie-wide average rating with `setRating(...)` and stores it back into the `Movies` table.

## UI Layout and Interaction Model

### `RightPanel`
Handles connection and authentication controls:
- Connect to server
- Disconnect from server
- Log in
- Log out
- Sign up
- Show connection and login status

### `TopPanel`
Provides the movie search bar and search button.

### `LeftPanel`
Acts as a simple navigation area:
- `Search Tab`
- `Watchlist`

### `CenterPanel`
Displays search results as a vertical list of movie buttons.

### `FilmPanel`
Displays:
- movie information
- average rating
- existing reviews
- comment/rating form
- add/remove watchlist button

### `WatchlistPanel`
Displays the user watchlist in a table with editable:
- rating
- status

A save button sends the updated watchlist state back to the server.

## Methods and Implementation Approaches

This project uses several important implementation methods and patterns.

### Socket-based request/response communication
The client creates a `Socket` and object streams in `ServerConnection`. Each user action is transformed into a `Request` object, sent to the server, and answered with a `Response` object.

This is a straightforward RPC-style design implemented on top of Java sockets.

### Thread-per-client server model
`ServerThread` extends `Thread`. Each accepted client connection is handled independently in its own thread. That keeps one client session from blocking all others.

### JSON as internal payload format
Even though transport uses Java serialization, most request data is packed as JSON strings. This makes it easier to carry structured fields like:
- `username`
- `password`
- `movie_id`
- `rating`
- `comment`
- watchlist arrays

### JDBC with prepared statements
The server uses prepared statements for most database queries and updates. This is good practice for:
- safer parameter handling
- cleaner query logic
- easier maintenance

### API integration and local caching
`TVDBSearcher.queryFromTVDB(...)` sends HTTP requests to TheTVDB API, parses the JSON response, converts results into `Movie` objects, and stores them in SQLite if they are not already present.

This means the application combines:
- **remote movie discovery** from TheTVDB
- **local persistence** for ratings, reviews, and watchlist state

### Trigger-based synchronization
The database schema uses triggers to keep watchlist and review data in sync. This reduces the amount of manual synchronization logic needed in Java code.

## How to Run

## Prerequisites
- Java JDK installed
- IntelliJ IDEA or another Java IDE recommended
- The JAR files inside `lib/` available on the classpath
- A valid TheTVDB API key and token if you want search to work

## Important configuration before running
Edit `src/project/server/TVDBSearcher.java` and replace the placeholders:

- `YOUR TVDB API KEY`
- `COPY GENERATED TOKEN HERE`

The code includes a helper entry point:
- `project.server.TVDBSearcher.main`

That method calls `getToken()` and can be used to print a token after setting the API key.

## Recommended IntelliJ run order
Because this repository includes an IntelliJ module file (`Se360-Project.iml`) and local `lib/` jars, the easiest way to run it is from IntelliJ.

### 1. Initialize the database
Run:
- `project.server.DBConnection.main`

This creates the SQLite schema in `mydb.db`.

### 2. Start the server
Run:
- `project.server.ServerThread.main`

The server listens on port `12345`.

### 3. Start the client
Run:
- `project.client.UiMainWindow.Launcher.main`

The client connects to:
- host: `localhost`
- port: `12345`

## Typical usage flow
1. Launch the server.
2. Launch the client.
3. Click **Connect**.
4. Sign up or log in.
5. Search for a movie.
6. Open a movie.
7. Add it to the watchlist.
8. Rate it and optionally write a comment.
9. Open the watchlist and update status/rating.

## Build Notes

This repository does **not** currently include a Maven or Gradle build file. From the checked-in files, it appears to be structured primarily as an IntelliJ IDEA Java module with manually managed library JARs under `lib/`.

That means:
- dependency setup is currently IDE/classpath based
- there is no verified one-command build script in the repository
- IntelliJ is the safest documented way to run the project as it exists today

## Known Limitations and Observations

Based on the current source code, here are some important notes for developers:

- **Passwords are stored in plain text** in the `Users` table.
- The TheTVDB API key and bearer token are hard-coded placeholders and must be filled manually.
- The server listens on a fixed port (`12345`).
- The client connects only to `localhost` by default.
- The project uses Java object serialization across the socket, which is simple but tightly couples client and server class definitions.
- Search functionality depends on valid TheTVDB credentials.
- There is no automated test suite in the current repository.
- There is no build automation file such as `pom.xml` or `build.gradle` in the visible workspace.

## Suggested Future Improvements

If you continue developing MovieSync, these would be high-value improvements:

1. Add **Maven or Gradle** build support.
2. Move secrets out of source code into environment variables or config files.
3. Hash passwords with a strong algorithm such as BCrypt.
4. Add unit and integration tests.
5. Improve error reporting in the UI.
6. Add pagination or lazy loading for search results.
7. Replace raw thread handling with an executor service.
8. Introduce a cleaner service/repository structure on the server side.
9. Add configuration for host, port, and database path.
10. Improve offline behavior and caching strategy.

## Key Entry Points

- Client UI launcher: `project.client.UiMainWindow.Launcher`
- Server launcher: `project.server.ServerThread`
- Database initialization: `project.server.DBConnection`
- TVDB token helper: `project.server.TVDBSearcher`

## Summary

MovieSync is a client-server Java Swing application for movie discovery and personal watchlist management. It combines:

- a desktop GUI
- socket communication
- SQLite persistence
- JSON payload handling
- third-party API integration
- rating and review workflows

It’s a solid educational example of a small full-stack Java desktop system where the UI, network protocol, database layer, and external API integration all work together in one project.
