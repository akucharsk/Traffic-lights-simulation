package org.example;

import org.example.location.Vehicle;

import java.util.ArrayList;
import java.util.List;

public class StepStatus {
    private List<List<Vehicle>> stepStatuses = new ArrayList<>();

    public StepStatus(List<List<Vehicle>> stepStatuses) {
        this.stepStatuses = stepStatuses;
    }

    public List<List<Vehicle>> getStepStatuses() {
        return stepStatuses;
    }
}
