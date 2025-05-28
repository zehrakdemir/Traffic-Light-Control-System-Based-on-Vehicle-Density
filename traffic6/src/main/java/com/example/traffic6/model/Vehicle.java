package com.example.traffic6.model;

public class Vehicle {
    private int direction; // direction 0: North, 1: South, 2: East, 3: West
    private double position; // aracın kavşağa uzaklığı(negatifse yaklaşıyor)
    private String type; // car, truck, ambulance
    private String turn; // Aracın dönüş yönü (right, left ya da straight)
    private static final double SPEED = 50;
    private static final double SPEED2=500;
    // Pixels per second
    private double turnAngle = 0; // Dönüş sırasında aracın döndüğü açı.
    private boolean isTurning = false; //Aracın şu anda dönüp dönmediğini belirten flag.

    public double getActualX() {
        switch (direction) {
            case 0: // North → aşağı iner
            case 1: // South → yukarı çıkar
                return 400; // dikey şeritte X sabit
            case 2: // East → sola gider
                return 800 + position; // sağdan sola
            case 3: // West → sağa gider
                return position; // soldan sağa
        }
        return 0;
    }

    public double getActualY() {
        switch (direction) {
            case 0: // North → aşağı iner
                return 600 + position; // yukarıdan aşağı
            case 1: // South → yukarı çıkar
                return position; // aşağıdan yukarı
            case 2: // East → sola gider
            case 3: // West → sağa gider
                return 300; // yatay şeritte Y sabit
        }
        return 0;
    }

    public Vehicle(int direction, double initialPosition, String type, String turn) {
        this.direction = direction;
        this.position = -initialPosition - 30;
        this.type = type;
        this.turn = turn;
    }

    public void move(double deltaTime) {
        if (!hasPassedIntersection()) {
            position += SPEED * deltaTime;
        }else if(hasPassedIntersection()){
            position += SPEED2 * deltaTime;
        }

    }


    public boolean hasPassedIntersection() {
        return position > -20; // Account for turning paths
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