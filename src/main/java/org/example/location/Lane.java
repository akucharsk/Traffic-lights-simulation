package org.example.location;

public enum Lane {
    LEFT, MIDDLE, RIGHT;

    public static Lane appropriateLane(Direction startRoad, Direction endRoad) {
        if (endRoad == startRoad.right()) {
            return RIGHT;
        } else if (endRoad == startRoad.left()) {
            return LEFT;
        } else {
            return MIDDLE;
        }
    }

    public static Lane appropriateLane(String startRoad, String endRoad) {
        return appropriateLane(Direction.fromString(startRoad), Direction.fromString(endRoad));
    }
}
