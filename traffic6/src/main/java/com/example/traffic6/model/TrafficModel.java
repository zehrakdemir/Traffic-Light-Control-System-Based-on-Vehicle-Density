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
    private int currentPhase = 0; // 0:     W Green, 1: WN Yellow, 2: N Green, 3: NE Yellow, 4: E Green, 5: ES Yellow, 6: S Green, 7: SW Yellow
    private double remainingTime = 0;
    private boolean isRunning = false;

    public TrafficModel() {
        for (int i = 0; i < 4; i++) {
            vehicles[i] = new ArrayList<>();
        }
    }

    //0: north, 1: south, 2: east, 3: west
    //yönlerdeki araç sayıları
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
            for (int i = 0; i < 4; i++) {//hiç araç yoksa 10 saniye yeşil yansın
                greenDurations[i] = MIN_GREEN;
            }
            return;
        }



        double[] percentages = new double[4]; //o yoldaki araç sayısı/ toplam araç sayısı= yüzde
        for (int i = 0; i < 4; i++) {
            percentages[i] = (double) vehicleCounts[i] / totalVehicles;
        }

        //  toplam 120 saniye
        double nPercentage = percentages[3] ;
        double sPercentage= percentages[1];
        double ePercentage = percentages[2];
        double wPercentage = percentages[0];
        int nGreenTotal = Math.max(MIN_GREEN , Math.min(MAX_GREEN * 2, (int) (TOTAL_CYCLE_TIME * nPercentage)));
        int sGreenTotal = Math.max(MIN_GREEN , Math.min(MAX_GREEN * 2, (int) (TOTAL_CYCLE_TIME * sPercentage)));
        int eGreenTotal = Math.max(MIN_GREEN , Math.min(MAX_GREEN * 2, (int) (TOTAL_CYCLE_TIME * ePercentage)));
        int wGreenTotal = Math.max(MIN_GREEN , Math.min(MAX_GREEN * 2, (int) (TOTAL_CYCLE_TIME * wPercentage)));
//0:     W Green, 1: WN Yellow, 2: N Green, 3: NE Yellow, 4: E Green, 5: ES Yellow, 6: S Green, 7: SW Yellow
        // int[] greenDurations = new int[4];
        greenDurations[3] = nGreenTotal;
        greenDurations[1] = sGreenTotal;
        greenDurations[2] = eGreenTotal;
        greenDurations[0] = wGreenTotal;
