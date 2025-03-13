package org.example.junction;

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
}
