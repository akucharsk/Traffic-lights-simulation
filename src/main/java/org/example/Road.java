package org.example;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Road {
    private final Map<Lane, List<Vehicle>> vehicles = new HashMap<>();
    private final Map<Lane, TrafficLights> lights = new HashMap<>();

    public Road() {
        vehicles.put(Lane.LEFT, new LinkedList<>());
        vehicles.put(Lane.MIDDLE, new LinkedList<>());
        vehicles.put(Lane.RIGHT, new LinkedList<>());

        lights.put(Lane.LEFT, new TrafficLights(this, Lane.LEFT));
        lights.put(Lane.MIDDLE, new TrafficLights(this, Lane.MIDDLE));
        lights.put(Lane.RIGHT, new TrafficLights(this, Lane.RIGHT));
    }

    public void addVehicle(Lane lane, Vehicle vehicle) {
        vehicles.get(lane).add(vehicle);
    }

    public List<Vehicle> getVehicles(Lane lane) {
        return vehicles.get(lane);
    }

    public Vehicle removeVehicle(Lane lane) {
        if (vehicles.get(lane).isEmpty())
            return null;
        return vehicles.get(lane).removeFirst();
    }

    public TrafficLights getLight(Lane lane) {
        return lights.get(lane);
    }

    public String toString() {
        return vehicles.toString();
    }
}
