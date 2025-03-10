package org.example;

import java.util.ArrayList;
import java.util.List;

public class StepCommand implements Command{

    private Junction junction;
    private String outputFilename = "output.json";
    private static List<List<Vehicle>> departedVehicles = new ArrayList<>();

    public static List<List<Vehicle>> getDepartedVehicles() {
        return departedVehicles;
    }

    @Override
    public void setJunction(Junction junction) {
        this.junction = junction;
    }

    @Override
    public void execute() {
        departedVehicles.add(junction.makeStep());
    }

    @Override
    public boolean equals(Command command) {
        return false;
    }

    @Override
    public String toString() {
        return "";
    }

}
