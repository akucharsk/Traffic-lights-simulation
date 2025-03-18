package org.example.location;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TrafficLightsConfiguration {
    private int activeSteps = 0;
    private final List<TrafficLights> parallelLights = new ArrayList<>();
    private boolean isActive = false;

    public static final int MAXIMUM_ACTIVE_STEPS = 20;
    public static final int GUARANTEED_STEPS = 5;

    public TrafficLightsConfiguration(List<TrafficLights> lights) {
        parallelLights.addAll(lights);
    }

    public int getActiveSteps() {return activeSteps;}

    public void activateLights() {
        isActive = true;
        parallelLights.forEach(TrafficLights::activate);
    }

    public void deactivateLights() {
        isActive = false;
        activeSteps = 0;
        parallelLights.forEach(TrafficLights::deactivate);
    }

    public double getPriority() {
        int waitingVehicles = getWaitingVehicles();
        int stepsTillEmpty = getStepsTillEmpty();
        if (isActive && waitingVehicles > 0 && activeSteps < GUARANTEED_STEPS) {
            return Double.POSITIVE_INFINITY;
        }

        if (waitingVehicles == 0 || activeSteps >= MAXIMUM_ACTIVE_STEPS) {
            return Double.NEGATIVE_INFINITY;
        }

        if (!isActive) {
            int redLightVehicles = getRedLightVehicles();
            return redLightVehicles == 0 ?
                    Double.NEGATIVE_INFINITY :
                    (double) redLightVehicles / stepsTillEmpty;
        }
        return (double) waitingVehicles / (stepsTillEmpty + 2 * activeSteps - 10);
    }

    public List<Vehicle> moveVehicles() {
        List<Vehicle> departedVehicles = new ArrayList<>();
        for (TrafficLights lights : getParallelLights()) {
            Road road = lights.getRoad();
            Lane lane = lights.getLane();

            Vehicle departed = road.removeVehicle(lane);
            if (departed != null)
                departedVehicles.add(departed);
        }
        registerActiveStep();
        return departedVehicles;
    }

    public boolean containsVehicle(Vehicle vehicle) {
        return parallelLights
                .stream()
                .anyMatch(light -> light.getRoad().getVehicles(light.getLane()).contains(vehicle));
    }

    public List<TrafficLights> getParallelLights() {
        return parallelLights;
    }

    public void registerActiveStep() {
        activeSteps++;
    }

    private Stream<Integer> getVehicleCounts() {
        return parallelLights
                .stream()
                .map(lights -> lights.getRoad().getVehicles(lights.getLane()).size());
    }

    public int getRedLightVehicles() {
        return parallelLights
                .stream()
                .filter(TrafficLights::isRed)
                .map(lights -> lights.getRoad().getVehicles(lights.getLane()).size())
                .reduce(0, Integer::sum);
    }

    public Set<TrafficLights> getRightTurnLights() {
        return parallelLights
                .stream()
                .filter(lights -> lights.getLane() == Lane.RIGHT)
                .collect(Collectors.toSet());
    }

    public int getNonRightTurnVehicles() {
        return parallelLights
                .stream()
                .filter(lights -> lights.getLane() != Lane.RIGHT)
                .map(lights -> lights.getRoad().getVehicles(lights.getLane()).size())
                .reduce(0, Integer::sum);
    }

    public int getWaitingVehicles() {
        return getVehicleCounts().reduce(0, Integer::sum);
    }

    public int getStepsTillEmpty() {
        return getVehicleCounts().reduce(0, Integer::max);
    }
}
