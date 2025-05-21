package com.example.traffic6.model;

public class Vehicle {
    private int direction; // 0: North, 1: South, 2: East, 3: West
    private double position; // Distance from intersection (negative = approaching)
    private String type; // car, truck, ambulance
    private String turn; // left, right, straight
    private static final double SPEED = 50; // Pixels per second
    private double turnAngle = 0; // For turning vehicles
    private boolean isTurning = false;

    public Vehicle(int direction, double initialPosition, String type, String turn) {
        this.direction = direction;
        this.position = -initialPosition - 20;
        this.type = type;
        this.turn = turn;
    }

    public void move(double deltaTime) {
        if (position < 0 || !turn.equals("straight")) {
            position += SPEED * deltaTime;
        }
        if (position >= -20 && !turn.equals("straight") && !isTurning) {
            isTurning = true;
            turnAngle = 0;
        }
        if (isTurning) {
            turnAngle += SPEED * deltaTime * 0.5; // Adjust turn speed
            if (turnAngle >= 90) {
                isTurning = false;
                position += SPEED * deltaTime;
            }
        }
    }

    public boolean hasPassedIntersection() {
        return position > 100; // Account for turning paths
    }

    public int getDirection() {
        return direction;
    }

    public double getPosition() {
        return position;
    }

    public String getType() {
        return type;
    }

    public String getTurn() {
        return turn;
    }

    public double getTurnAngle() {
        return turnAngle;
    }

    public boolean isTurning() {
        return isTurning;
    }
}