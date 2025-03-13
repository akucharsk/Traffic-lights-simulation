package org.example.commands;

import org.example.junction.Direction;
import org.example.junction.Junction;
import org.example.junction.Vehicle;

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

    @Override
    public void setJunction(Junction junction) {
        this.junction = junction;
    }

    @Override
    public void execute() {
        Vehicle vehicle = new Vehicle(vehicleId);
        junction.addVehicle(vehicle, Direction.fromString(startRoad), Direction.fromString(endRoad));
    }

    @Override
    public boolean equals(Command command) {
        return false;
    }

    @Override
    public String toString() {
        return vehicleId + "," + startRoad + "," + endRoad;
    }

}
