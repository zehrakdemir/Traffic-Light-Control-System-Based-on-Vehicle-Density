package com.example.traffic6.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class TrafficModel {
    private static final int TOTAL_CYCLE_TIME = 120; // toplam yeşil ışık süresi
    private static final int YELLOW_DURATION = 3; // seconds
    private static final int MIN_GREEN = 10; // seconds
    private static final int MAX_GREEN = 60; // seconds
    private int[] vehicleCounts = new int[4]; // North, South, East, West
    private int[] greenDurations = new int[4];
    private List<Vehicle>[] vehicles = new ArrayList[4]; // Yöne göre araçlar
    private int currentPhase = 0; // 0: W Green, 1: WN Yellow, 2: N Green, 3: NE Yellow, 4: E Green, 5: ES Yellow, 6: S Green, 7: SW Yellow
    private double remainingTime = 0;
    private boolean isRunning = false;
    private boolean isPaused = false;

    public TrafficModel() {
        for (int i = 0; i < 4; i++) {
            vehicles[i] = new ArrayList<>();
        }
    }

    // araç sayılarını alır
    public void setVehicleCounts(int north, int south, int east, int west) {
        vehicleCounts[0] = Math.max(0, north);
        vehicleCounts[1] = Math.max(0, south);
        vehicleCounts[2] = Math.max(0, east);
        vehicleCounts[3] = Math.max(0, west);
        calculateGreenDurations();
        generateVehicles();
    }

    public void generateRandomCounts() {
        Random rand = new Random();
        vehicleCounts[0] = rand.nextInt(101);
        vehicleCounts[1] = rand.nextInt(101);
        vehicleCounts[2] = rand.nextInt(101);
        vehicleCounts[3] = rand.nextInt(101);
        calculateGreenDurations();
        generateVehicles();
    }

    private void calculateGreenDurations() {
        int totalVehicles = vehicleCounts[0] + vehicleCounts[1] + vehicleCounts[2] + vehicleCounts[3];

        double[] percentages = new double[4]; // o yoldaki araç sayısı / toplam araç sayısı = yüzde
        for (int i = 0; i < 4; i++) {
            percentages[i] = (double) vehicleCounts[i] / totalVehicles;
        }

        // 0: north, 1: south, 2: east, 3: west
        greenDurations[0] = Math.min(MAX_GREEN, Math.max(MIN_GREEN, (int) (TOTAL_CYCLE_TIME * percentages[0]))); // North
        greenDurations[1] = Math.min(MAX_GREEN, Math.max(MIN_GREEN, (int) (TOTAL_CYCLE_TIME * percentages[1]))); // South
        greenDurations[2] = Math.min(MAX_GREEN, Math.max(MIN_GREEN, (int) (TOTAL_CYCLE_TIME * percentages[2]))); // East
        greenDurations[3] = Math.min(MAX_GREEN, Math.max(MIN_GREEN, (int) (TOTAL_CYCLE_TIME * percentages[3]))); // West
        for (int i = 0; i < 4; i++) { // hiç araç yoksa 0 saniye yeşil yansın
            if(vehicleCounts[i] == 0) {greenDurations[i] = 0;}
        }

    }

    private void generateVehicles() {
        Random rand = new Random();
        for (int i = 0; i < 4; i++) {
            vehicles[i].clear();
            for (int j = 0; j < vehicleCounts[i]; j++) {
                String type = rand.nextInt(3) == 0 ? "car" : rand.nextInt(2) == 0 ? "truck" : "ambulance";
                String turn = "straight";
                vehicles[i].add(new Vehicle(i, j * 90, type, turn)); //araç aralıkları
            }
        }
    }

    //0: W Green, 1: WN Yellow, 2: N Green, 3: NE Yellow, 4: E Green, 5: ES Yellow, 6: S Green, 7: SW Yellow
    public void update(double deltaTime) {
        if (!isRunning || isPaused) return; //durma duraklatma kontrolü

        remainingTime -= deltaTime;
        if (remainingTime <= 0) {//remainingTime şu anki trafik ışığının bitmesine kalan süre
            advancePhase();
        }

        for (int i = 0; i < 4; i++) {
            moveVehiclesWithPriority(i, deltaTime);
        }
    }

    private void advancePhase() {
        currentPhase = (currentPhase + 1) % 8;
// 0: W Green, 1: WN Yellow, 2: N Green, 3: NE Yellow, 4: E Green, 5: ES Yellow, 6: S Green, 7: SW Yellow
        switch (currentPhase) {
            case 0: // W Green
                remainingTime = greenDurations[3]; // West
                break;
            case 1: // WN Yellow
                remainingTime = YELLOW_DURATION;
                break;
            case 2: // N Green
                remainingTime = greenDurations[0]; // North
                break;
            case 3: // NE Yellow
                remainingTime = YELLOW_DURATION;
                break;
            case 4: // E Green
                remainingTime = greenDurations[2]; // East
                break;
            case 5: // ES Yellow
                remainingTime = YELLOW_DURATION;
                break;
            case 6: // S Green
                remainingTime = greenDurations[1]; // South
                break;
            case 7: // SW Yellow
                remainingTime = YELLOW_DURATION;
                break;
        }
    }

    private void moveVehiclesWithPriority(int direction, double deltaTime) {
        List<Vehicle> toRemove = new ArrayList<>();
        Vehicle prevVehicle = null;
        vehicles[direction].sort(Comparator.comparingDouble(Vehicle::getPosition).reversed());

        for (Vehicle vehicle : vehicles[direction]) {
            int phase = getCurrentPhase();
            // 0: W Green, 1: WN Yellow, 2: N Green, 3: NE Yellow, 4: E Green, 5: ES Yellow, 6: S Green, 7: SW Yellow
            boolean isGreen = (direction == 3 && phase == 0) || (direction == 0 && phase == 2) || (direction == 2 && phase == 4) || (direction == 1 && phase == 6);
            boolean isYellow = (((direction == 0) || (direction == 3)) && phase == 1) ||
                    (((direction == 0) || (direction == 2)) && phase == 3) ||
                    (((direction == 1) || (direction == 2)) && phase == 5) ||
                    (((direction == 1) || (direction == 3)) && phase == 7);

            if (!isGreen && (isYellow && vehicle.getPosition() < -20)) {
                continue; // sarıysa ve kavşağı geçmediyse dur
            }

            //çarpışma kontrolü
            if (prevVehicle != null) {
                double dx = prevVehicle.getActualX() - vehicle.getActualX();
                double dy = prevVehicle.getActualY() - vehicle.getActualY();
                double distance = Math.sqrt(dx * dx + dy * dy);

                double minDistance = prevVehicle.getType().equals("truck") ? 80 : 60;
                if (distance < minDistance) {
                    continue; // çok yaklaştınnn
                }
            }

            if (vehicle.hasPassedIntersection() || (!vehicle.hasPassedIntersection() && isGreen)) {
                vehicle.move(deltaTime);
            }

            prevVehicle = vehicle;
        }
        vehicles[direction].removeAll(toRemove);
    }

    public int[] getVehicleCounts() {
        return vehicleCounts;
    }

    public List<Vehicle>[] getVehicles() {
        return vehicles;
    }

    public int getCurrentPhase() {
        return currentPhase;
    }

    public double getRemainingTime() {
        return remainingTime;
    }

    // Control simulation state methods
    public void startSimulation() {
        calculateGreenDurations();
        isRunning = true;
        isPaused = false;
        currentPhase = 0;//west ten başlatıyor
        remainingTime = greenDurations[3]; // West green starts first
    }

    public void pauseSimulation() {
        if (isRunning && !isPaused) {
            isPaused = true;
        }
    }

    public void resumeSimulation() {
        if (isRunning && isPaused) {
            isPaused = false;
        }
    }

    public void stopSimulation() {
        isRunning = false;
        isPaused = false;
    }

    public void resetSimulation() {
        stopSimulation();
        currentPhase = 0;
        remainingTime = 0;
        for (int i = 0; i < 4; i++) {
            vehicleCounts[i] = 0;
            greenDurations[i] = 0;
            vehicles[i].clear();
        }
    }
}