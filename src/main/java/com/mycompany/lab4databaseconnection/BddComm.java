package com.mycompany.lab4databaseconnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Handles all communication with the SQLite database (table Player).
 */
//class mas2oul 3n connection with database, fe b2lbo updateScore , getScore , connect , disconnect , authenticate. 
public class BddComm {
   //datbase url here used sqlite.
    private final String url;
    private Connection connection;
    //authentication by default -1 if other than -1 then authenticated ana 3m syv fiya l id tb3 user w aked mesh best practice law project w full stack w bado yetl3 pprduction fe encryption w token , w cookies. 
    private int authenticatedId = -1;
    //default constructor connect to the sqlite 
    public BddComm() {
        this("jdbc:sqlite:players.db");
    }
    //parametrized constructor for a specific url 
    public BddComm(String url) {
        this.url = url;
    }

    /** Opens the connection and creates/seeds the Player table if needed. */
    public void connect() throws SQLException {
        //if connection already opened return 
        if (connection != null && !connection.isClosed()) {
            return;
        }
        //if not opened connect using drivemanager
        connection = DriverManager.getConnection(url);
        try (Statement st = connection.createStatement()) {
            //create table if it dont exists with columns.
            st.execute("CREATE TABLE IF NOT EXISTS Player ("
                    + "IdPlayer INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "playerName TEXT NOT NULL UNIQUE, "
                    + "PlayerPassword TEXT NOT NULL, "
                    + "score INTEGER NOT NULL DEFAULT 0)");
            //execute the query and test if select count was 0 so n3ml insert la 3 players. 
            try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM Player")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    st.execute("INSERT INTO Player(playerName, PlayerPassword, score) VALUES "
                            + "('alice', '1234', 10), ('bob', 'abcd', 25), ('carol', 'pass', 40)");
                }
            }
        }
    }
//disconnect by setting authenticationid to -1
    public void disconnect() throws SQLException {
        authenticatedId = -1;
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        connection = null;
    }

    /** Returns true if the name/password pair matches a player; remembers that player. */
    public boolean authenticate(String playerName, String password) throws SQLException {
        authenticatedId = -1;
        //prevent sql injection fa ma mne5od l parameters directly.
        //authentication mn 5lel l id mn8yro mn -1 la id tb3 player w id mnjebo mn query
        String sql = "SELECT IdPlayer FROM Player WHERE playerName = ? AND PlayerPassword = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, playerName);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    authenticatedId = rs.getInt(1);
                    return true;
                }
            }
        }
        return false;
    }
//always check if authenitcated if it differ from -1
    public boolean isAuthenticated() {
        return authenticatedId != -1;
    }

    /** Score of the authenticated player, or -1 if nobody is authenticated. */
    public int getScore() throws SQLException {
        //first step check if authenticated
        if (!isAuthenticated()) {
            return -1;
        }
        //select score mn player table 7sb playerid 
        try (PreparedStatement ps = connection.prepareStatement("SELECT score FROM Player WHERE IdPlayer = ?")) {
            ps.setInt(1, authenticatedId);
            try (ResultSet rs = ps.executeQuery()) {
                //eza 3mlna execute w no results wrong id parameter return -1.
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }

    /** Updates the score of the authenticated player; returns false if not authenticated. */
    public boolean updateScore(int newScore) throws SQLException {
        //step 1 check authentication
        if (!isAuthenticated()) {
            return false;
        }
        //update  data from player table using sql query 
        try (PreparedStatement ps = connection.prepareStatement("UPDATE Player SET score = ? WHERE IdPlayer = ?")) {
            //mnb3t l new score ma7al awl ? w authId mahal tene ?
            ps.setInt(1, newScore);
            ps.setInt(2, authenticatedId);
            //return true or false hsb eza update zobet 
            return ps.executeUpdate() == 1;
        }
    }
}
