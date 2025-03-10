package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class TrafficLightsConfiguration {
    private double priority = 0;
    private int activeSteps = 0;
    private int passiveSteps = 0;
    private final List<TrafficLights> parallelLights = new ArrayList<>();
    private boolean isActive = false;
    private int expectedStepsTillEmpty = 0;

    public TrafficLightsConfiguration(List<TrafficLights> lights) {
        parallelLights.addAll(lights);
        expectedStepsTillEmpty = getStepsTillEmpty();
    }

    public int getActiveSteps() {
        return activeSteps;
    }

    public int getPassiveSteps() {
        return passiveSteps;
    }

    public void activateLights() {
        isActive = true;
        parallelLights.forEach(TrafficLights::activate);
    }

    public void deactivateLights() {
        isActive = false;
        parallelLights.forEach(TrafficLights::deactivate);
    }

    public double getPriority() {
        int waitingVehicles = getWaitingVehicles();
        int stepsTillEmpty = getStepsTillEmpty();
        if (isActive && waitingVehicles > 0 && activeSteps < 5) {
            return Double.POSITIVE_INFINITY;
        }

        if (waitingVehicles == 0) {
            return Double.NEGATIVE_INFINITY;
        }

        if (!isActive) {
            return (double) waitingVehicles / (stepsTillEmpty + 1);
        }

        return (double) waitingVehicles / (activeSteps - 5);
    }

    public List<TrafficLights> getParallelLights() {
        return parallelLights;
    }

    public void registerActiveStep() {
        activeSteps++;
        passiveSteps = 0;
    }

    public void registerPassiveStep() {
        passiveSteps++;
        activeSteps = 0;
    }

    private Stream<Integer> getVehicleCounts() {
        return parallelLights
                .stream()
                .map(lights -> lights.getRoad().getVehicles(lights.getLane()).size());
    }

    public int getWaitingVehicles() {
        return getVehicleCounts().reduce(0, Integer::sum);
    }

    public int getStepsTillEmpty() {
        return getVehicleCounts().reduce(0, Integer::max);
    }
}
