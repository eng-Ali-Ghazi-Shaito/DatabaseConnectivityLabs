package com.mycompany.lab4databaseconnection;

import java.sql.SQLException;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Window 1: authentication. Window 2: shows and updates the player's score.
 *
 * @author Evech
 */
//y3ne l class howe javafx 
public class Lab4DatabaseConnection extends Application {
    //object mn class responsible for connection and comunicating with database.
    private final BddComm bdd = new BddComm();
    private Stage scoreStage;

    @Override
    public void start(Stage stage) {
        try {
            //try to connect to database   
            bdd.connect();
        } catch (SQLException e) {
            //if connection failed prevent from crashing. 
            error("Cannot connect to the database: " + e.getMessage());
            return;
        }
        // to enter the name
        TextField nameField = new TextField();
        //special field for password  
        PasswordField passField = new PasswordField();
        //login button
        Button login = new Button("Login");
        //without mouse user can press enter on keyboard as if he pressed on login button.
        login.setDefaultButton(true);
        
        //layout for the button and fields is gridpane(uses columns and rows )

        GridPane grid = new GridPane();
        //horizontal spacing between columns.
        grid.setHgap(10);
        //vertical spacing between rows 
        grid.setVgap(10);
        //add the label on column 0 , row 0 
        grid.add(new Label("Player name:"), 0, 0);
        //same but different columns and rows.
        grid.add(nameField, 1, 0);
        //same but different columns and rows.
        grid.add(new Label("Password:"), 0, 1);
        //same but different columns and rows.
        grid.add(passField, 1, 1);
        //layout the childern vertically where login button(child 2 ) under the grid(child 1 )(labels and textboxes)
        VBox root = new VBox(15, grid, login);
        //set padding 
        root.setPadding(new Insets(20));
        //center the content 
        root.setAlignment(Pos.CENTER);
       //event handlerr for the login button so lama nekbsa shu bado yseer. 
        login.setOnAction(e -> {
            try {
                //check authentication. 
                if (bdd.authenticate(nameField.getText().trim(), passField.getText())) {
                    showScoreWindow(nameField.getText().trim());
                    passField.clear();
                } else {
                    error("Wrong player name or password.");
                }
            } catch (SQLException ex) {
                error(ex.getMessage());
            }
        });

        stage.setTitle("Authentication");
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void showScoreWindow(String name) throws SQLException {
        if (scoreStage != null) {
            scoreStage.close();
        }
        //lama user y3ml login ybyn l score 
        Label scoreLabel = new Label("Score: " + bdd.getScore());
        //wel n3ml textfield m7l ma user bado yfwt new score 
        TextField newScore = new TextField();
        //background la text field nzed 3le klmet new score 
        newScore.setPromptText("New score");
        //badna update button.
        Button update = new Button("Update score");
       //event handler lama nelbos 3la l buton call the function in other class update score then show it on screen by getScore.
        update.setOnAction(e -> {
            try {
                int value = Integer.parseInt(newScore.getText().trim());
                if (bdd.updateScore(value)) {
                    scoreLabel.setText("Score: " + bdd.getScore());
                    newScore.clear();
                }
            } catch (NumberFormatException ex) {
                error("Please enter an integer.");
            } catch (SQLException ex) {
                error(ex.getMessage());
            }
        });
        //layout as befofe this is independet stage and layout 
        VBox root = new VBox(15, new Label("Player: " + name), scoreLabel, new HBox(10, newScore, update));
        root.setPadding(new Insets(20));
        scoreStage = new Stage();
        scoreStage.setTitle("Score");
        scoreStage.setScene(new Scene(root));
        scoreStage.show();
    }
   //error for catch block one time for scailability 
    private void error(String message) {
        new Alert(Alert.AlertType.ERROR, message).showAndWait();
    }

    @Override
    //disconnect
    public void stop() throws SQLException {
        bdd.disconnect();
    }
    //main function starts the program

    public static void main(String[] args) {
        Application.launch(args);
    }
}
