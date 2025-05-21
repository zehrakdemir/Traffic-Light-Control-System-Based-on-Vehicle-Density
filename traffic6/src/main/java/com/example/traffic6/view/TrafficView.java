package com.example.traffic6.view;

import com.example.traffic6.controller.TrafficController;
import com.example.traffic6.model.Vehicle;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.List;

public class TrafficView {
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final TextField[] densityFields = new TextField[4];
    private final Label[] timerLabels = new Label[4];
    private TrafficController controller;
    private final AnimationTimer timer;

    public TrafficView() {
        canvas = new Canvas(800, 800);
        gc = canvas.getGraphicsContext2D();
        timer = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (lastUpdate == 0) {
                    lastUpdate = now;
                    return;
                }
                double deltaTime = (now - lastUpdate) / 1_000_000_000.0;
                lastUpdate = now;
                controller.update(deltaTime);
                render();
            }
        };
    }

    public void setController(TrafficController controller) {
        this.controller = controller;
    }

    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();

        // Input panel
        VBox inputPanel = new VBox(10);
        inputPanel.setPadding(new Insets(10));
        String[] directions = {"North", "South", "East", "West"};
        for (int i = 0; i < 4; i++) {
            HBox row = new HBox(10);
            densityFields[i] = new TextField("0");
            timerLabels[i] = new Label("0s");
            row.getChildren().addAll(new Label(directions[i] + " Vehicles:"), densityFields[i],
                    new Label("Timer:"), timerLabels[i]);
            inputPanel.getChildren().add(row);
        }

        // Control buttons (converted to local variables as suggested)
        HBox controls = new HBox(10);
        Button startButton = new Button("Start");
        Button pauseButton = new Button("Pause");
        Button resetButton = new Button("Reset");
        Button randomButton = new Button("Random");
        controls.getChildren().addAll(startButton, pauseButton, resetButton, randomButton);
        controls.setAlignment(Pos.CENTER);
        inputPanel.getChildren().add(controls);

        root.setTop(inputPanel);
        root.setCenter(canvas);

        // Event handlers
        startButton.setOnAction(e -> controller.startSimulation());
        pauseButton.setOnAction(e -> controller.pauseSimulation());
        resetButton.setOnAction(e -> controller.resetSimulation());
        randomButton.setOnAction(e -> controller.generateRandomCounts());

        // Start the animation timer
        timer.start();

        Scene scene = new Scene(root, 900, 900);
        primaryStage.setTitle("Traffic Light Simulation");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void render() {
        // Clear canvas
        gc.setFill(Color.GREEN); // Grass background
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Draw roads
        gc.setFill(Color.GRAY);
        gc.fillRect(0, 350, 350, 100); // West road
        gc.fillRect(450, 350, 350, 100); // East road
        gc.fillRect(350, 0, 100, 350); // North road
        gc.fillRect(350, 450, 100, 350); // South road

        // Draw dashed yellow center lines
        gc.setStroke(Color.YELLOW);
        gc.setLineWidth(2);
        gc.setLineDashes(10, 10);
        gc.strokeLine(0, 400, 350, 400); // West road center
        gc.strokeLine(450, 400, 800, 400); // East road center
        gc.strokeLine(400, 0, 400, 350); // North road center
        gc.strokeLine(400, 450, 400, 800); // South road center
        gc.setLineDashes(null);

        // Draw intersection
        gc.setFill(Color.GRAY);
        gc.fillRect(350, 350, 100, 100);

        // Draw trees
        gc.setFill(Color.BROWN);
        gc.fillRect(300, 300, 10, 20);
        gc.setFill(Color.DARKGREEN);
        gc.fillOval(290, 280, 30, 30);
        gc.setFill(Color.BROWN);
        gc.fillRect(490, 300, 10, 20);
        gc.setFill(Color.DARKGREEN);
        gc.fillOval(480, 280, 30, 30);
        gc.setFill(Color.BROWN);
        gc.fillRect(300, 480, 10, 20);
        gc.setFill(Color.DARKGREEN);
        gc.fillOval(290, 460, 30, 30);
        gc.setFill(Color.BROWN);
        gc.fillRect(490, 480, 10, 20);
        gc.setFill(Color.DARKGREEN);
        gc.fillOval(480, 460, 30, 30);

        // Draw traffic lights (outside roads)
        int phase = controller.getModel().getCurrentPhase();
        drawTrafficLight(320, 260, phase == 0 ? "green" : phase == 1 ? "yellow" : "red"); // North
        drawTrafficLight(450, 500, phase == 0 ? "green" : phase == 1 ? "yellow" : "red"); // South
        drawTrafficLight(500, 320, phase == 2 ? "green" : phase == 3 ? "yellow" : "red"); // East
        drawTrafficLight(260, 450, phase == 2 ? "green" : phase == 3 ? "yellow" : "red"); // West

        // Draw vehicles
        List<Vehicle>[] vehicles = controller.getModel().getVehicles();
        for (int i = 0; i < 4; i++) {
            for (Vehicle vehicle : vehicles[i]) {
                double pos = vehicle.getPosition();
                double x = 0, y = 0;
                double angle = vehicle.isTurning() ? vehicle.getTurnAngle() : 0;
                gc.save();
                if (i == 0) { // North
                    x = 375;
                    y = 350 - pos;
                    if (vehicle.isTurning()) {
                        gc.translate(x, y);
                        gc.rotate(-angle * (vehicle.getTurn().equals("left") ? 1 : -1));
                        x = 0;
                        y = 0;
                    }
                } else if (i == 1) { // South
                    x = 405;
                    y = 450 + pos;
                    if (vehicle.isTurning()) {
                        gc.translate(x, y);
                        gc.rotate(angle * (vehicle.getTurn().equals("left") ? 1 : -1));
                        x = 0;
                        y = 0;
                    }
                } else if (i == 2) { // East
                    x = 450 + pos;
                    y = 375;
                    if (vehicle.isTurning()) {
                        gc.translate(x, y);
                        gc.rotate(angle * (vehicle.getTurn().equals("left") ? -1 : 1));
                        x = 0;
                        y = 0;
                    }
                } else { // West
                    x = 350 - pos;
                    y = 405;
                    if (vehicle.isTurning()) {
                        gc.translate(x, y);
                        gc.rotate(-angle * (vehicle.getTurn().equals("left") ? -1 : 1));
                        x = 0;
                        y = 0;
                    }
                }
                drawVehicle(x, y, vehicle.getType());
                gc.restore();
            }
        }

        // Update timers
        double remaining = controller.getModel().getRemainingTime();
        timerLabels[0].setText(phase == 0 || phase == 1 ? String.format("%.1fs", remaining) : "0.0s"); // North
        timerLabels[1].setText(phase == 0 || phase == 1 ? String.format("%.1fs", remaining) : "0.0s"); // South
        timerLabels[2].setText(phase == 2 || phase == 3 ? String.format("%.1fs", remaining) : "0.0s"); // East
        timerLabels[3].setText(phase == 2 || phase == 3 ? String.format("%.1fs", remaining) : "0.0s"); // West
    }

    private void drawTrafficLight(double x, double y, String activeLight) {
        gc.setFill(Color.BLACK);
        gc.fillRect(x, y, 30, 90);
        gc.setFill(activeLight.equals("red") ? Color.RED : Color.GRAY);
        gc.fillOval(x + 5, y + 5, 20, 20);
        gc.setFill(activeLight.equals("yellow") ? Color.YELLOW : Color.GRAY);
        gc.fillOval(x + 5, y + 35, 20, 20);
        gc.setFill(activeLight.equals("green") ? Color.GREEN : Color.GRAY);
        gc.fillOval(x + 5, y + 65, 20, 20);
    }

    private void drawVehicle(double x, double y, String type) {
        if (type.equals("car")) {
            gc.setFill(Color.BLUE);
            gc.fillRect(x - 15, y - 7, 30, 14); // Car body
            gc.setFill(Color.BLACK);
            gc.fillOval(x - 10, y + 2, 5, 5); // Left wheel
            gc.fillOval(x + 5, y + 2, 5, 5); // Right wheel
        } else if (type.equals("truck")) {
            gc.setFill(Color.RED);
            gc.fillRect(x - 20, y - 10, 40, 20); // Truck body
            gc.setFill(Color.BLACK);
            gc.fillOval(x - 15, y + 5, 6, 6); // Left front wheel
            gc.fillOval(x - 5, y + 5, 6, 6); // Left rear wheel
            gc.fillOval(x + 9, y + 5, 6, 6); // Right rear wheel
        } else if (type.equals("ambulance")) {
            gc.setFill(Color.WHITE);
            gc.fillRect(x - 15, y - 7, 30, 14); // Ambulance body
            gc.setFill(Color.RED);
            gc.fillRect(x - 5, y - 3, 10, 6); // Red cross
            gc.setFill(Color.BLACK);
            gc.fillOval(x - 10, y + 2, 5, 5); // Left wheel
            gc.fillOval(x + 5, y + 2, 5, 5); // Right wheel
        }
    }

    public TextField[] getDensityFields() {
        return densityFields;
    }

    public void updateVehicleCounts(int[] counts) {
        for (int i = 0; i < 4; i++) {
            densityFields[i].setText(String.valueOf(counts[i]));
        }
    }
}