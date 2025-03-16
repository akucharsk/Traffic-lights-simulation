package org.example;

public enum Direction {
    NORTH, SOUTH, EAST, WEST;

    public Direction right() {
        return switch (this) {
            case NORTH -> WEST;
            case WEST -> SOUTH;
            case SOUTH -> EAST;
            case EAST -> NORTH;
        };
    }

    public Direction opposite() {
        return switch (this) {
            case NORTH -> SOUTH;
            case SOUTH -> NORTH;
            case EAST -> WEST;
            case WEST -> EAST;
        };
    }

    public Direction left() {
        return right().opposite();
    }

    static Direction fromString(String direction) {
        return switch (direction.toLowerCase().strip()) {
            case "north" -> NORTH;
            case "south" -> SOUTH;
            case "east" -> EAST;
            case "west" -> WEST;
            default -> throw new IllegalStateException("Unexpected value: " + direction);
        };
    }
}
