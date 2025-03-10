package org.example;

public class Vehicle {
    private final String id;

    public Vehicle(String id) {
        this.id = id;
    }

    public boolean equals(Vehicle other) {
        return this.id.equals(other.id);
    }

    public String toString() {
        return this.id;
    }
}
