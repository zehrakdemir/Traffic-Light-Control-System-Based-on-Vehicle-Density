package com.example.traffic6.controller;

import com.example.traffic6.model.TrafficModel;
import com.example.traffic6.view.TrafficView;
import javafx.scene.control.TextField;

public class TrafficController {
    private TrafficModel model;
    private TrafficView view;

    public TrafficController(TrafficModel model, TrafficView view) {
        this.model = model;
        this.view = view;
        view.setController(this);
    }

    public void startSimulation() {
        try {
            TextField[] fields = view.getDensityFields();
            int north = Integer.parseInt(fields[0].getText());
            int south = Integer.parseInt(fields[1].getText());
            int east = Integer.parseInt(fields[2].getText());
            int west = Integer.parseInt(fields[3].getText());
            model.setVehicleCounts(north, south, east, west);
            model.startSimulation();
        } catch (NumberFormatException e) {
            // Handle invalid input
            model.generateRandomCounts();
            view.updateVehicleCounts(model.getVehicleCounts());
        }
    }

    public void pauseSimulation() {
        model.pauseSimulation();
    }

    public void resetSimulation() {
        model.resetSimulation();
        view.updateVehicleCounts(new int[]{0, 0, 0, 0});
        view.render();
    }

    public void generateRandomCounts() {
        model.generateRandomCounts();
        view.updateVehicleCounts(model.getVehicleCounts());
    }

    public void update(double deltaTime) {
        model.update(deltaTime);
    }

    public TrafficModel getModel() {
        return model;
    }
}