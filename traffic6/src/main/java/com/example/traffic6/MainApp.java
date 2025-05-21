package com.example.traffic6;

import com.example.traffic6.controller.TrafficController;
import com.example.traffic6.model.TrafficModel;
import com.example.traffic6.view.TrafficView;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        TrafficModel model = new TrafficModel();
        TrafficView view = new TrafficView();
        TrafficController controller = new TrafficController(model, view);

        view.start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}