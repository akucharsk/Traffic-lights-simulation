package org.example;

public enum Direction {
    NORTH, SOUTH, EAST, WEST;

    public Direction right() {
        return switch (this) {
            case NORTH:
                yield WEST;
            case WEST:
                yield SOUTH;
            case SOUTH:
                yield EAST;
            case EAST:
                yield NORTH;
        };
    }

    public Direction opposite() {
        return switch (this) {
            case NORTH:
                yield SOUTH;
            case SOUTH:
                yield NORTH;
            case EAST:
                yield WEST;
            case WEST:
                yield EAST;
        };
    }

    public Direction left() {
        return right().opposite();
    }

    static Direction fromString(String direction) {
        return switch (direction) {
            case "north":
                yield NORTH;
            case "south":
                yield SOUTH;
            case "east":
                yield EAST;
            case "west":
                yield WEST;
            default:
                throw new IllegalStateException("Unexpected value: " + direction);
        };
    }
}