//0: north, 1: south, 2: east, 3: west
        // minimum yeşil yanma süresine uygunluk
        for (int i = 0; i < 4; i++) {
            greenDurations[i] = Math.max(MIN_GREEN, greenDurations[i]);
            greenDurations[i] = Math.min(MAX_GREEN, greenDurations[i]);
        }
    }

    private void generateVehicles() {
        Random rand = new Random();
        for (int i = 0; i < 4; i++) {
            vehicles[i].clear();
            for (int j = 0; j < vehicleCounts[i]; j++) {

                // Araç tipini rastgele atama
                String type = rand.nextInt(3) == 0 ? "car" : rand.nextInt(2) == 0 ? "truck" : "ambulance";
                String turn = "straight"; //rand.nextInt(3) == 0 ? "left" : rand.nextInt(2) == 0 ? "right" : sildim
                vehicles[i].add(new Vehicle(i, j * 60, type, turn)); // Increased spacing to 60 pixels
            }
        }
    }
    //0:     W Green, 1: WN Yellow, 2: N Green, 3: NE Yellow, 4: E Green, 5: ES Yellow, 6: S Green, 7: SW Yellow
    public void update(double deltaTime) {
        if (!isRunning) return;

        remainingTime -= deltaTime;
        if (remainingTime <= 0) {
            advancePhase();
        }

        // Yeşil ışıkta araçları hareket ettir
        //0:     W Green, 1: WN Yellow, 2: N Green, 3: NE Yellow, 4: E Green, 5: ES Yellow, 6: S Green, 7: SW Yellow
        // 0: W Green, 1: WN Yellow, 2: N Green, 3: NE Yellow, 4: E Green, 5: ES Yellow, 6: S Green, 7: SW Yellow
        if (currentPhase == 0) {
            moveVehiclesWithPriority(3, deltaTime); // West
        } else if (currentPhase == 2) {
            moveVehiclesWithPriority(0, deltaTime); // North
        } else if (currentPhase == 4) {
            moveVehiclesWithPriority(2, deltaTime); // East
        } else if (currentPhase == 6) {
            moveVehiclesWithPriority(1, deltaTime); // South
        }
        //0: north, 1: south, 2: east, 3: west
    }
    //0:     W Green, 1: WN Yellow, 2: N Green, 3: NE Yellow, 4: E Green, 5: ES Yellow, 6: S Green, 7: SW Yellow
    // 0: W Green, 1: WN Yellow, 2: N Green, 3: NE Yellow, 4: E Green, 5: ES Yellow, 6: S Green, 7: SW Yellow
    private void advancePhase() {
        currentPhase = (currentPhase + 1) % 8;

        switch (currentPhase) {
            /*greenDurations[0] = nGreenTotal;
        greenDurations[1] = sGreenTotal;
        greenDurations[2] = eGreenTotal;
        greenDurations[3] = wGreenTotal;*/
           /* greenDurations[3] = nGreenTotal;
            greenDurations[1] = sGreenTotal;
            greenDurations[2] = eGreenTotal;
            greenDurations[0] = wGreenTotal;*/
            case 0: // West Green
                remainingTime = greenDurations[3]; // Batı
                break;
            case 1: // West to North Yellow
                remainingTime = YELLOW_DURATION;
                break;
            case 2: // North Green
                remainingTime = greenDurations[0]; // Kuzey
                break;
            case 3: // North to East Yellow
                remainingTime = YELLOW_DURATION;
                break;
            case 4: // East Green
                remainingTime = greenDurations[2]; // Doğu
                break;
            case 5: // East to South Yellow
                remainingTime = YELLOW_DURATION;
                break;
            case 6: // South Green
                remainingTime = greenDurations[1]; // Güney
                break;
            case 7: // South to West Yellow
                remainingTime = YELLOW_DURATION;
                break;
        }
    }

    // direction 0: North, 1: South, 2: East, 3: West
    // 0: W Green, 1: WN Yellow, 2: N Green, 3: NE Yellow, 4: E Green, 5: ES Yellow, 6: S Green, 7: SW Yellow
    private void moveVehiclesWithPriority(int direction, double deltaTime) {
        List<Vehicle> toRemove = new ArrayList<>();
        Vehicle prevVehicle = null;
        vehicles[direction].sort(Comparator.comparingDouble(Vehicle::getPosition).reversed());

        for (Vehicle vehicle : vehicles[direction]) {
            // Check if vehicle should stop (red or yellow and not past intersection)
            int phase = getCurrentPhase(); //Trafik ışıklarının hangi fazda olduğunu belirtir (0–3 arası).
            boolean isGreen = (direction == 3 && phase == 0) || (direction == 0 && phase == 2)|| (direction  == 2 && phase == 4)||(direction == 1 && phase == 6);
            boolean isYellow = (((direction==0)||(direction==3)) && phase == 1) || (((direction==0)||(direction==2)) && phase == 3)||(((direction==1)||(direction==2)) && phase == 5)||(((direction==1)||(direction==3)) && phase == 7);
            if (!isGreen && (isYellow && vehicle.getPosition() < -20)) {
                continue; // Stop if yellow and not past intersection//BENCE BREAK YAPILABİLİR
            }



            // Önceki araçla çarpışmayı önle
            if (prevVehicle != null) {
                double dx = prevVehicle.getActualX() - vehicle.getActualX();
                double dy = prevVehicle.getActualY() - vehicle.getActualY();
                double distance = Math.sqrt(dx * dx + dy * dy);

                double minDistance = prevVehicle.getType().equals("truck") ? 80 : 60;
                if (distance < minDistance) {
                    continue; // Çok yakın, ilerleme
                }
            }


            vehicle.move(deltaTime);
            if (vehicle.hasPassedIntersection()) {
                //toRemove.add(vehicle);
                vehicle.move(deltaTime);
            }
            prevVehicle = vehicle;
        }
        vehicles[direction].removeAll(toRemove); //Kavşağı geçen araçlar ana listeden çıkarılır.
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

    /* public void startSimulation() {
         isRunning = true;
         currentPhase = 0;
         remainingTime = greenDurations[currentPhase];
     }

     public void pauseSimulation() {
         isRunning = false;
     }*/
     /*public void startSimulation() {
        isRunning = true;
        currentPhase = 0;
        advancePhase(); // İlk fazı başlat
    }*/
    public void resumeSimulation() {
        if (!isRunning && remainingTime > 0) {
            isRunning = true;
        }
    }

    public void startSimulation() {
        calculateGreenDurations();
        isRunning = true;
        currentPhase = 0;
        remainingTime = greenDurations[currentPhase];
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