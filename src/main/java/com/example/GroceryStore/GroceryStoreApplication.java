//Student name: Terence Chu
//Student ID: 301220117

package com.example.GroceryStore;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.io.IOException;

public class GroceryStoreApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GroceryStoreApplication.class.getResource("Lab5GroceryStore-View.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1100, 600);
        stage.setTitle("Terence Chu");
        stage.setScene(scene);

        //Get access to the controller
        GroceryStoreController controller = fxmlLoader.getController();

        //setOnHidden documentation from Oracle:
            //"Called just after the Window has been hidden. When the Window is hidden, this event handler is invoked
            //allowing the developer to clean up resources or perform other tasks when the Window is closed."
        //Source: https://docs.oracle.com/javase/8/javafx/api/javafx/stage/Window.html
        //controller.onApplicationClose() contains a method to close the DB connection
        stage.setOnHidden(new EventHandler<WindowEvent>() {
                              @Override
                              public void handle(WindowEvent windowEvent) {
                                  controller.onApplicationClose();
                              }
                          });

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}