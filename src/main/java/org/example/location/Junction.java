package org.example.location;

import java.util.*;

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
        roads.put(Direction.NORTH, new Road(Direction.NORTH));
        roads.put(Direction.SOUTH, new Road(Direction.SOUTH));
        roads.put(Direction.EAST, new Road(Direction.EAST));
        roads.put(Direction.WEST, new Road(Direction.WEST));

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

    public void addVehicle(Vehicle vehicle) {
        Direction from = vehicle.getStartRoadDirection();
        Direction to = vehicle.getEndRoadDirection();
        Road road = roads.get(from);
        Lane lane = Lane.appropriateLane(from, to);
        road.addVehicle(lane, vehicle);

        if (!lightsOnDemand) {
            return;
        }
        configurations.get(configurationIdx).deactivateLights();
        lightsOnDemand = false;
        for (int i = 0; i < configurations.size(); i++) {
            if (configurations.get(i).getWaitingVehicles() > 0) {
                configurationIdx = i;
                configurations.get(i).activateLights();
                System.out.println(configurationIdx);
                break;
            }
        }
    }

    public Road getRoad(Direction direction) {
        return roads.get(direction);
    }

    public List<Vehicle> makeStep() {
        TrafficLightsConfiguration config = configurations.get(configurationIdx);
        List<Vehicle> departedVehicles = config.moveVehicles();

        double priority = config.getPriority();

        int firstLiveConfigIdx = -1;
        int bestPriorityIdx = configurationIdx;
        int i = (configurationIdx + 1) % configurations.size();
        while (i != configurationIdx) {
            if (configurations.get(i).getRedLightVehicles() == 0) {
                i = (i + 1) % configurations.size();
                continue;
            }
            double configPriority = configurations.get(i).getPriority();
            if (firstLiveConfigIdx < 0 && configPriority > Double.NEGATIVE_INFINITY)
                firstLiveConfigIdx = i;

            if (configPriority > priority) {
                priority = configPriority;
                bestPriorityIdx = i;
            }
            i = (i + 1) % configurations.size();
        }
        if (firstLiveConfigIdx < 0 && config.getWaitingVehicles() > 0)
            return departedVehicles;

        if (firstLiveConfigIdx < 0) {
            lightsOnDemand = true;
            return departedVehicles;
        }
        if (bestPriorityIdx == configurationIdx)
            return departedVehicles;
        int prev = configurationIdx;
        configurationIdx = firstLiveConfigIdx;
        configurationIdx = getPossibleUpgrade();
        if (prev == configurationIdx) {
            return departedVehicles;
        }
        config.deactivateLights();
        config = configurations.get(configurationIdx);
        config.activateLights();

        return departedVehicles;
    }

    private int getPossibleUpgrade() {
        TrafficLightsConfiguration config = configurations.get(configurationIdx);
        Set<TrafficLights> rightTurnLights = config.getRightTurnLights();
        int nonRightTurners = config.getNonRightTurnVehicles();
        if (nonRightTurners > 0)
            return configurationIdx;
        int i = (configurationIdx + 1) % configurations.size();
        while (i != configurationIdx) {
            nonRightTurners = configurations.get(i).getNonRightTurnVehicles();
            Set<TrafficLights> otherRightTurnLights =
                    configurations.get(i).getRightTurnLights();
            if (!rightTurnLights.containsAll(otherRightTurnLights) && nonRightTurners > 0)
                return configurationIdx;
            if (nonRightTurners > 0)
                return i;
            i = (i + 1) % configurations.size();
        }
        return configurationIdx;
    }

    public boolean isEmpty() {
        return configurations
                .stream()
                .allMatch(config -> config.getWaitingVehicles() == 0);
    }

    public TrafficLightsConfiguration getActiveConfig() {
        return configurations.get(configurationIdx);
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
