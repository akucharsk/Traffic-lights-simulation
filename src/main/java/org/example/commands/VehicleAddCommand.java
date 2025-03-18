package org.example.commands;

import org.example.location.Junction;
import org.example.location.Vehicle;

public class VehicleAddCommand implements Command{
    private final String vehicleId;
    private final String startRoad;
    private final String endRoad;
    private Junction junction;

    public VehicleAddCommand(String vehicleId, String startRoad, String endRoad) {
        super();
        this.vehicleId = vehicleId;
        this.startRoad = startRoad;
        this.endRoad = endRoad;
    }

    // accepted format: VADD;<id>;<start-road>;<end-road>
    public static VehicleAddCommand parse(String line) {
        if (!line.startsWith("VADD;"))
            return null;
        String[] elements = line.split(";");
        if (elements.length < 4) {
            return null;
        }
        return new VehicleAddCommand(elements[1], elements[2], elements[3]);
    }

    @Override
    public void setJunction(Junction junction) {
        this.junction = junction;
    }

    @Override
    public void execute() {
        Vehicle vehicle = new Vehicle(vehicleId, startRoad, endRoad);
        junction.addVehicle(vehicle);
    }

    @Override
    public boolean equals(Command command) {
        return false;
    }

    @Override
    public String toString() {
        return "addVehicle," + vehicleId + "," + startRoad + "," + endRoad;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public String getStartRoad() {
        return startRoad;
    }

    public String getEndRoad() {
        return endRoad;
    }

}
