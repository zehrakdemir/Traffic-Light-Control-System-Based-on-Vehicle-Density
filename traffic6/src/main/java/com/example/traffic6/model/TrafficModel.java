package com.example.traffic6.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TrafficModel {
    private static final int TOTAL_CYCLE_TIME = 120; // seconds for green lights
    private static final int YELLOW_DURATION = 3; // seconds
    private static final int MIN_GREEN = 10; // seconds
    private static final int MAX_GREEN = 60; // seconds
    private int[] vehicleCounts = new int[4]; // North, South, East, West
    private int[] greenDurations = new int[4];
    private List<Vehicle>[] vehicles = new ArrayList[4]; // Vehicles per direction
    private int currentPhase = 0; // 0: NS Green, 1: NS Yellow, 2: EW Green, 3: EW Yellow
    private double remainingTime = 0;
    private boolean isRunning = false;

    public TrafficModel() {
        for (int i = 0; i < 4; i++) {
            vehicles[i] = new ArrayList<>();
        }
    }

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
        vehicleCounts[0] = rand.nextInt(41);
        vehicleCounts[1] = rand.nextInt(41);
        vehicleCounts[2] = rand.nextInt(41);
        vehicleCounts[3] = rand.nextInt(41);
        calculateGreenDurations();
        generateVehicles();
    }

    private void calculateGreenDurations() {
        int totalVehicles = vehicleCounts[0] + vehicleCounts[1] + vehicleCounts[2] + vehicleCounts[3];
        if (totalVehicles == 0) {
            for (int i = 0; i < 4; i++) {
                greenDurations[i] = MIN_GREEN;
            }
            return;
        }

        double[] percentages = new double[4];
        for (int i = 0; i < 4; i++) {
            percentages[i] = (double) vehicleCounts[i] / totalVehicles;
        }

        // Allocate green times for NS and EW pairs, total 120 seconds
        double nsPercentage = percentages[0] + percentages[1];
        double ewPercentage = percentages[2] + percentages[3];
        int nsGreenTotal = Math.max(MIN_GREEN * 2, Math.min(MAX_GREEN * 2, (int) (TOTAL_CYCLE_TIME * nsPercentage)));
        int ewGreenTotal = TOTAL_CYCLE_TIME - nsGreenTotal;

        greenDurations[0] = (int) (nsGreenTotal * (percentages[0] / (percentages[0] + percentages[1] + 0.0001)));
        greenDurations[1] = nsGreenTotal - greenDurations[0];
        greenDurations[2] = (int) (ewGreenTotal * (percentages[2] / (percentages[2] + percentages[3] + 0.0001)));
        greenDurations[3] = ewGreenTotal - greenDurations[2];

        // Ensure minimum green time
        for (int i = 0; i < 4; i++) {
            greenDurations[i] = Math.max(MIN_GREEN, greenDurations[i]);
        }
    }

    private void generateVehicles() {
        Random rand = new Random();
        for (int i = 0; i < 4; i++) {
            vehicles[i].clear();
            for (int j = 0; j < vehicleCounts[i]; j++) {
                // Randomly assign vehicle type and turn direction
                String type = rand.nextInt(3) == 0 ? "car" : rand.nextInt(2) == 0 ? "truck" : "ambulance";
                String turn = rand.nextInt(3) == 0 ? "left" : rand.nextInt(2) == 0 ? "right" : "straight";
                vehicles[i].add(new Vehicle(i, j * 60, type, turn)); // Increased spacing to 60 pixels
            }
        }
    }

    public void update(double deltaTime) {
        if (!isRunning) return;

        remainingTime -= deltaTime;
        if (remainingTime <= 0) {
            advancePhase();
        }

        // Move vehicles during green phases, prioritize NS over EW if conflict
        if (currentPhase == 0) {
            moveVehiclesWithPriority(0, deltaTime); // North
            moveVehiclesWithPriority(1, deltaTime); // South
        } else if (currentPhase == 2) {
            moveVehiclesWithPriority(2, deltaTime); // East
            moveVehiclesWithPriority(3, deltaTime); // West
        }
    }

    private void advancePhase() {
        currentPhase = (currentPhase + 1) % 4;
        if (currentPhase == 0) {
            remainingTime = greenDurations[0] + greenDurations[1];
        } else if (currentPhase == 1) {
            remainingTime = YELLOW_DURATION;
        } else if (currentPhase == 2) {
            remainingTime = greenDurations[2] + greenDurations[3];
        } else {
            remainingTime = YELLOW_DURATION;
        }
    }

    private void moveVehiclesWithPriority(int direction, double deltaTime) {
        List<Vehicle> toRemove = new ArrayList<>();
        Vehicle prevVehicle = null;
        for (Vehicle vehicle : vehicles[direction]) {
            // Check if vehicle should stop (red or yellow and not past intersection)
            int phase = getCurrentPhase();
            boolean isGreen = (direction <= 1 && phase == 0) || (direction >= 2 && phase == 2);
            boolean isYellow = (direction <= 1 && phase == 1) || (direction >= 2 && phase == 3);
            if (!isGreen && (isYellow && vehicle.getPosition() < -20)) {
                continue; // Stop if yellow and not past intersection
            }

            // Prevent collision with previous vehicle
            if (prevVehicle != null) {
                double minDistance = prevVehicle.getType().equals("truck") ? 80 : 60; // Larger for trucks
                if (vehicle.getPosition() + minDistance > prevVehicle.getPosition()) {
                    continue; // Wait if too close
                }
            }

            vehicle.move(deltaTime);
            if (vehicle.hasPassedIntersection()) {
                toRemove.add(vehicle);
            }
            prevVehicle = vehicle;
        }
        vehicles[direction].removeAll(toRemove);
    }

    public int[] getVehicleCounts() {
        return vehicleCounts;
    }

    public int[] getGreenDurations() {
        return greenDurations;
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

    public void startSimulation() {
        isRunning = true;
        currentPhase = 0;
        remainingTime = greenDurations[0] + greenDurations[1];
    }

    public void pauseSimulation() {
        isRunning = false;
    }

    public void resetSimulation() {
        isRunning = false;
        currentPhase = 0;
        remainingTime = 0;
        for (int i = 0; i < 4; i++) {
            vehicles[i].clear();
        }
    }

    public boolean isRunning() {
        return isRunning;
    }
}