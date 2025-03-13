package org.example.junction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Junction {
    private final Map<Direction, Road> roads = new HashMap<>();
    private final List<TrafficLightsConfiguration> configurations = new ArrayList<>();
    private int configurationIdx = 0;
    private boolean lightsOnDemand = true;

    public static final int NORTH_SOUTH_STRAIGHT_LINE = 0;
    public static final int EAST_WEST_STRAIGHT_LINE = 1;
    public static final int NORTH_SOUTH_LEFT_TURN = 2;
    public static final int EAST_WEST_LEFT_TURN = 3;

    public Junction() {
        roads.put(Direction.NORTH, new Road());
        roads.put(Direction.SOUTH, new Road());
        roads.put(Direction.EAST, new Road());
        roads.put(Direction.WEST, new Road());

        configurations.add(new TrafficLightsConfiguration(List.of(
                roads.get(Direction.NORTH).getLight(Lane.MIDDLE),
                roads.get(Direction.NORTH).getLight(Lane.RIGHT),
                roads.get(Direction.SOUTH).getLight(Lane.MIDDLE),
                roads.get(Direction.SOUTH).getLight(Lane.RIGHT)
        )));
        configurations.add(new TrafficLightsConfiguration(List.of(
                roads.get(Direction.WEST).getLight(Lane.MIDDLE),
                roads.get(Direction.WEST).getLight(Lane.RIGHT),
                roads.get(Direction.EAST).getLight(Lane.MIDDLE),
                roads.get(Direction.EAST).getLight(Lane.RIGHT)
        )));
        configurations.add(new TrafficLightsConfiguration(List.of(
                roads.get(Direction.NORTH).getLight(Lane.LEFT),
                roads.get(Direction.SOUTH).getLight(Lane.LEFT),
                roads.get(Direction.EAST).getLight(Lane.RIGHT),
                roads.get(Direction.WEST).getLight(Lane.RIGHT)
        )));
        configurations.add(new TrafficLightsConfiguration(List.of(
                roads.get(Direction.EAST).getLight(Lane.LEFT),
                roads.get(Direction.WEST).getLight(Lane.LEFT),
                roads.get(Direction.NORTH).getLight(Lane.RIGHT),
                roads.get(Direction.SOUTH).getLight(Lane.RIGHT)
        )));
    }

    public void addVehicle(Vehicle vehicle, Direction from, Direction to) {
        Road road = roads.get(from);
        road.addVehicle(Lane.appropriateLane(from, to), vehicle);

        if (lightsOnDemand) {
            lightsOnDemand = false;
            for (int i = 0; i < configurations.size(); i++) {
                if (configurations.get(i).getWaitingVehicles() > 0) {
                    configurationIdx = i;
                    break;
                }
            }
        }
    }

    public Road getRoad(Direction direction) {
        return roads.get(direction);
    }

    public List<Vehicle> makeStep() {
        List<Vehicle> departedVehicles = new ArrayList<>();
        for (TrafficLights lights : configurations.get(configurationIdx).getParallelLights()) {
            Road road = lights.getRoad();
            Lane lane = lights.getLane();

            Vehicle departed = road.removeVehicle(lane);
            if (departed != null)
                departedVehicles.add(departed);
        }
        configurations.get(configurationIdx).registerActiveStep();

        double priority = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < configurations.size(); i++) {
            double configPriority = configurations.get(i).getPriority();
            if (configPriority > priority) {
                priority = configPriority;
                configurationIdx = i;
            }
        }

        if (priority == Double.NEGATIVE_INFINITY) {
            lightsOnDemand = true;
        }

        return departedVehicles;
    }

    public boolean lightsOnDemand() {
        return lightsOnDemand;
    }

    public int getConfigurationIdx() {
        return configurationIdx;
    }

    public String toString() {
        return roads.toString();
    }
}
