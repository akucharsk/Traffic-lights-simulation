package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class TrafficLightsConfiguration {
    private int activeSteps = 0;
    private int passiveSteps = 0;
    private final List<TrafficLights> parallelLights = new ArrayList<>();
    private boolean isActive = false;

    public TrafficLightsConfiguration(List<TrafficLights> lights) {
        parallelLights.addAll(lights);
    }

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
        if (isActive && waitingVehicles > 0 && activeSteps < 5) {
            return Double.POSITIVE_INFINITY;
        }

        if (waitingVehicles == 0) {
            return Double.NEGATIVE_INFINITY;
        }

        if (!isActive) {
            int redLightVehicles = getRedLightVehicles();
            return redLightVehicles == 0 ?
                    Double.NEGATIVE_INFINITY :
                    (double) redLightVehicles / stepsTillEmpty;
        }
        return (double) waitingVehicles / (stepsTillEmpty + activeSteps - 5);
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

    public int getWaitingVehicles() {
        return getVehicleCounts().reduce(0, Integer::sum);
    }

    public int getStepsTillEmpty() {
        return getVehicleCounts().reduce(0, Integer::max);
    }
}
