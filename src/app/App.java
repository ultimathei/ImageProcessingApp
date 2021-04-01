package app;

import app.mvc.controllers.Controller;
import app.utils.AppLogger;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * @author Mate Krisztian
 * @date second semester of year 2020/2021 at QMUL CompSci Bsc Image Processing
 *       Application (app) for the Image Processing module at QMUL
 * 
 *       Using the MVC pattern - as singletons Using EventBus and ServiceLocator
 *       pattern to handle events Starting the application through the
 *       controller object
 */
public class App extends Application {
    public static final String APP_NAME = "Image Processing Application";
    private static final Boolean DEBUG_MODE = true;
    public static final AppLogger LOGGER = new AppLogger(false, "Logger");
    public static final AppLogger LOGGER2 = new AppLogger(DEBUG_MODE, "Logger2");

    /**
     * This is the main method
     * 
     * @param args a string array of command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Start up the application
     * 
     * @param primaryStage the Stage object to append the view to
     * @Override start method in Application class
     */
    public void start(Stage primaryStage) {
        Controller.getInstance().setMainStage(primaryStage);
        primaryStage.show();
    }
}