import org.example.junction.Direction;
import org.example.junction.Lane;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class TestLane {
    @Test
    public void testAppropriateLaneForRightTurn() {
        assertEquals(Lane.appropriateLane(Direction.NORTH, Direction.WEST), Lane.RIGHT);
        assertEquals(Lane.appropriateLane(Direction.WEST, Direction.SOUTH), Lane.RIGHT);
        assertEquals(Lane.appropriateLane(Direction.SOUTH, Direction.EAST), Lane.RIGHT);
        assertEquals(Lane.appropriateLane(Direction.EAST, Direction.NORTH), Lane.RIGHT);
    }

    @Test
    public void testAppropriateLaneForLeftTurn() {
        assertEquals(Lane.appropriateLane(Direction.NORTH, Direction.EAST), Lane.LEFT);
        assertEquals(Lane.appropriateLane(Direction.EAST, Direction.SOUTH), Lane.LEFT);
        assertEquals(Lane.appropriateLane(Direction.SOUTH, Direction.WEST), Lane.LEFT);
        assertEquals(Lane.appropriateLane(Direction.WEST, Direction.NORTH), Lane.LEFT);
    }

    @Test
    public void testAppropriateLaneForStraightLine() {
        assertEquals(Lane.appropriateLane(Direction.NORTH, Direction.SOUTH), Lane.MIDDLE);
        assertEquals(Lane.appropriateLane(Direction.SOUTH, Direction.NORTH), Lane.MIDDLE);
        assertEquals(Lane.appropriateLane(Direction.WEST, Direction.EAST), Lane.MIDDLE);
        assertEquals(Lane.appropriateLane(Direction.EAST, Direction.WEST), Lane.MIDDLE);
    }
}
