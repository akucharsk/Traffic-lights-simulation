package org.example;

public class TrafficLights {

    private boolean isGreen;
    private final Road road;
    private final Lane lane;

    public TrafficLights(Road road, Lane lane) {
        this.isGreen = false;
        this.road = road;
        this.lane = lane;
    }

    public boolean isGreen() {
        return isGreen;
    }

    public boolean isRed() {return !isGreen;}

    public void activate() {
        this.isGreen = true;
    }

    public void deactivate() {
        this.isGreen = false;
    }

    public Road getRoad() {
        return road;
    }

    public Lane getLane() {
        return lane;
    }
}
