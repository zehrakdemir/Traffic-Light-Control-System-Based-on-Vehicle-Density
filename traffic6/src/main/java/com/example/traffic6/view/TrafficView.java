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
        canvas = new Canvas(600, 600);
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
        String[] directions = {"South", "North", "West", "East"};

        timerLabels[0] = new Label("0.0s");
        timerLabels[1] = new Label("0.0s");
        timerLabels[2] = new Label("0.0s");
        timerLabels[3] = new Label("0.0s");
        for (int i = 0; i < 4; i++) {
            HBox row = new HBox(10);
            final TextField densityField = new TextField("0");
            densityFields[i] = densityField;

            row.getChildren().addAll(new Label(directions[i] + " Vehicles:"), densityField,
                    new Label("Timer:"), timerLabels[i]);
            inputPanel.getChildren().add(row);
            densityField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal.matches("\\d*")) {
                    densityField.setText(newVal.replaceAll("[^\\d]", ""));
                }
            });
        }

        // Kontrol düğmeleri
        HBox controls = new HBox(10);
        Button startButton = new Button("Start");
        Button pauseButton = new Button("Pause");
        Button resetButton = new Button("Reset");
        Button randomButton = new Button("Random");
        Button resumeButton = new Button("Resume");
        controls.getChildren().addAll(startButton, pauseButton, resumeButton,resetButton, randomButton);
        controls.setAlignment(Pos.CENTER);
        inputPanel.getChildren().add(controls);

        root.setTop(inputPanel);
        root.setCenter(canvas);

        // Buton işlevleri
        startButton.setOnAction(e -> controller.startSimulation());
        pauseButton.setOnAction(e -> controller.pauseSimulation());
        resetButton.setOnAction(e -> controller.resetSimulation());
        randomButton.setOnAction(e -> controller.generateRandomCounts());
        resumeButton.setOnAction(e -> controller.resumeSimulation());

        // zamanlayıcıyı başlatır
        timer.start();

        Scene scene = new Scene(root, 700, 700);
        primaryStage.setTitle("TRAFFIC LIGHT SIMULATION");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    //Ağaç çizimi
    private void drawTree(GraphicsContext gc, double x, double y) {
        gc.setFill(Color.BROWN);
        gc.fillRect(x, y, 10, 20); // Gövde
        gc.setFill(Color.DARKGREEN);
        gc.fillOval(x - 10, y - 20, 30, 30); // Yapraklar
    }

    //Ev çizimi
    private void drawHouse(GraphicsContext gc, double x, double y) {
        // Ev gövdesi
        gc.setFill(Color.BEIGE);
        gc.fillRect(x, y, 80, 60);

        // Çatı
        gc.setFill(Color.DARKRED);
        gc.fillPolygon(new double[]{x - 10, x + 40, x + 90}, new double[]{y, y - 40, y}, 3);

        // Kapı
        gc.setFill(Color.SADDLEBROWN);
        gc.fillRect(x + 30, y + 25, 20, 35);

        // Pencere
        gc.setFill(Color.LIGHTBLUE);
        gc.fillRect(x + 10, y + 15, 15, 15);
        gc.fillRect(x + 55, y + 15, 15, 15);
    }

    //Yaya geçidi çizimi
    private void drawCrosswalk(double startX, double startY, boolean horizontal) {  //yaya geçidi
        gc.setFill(Color.WHITE);
        int stripeCount = 7;
        double stripeWidth = 5.5;
        double stripeLength = 40;
        double gap = 10;

        for (int i = 0; i < stripeCount; i++) {
            double x = startX + (horizontal ? (i * (stripeWidth + gap)) : 0);
            double y = startY + (!horizontal ? (i * (stripeWidth + gap)) : 0);
            double width = horizontal ? stripeWidth : stripeLength;
            double height = horizontal ? stripeLength : stripeWidth;
            gc.fillRect(x, y, width, height);
        }
    }

    //Dikey trafik ışığı çizimi
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

    //Yatay trafik ışığı çizimi
    private void drawTrafficLightHorizontal(double x, double y, String activeLight) {
        gc.setFill(Color.BLACK);
        gc.fillRect(x, y, 90, 30); // Yatay siyah kutu (3 ışık için 30x30'luk kutular)

        // Kırmızı ışık (solda)
        gc.setFill(activeLight.equals("red") ? Color.RED : Color.GRAY);
        gc.fillOval(x + 5, y + 5, 20, 20);

        // Sarı ışık (ortada)
        gc.setFill(activeLight.equals("yellow") ? Color.YELLOW : Color.GRAY);
        gc.fillOval(x + 35, y + 5, 20, 20);

        // Yeşil ışık (sağda)
        gc.setFill(activeLight.equals("green") ? Color.GREEN : Color.GRAY);
        gc.fillOval(x + 65, y + 5, 20, 20);
    }

    //Çiçek çizimi
    public static void drawFlower(GraphicsContext gc, double centerX, double centerY, Color petalColor, Color centerColor) {
        double petalRadius = 6;
        int petals = 4;

        gc.setFill(petalColor);

        // Yaprakları çiz
        for (int i = 0; i < petals; i++) {
            double angle = 2 * Math.PI / petals * i;
            double x = centerX + Math.cos(angle) * petalRadius;
            double y = centerY + Math.sin(angle) * petalRadius;
            gc.fillOval(x - petalRadius, y - petalRadius, petalRadius * 2, petalRadius * 2);
        }

        // Orta kısmı çiz
        gc.setFill(centerColor);
        gc.fillOval(centerX - petalRadius / 2, centerY - petalRadius / 2, petalRadius, petalRadius);
    }

    // Ördek çizimi
    private void drawDuck(GraphicsContext gc, double x, double y) {
        // Gövde
        gc.setFill(Color.ORANGE);
        gc.fillOval(x, y, 25, 15);

        // Kafa
        gc.fillOval(x + 18, y - 8, 12, 12);

        // Gaga
        gc.setFill(Color.YELLOW);
        gc.fillPolygon(new double[]{x + 28, x + 35, x + 28}, new double[]{y - 3, y + 2, y + 7}, 3);

        // Göz
        gc.setFill(Color.BLACK);
        gc.fillOval(x + 24, y - 5, 2, 2);

        // Ayaklar
        gc.setFill(Color.ORANGE.darker());
        gc.fillRect(x + 5, y + 15, 3, 5);
        gc.fillRect(x + 15, y + 15, 3, 5);
    }

    // İnek çizimi
    private void drawCow(GraphicsContext gc, double x, double y) {
        // Gövde
        gc.setFill(Color.WHITE);
        gc.fillRect(x, y, 40, 25);

        // Lekeler
        gc.setFill(Color.BLACK);
        gc.fillOval(x + 5, y + 5, 10, 8);
        gc.fillOval(x + 25, y + 15, 8, 5);

        // Kafa
        gc.setFill(Color.WHITE);
        gc.fillOval(x - 15, y - 5, 20, 15);

        // Göz
        gc.setFill(Color.BLACK);
        gc.fillOval(x - 10, y - 2, 3, 3);

        // Burun
        gc.setFill(Color.PINK);
        gc.fillOval(x - 13, y + 5, 6, 4);

        // Kulaklar
        gc.setFill(Color.WHITE);
        gc.fillOval(x - 17, y - 8, 5, 5); // Sol kulak
        gc.fillOval(x + 2, y - 8, 5, 5);  // Sağ kulak

        // Ayaklar
        gc.setFill(Color.BLACK);
        gc.fillRect(x + 5, y + 25, 4, 7);   // Sol ön ayak
        gc.fillRect(x + 30, y + 25, 4, 7);  // Sağ arka ayak

        // Kuyruk
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeLine(x + 40, y + 10, x + 48, y + 15);
        gc.setFill(Color.BLACK);
        gc.fillOval(x + 46, y + 13, 4, 4); // Kuyruğun ucu
    }

    //Araç çizimi
    private void drawVehicle(double x, double y, String type) {
        if (type.equals("car")) {
            gc.setFill(Color.DARKRED);
            gc.fillRect(x - 15, y - 7, 30, 14); // Car body
            gc.setFill(Color.BLACK);
            gc.fillOval(x - 10, y + 2, 5, 5); // Left wheel
            gc.fillOval(x + 5, y + 2, 5, 5); // Right wheel
        } else if (type.equals("truck")) {
            gc.setFill(Color.DARKSLATEBLUE);
            gc.fillRect(x - 20, y - 10, 40, 20); // Truck body
            gc.setFill(Color.BLACK);
            gc.fillOval(x - 15, y + 5, 6, 6); // Left front wheel
            gc.fillOval(x - 5, y + 5, 6, 6); // Left rear wheel
            gc.fillOval(x + 9, y + 5, 6, 6); // Right rear wheel
        } else if (type.equals("ambulance")) {
            gc.setFill(Color.WHITESMOKE);
            gc.fillRect(x - 15, y - 7, 30, 14); // Ambulance body
            gc.setFill(Color.RED);
            gc.fillRect(x - 5, y - 3, 10, 6); // Red cross
            gc.setFill(Color.BLACK);
            gc.fillOval(x - 10, y + 2, 5, 5); // Left wheel
            gc.fillOval(x + 5, y + 2, 5, 5); // Right wheel
        }
    }

    public void render() {
        // Clear canvas
        gc.setFill(Color.GREEN); // Grass background
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Yol çizimi
        gc.setFill(Color.GRAY);
        gc.fillRect(0, 250, 250, 100); // West road
        gc.fillRect(350, 250, 250, 100); // East road
        gc.fillRect(250, 0, 100, 250); // North road
        gc.fillRect(250, 350, 100, 250); // South road

        // Yol şerit çizimi
        gc.setStroke(Color.YELLOW);
        gc.setLineWidth(3);
        gc.setLineDashes(10, 10);
        gc.strokeLine(0, 300, 250, 300); // West road center
        gc.strokeLine(350, 300, 600, 300); // East road center
        gc.strokeLine(300, 0, 300, 250); // North road center
        gc.strokeLine(300, 350, 300, 600); // South road center
        gc.setLineDashes(null);

        //Kavşak çizimi
        gc.setFill(Color.GRAY);
        gc.fillRect(250, 250, 100, 100);

        //ev çizimi
        drawHouse(gc, 70, 140);
        drawHouse(gc, 460, 140);

        // Bahçeye çiçekler ekle
        drawFlower(gc, 30, 30, Color.PINK, Color.YELLOW);
        drawFlower(gc, 100, 65, Color.LIGHTBLUE, Color.ORANGE);
        drawFlower(gc, 40, 100, Color.DARKRED, Color.WHITE);
        drawFlower(gc, 180, 100, Color.RED, Color.WHITE);
        drawFlower(gc, 410, 400, Color.HOTPINK, Color.WHITE);
        drawFlower(gc, 40, 460, Color.MEDIUMPURPLE, Color.WHITE);
        drawFlower(gc, 210, 480, Color.YELLOWGREEN, Color.WHITE);
        drawFlower(gc, 440, 60, Color.YELLOW, Color.WHITE);
        drawFlower(gc, 180, 30, Color.ORANGE, Color.WHITE);
        drawFlower(gc, 570, 80, Color.DARKBLUE, Color.WHITE);
        drawFlower(gc, 190, 570, Color.SADDLEBROWN, Color.WHITE);
        drawFlower(gc, 60, 580, Color.DARKSLATEBLUE, Color.WHITE);
        drawFlower(gc, 420, 570, Color.BROWN, Color.WHITE);
        drawFlower(gc, 550, 580, Color.DARKSLATEBLUE, Color.WHITE);

        // Ördek çizimi
        drawDuck(gc, 30, 210); // Sol üst yeşil alana yerleştirildi.
        drawDuck(gc, 380, 180); // Sağ üst yeşil alana yerleştirildi
        drawDuck(gc, 90, 480); // Sol alt yeşil alana yerleştirildi
        drawDuck(gc, 500, 480);

        // İnek çizimi
        drawCow(gc, 40, 400);
        drawCow(gc, 500, 400);

        // Ağaç çizimi
        drawTree(gc, 170, 180);
        drawTree(gc, 430, 180);
        drawTree(gc, 170, 420);
        drawTree(gc, 430, 420);

        // Yaya geçidi çizimi
        drawCrosswalk(180, 250, false); // West

        // Trafik ışıklarının renklendirilmesi
        int phase = controller.getModel().getCurrentPhase();
        drawTrafficLight(350, 350, phase == 2  ? "green" : (phase == 1)||(phase==3) ? "yellow" : "red"); // North
        drawTrafficLight(220, 160, phase == 6 ? "green" : (phase == 5)||(phase==7) ? "yellow" : "red"); // South

        drawTrafficLightHorizontal(350, 220, phase == 0 ? "green" : (phase == 1)||(phase==7)  ? "yellow" : "red"); // west
        drawTrafficLightHorizontal(160, 350, phase == 4 ? "green" : (phase == 3)||(phase==5)  ? "yellow" : "red"); // east

        // Araç çizimi
        List<Vehicle>[] vehicles = controller.getModel().getVehicles();
        for (int i = 0; i < 4; i++) {
            for (Vehicle vehicle : vehicles[i]) {
                double pos = vehicle.getPosition();
                double x = 0, y = 0;
                gc.save();
                if (i == 0) { // North
                    x = 325;
                    y = 340 - pos;
                } else if (i == 1) { // South
                    x = 275;
                    y = 260 + pos;
                } else if (i == 2) { // East
                    x = 250 + pos;
                    y = 325;
                } else { // West
                    x = 350 - pos;
                    y = 275;
                }
                drawVehicle(x, y, vehicle.getType());
                gc.restore();
            }
        }

        // Zamanlayıcıyı güncelle
        double remaining = controller.getModel().getRemainingTime();
        timerLabels[0].setText(phase == 2 ? String.format("%.1fs", remaining) : "0.0s"); // North
        timerLabels[1].setText(phase == 6 ? String.format("%.1fs", remaining) : "0.0s"); // South
        timerLabels[2].setText(phase == 4 ? String.format("%.1fs", remaining) : "0.0s"); // East
        timerLabels[3].setText(phase == 0 ? String.format("%.1fs", remaining) : "0.0s"); // West
    }

    public TextField[] getDensityFields() {
        return densityFields;
    }

    public void updateVehicleCounts(int[] counts) {
        TextField[] fields = getDensityFields();
        fields[0].setText(String.valueOf(counts[0]));
        fields[1].setText(String.valueOf(counts[1]));
        fields[2].setText(String.valueOf(counts[2]));
        fields[3].setText(String.valueOf(counts[3]));
    }
}