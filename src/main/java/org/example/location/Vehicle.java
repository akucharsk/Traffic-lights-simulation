package org.example.location;

public class Vehicle {
    private String id;
    private String startRoad;
    private String endRoad;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setStartRoad(String startRoad) {
        this.startRoad = startRoad;
    }

    public void setEndRoad(String endRoad) {
        this.endRoad = endRoad;
    }

    public Vehicle() {}

    public Vehicle(String id) {
        this.id = id;
    }

    public Vehicle(String id, String startRoad, String endRoad) {
        this.id = id;
        this.startRoad = startRoad;
        this.endRoad = endRoad;
    }

    public Vehicle(String id, Direction startRoad, Direction endRoad) {
        this.id = id;
        this.startRoad = startRoad.name().toLowerCase();
        this.endRoad = endRoad.name().toLowerCase();
    }

    public String getStartRoad() {
        return startRoad;
    }

    public Direction getStartRoadDirection() {
        return Direction.fromString(startRoad);
    }

    public String getEndRoad() {
        return endRoad;
    }

    public Direction getEndRoadDirection() {
        return Direction.fromString(endRoad);
    }

    public String toString() {
        return this.id;
    }
}
