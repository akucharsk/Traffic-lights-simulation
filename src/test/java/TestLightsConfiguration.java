import org.example.location.*;
import org.junit.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class TestLightsConfiguration {

    private TrafficLightsConfiguration config;
    private Road north, south;

    private void setup() {
        north = new Road(Direction.NORTH);
        south = new Road(Direction.SOUTH);
        List<TrafficLights> lights = List.of(
                new TrafficLights(north, Lane.MIDDLE),
                new TrafficLights(north, Lane.RIGHT),
                new TrafficLights(south, Lane.MIDDLE),
                new TrafficLights(south, Lane.RIGHT)
        );
        config = new TrafficLightsConfiguration(lights);
        config.activateLights();
    }

    @Test
    public void testGettingWaitingVehicles() {
        setup();
        north.addVehicle(Lane.LEFT, new Vehicle("1"));
        north.addVehicle(Lane.LEFT, new Vehicle("1.5"));
        north.addVehicle(Lane.RIGHT, new Vehicle("2"));
        north.addVehicle(Lane.MIDDLE, new Vehicle("3"));
        north.addVehicle(Lane.MIDDLE, new Vehicle("4"));

        // The left lane isn't in the configuration -> vehicles 1 and 1.5 aren't counted
        assertEquals(3, config.getWaitingVehicles());
        assertEquals(2, config.getNonRightTurnVehicles());
    }

    @Test
    public void testGettingRedLightVehiclesWhenAnotherConfigurationHasTheSameRightTurnLights() {
        setup();
        Road east = new Road(Direction.EAST);
        Road west = new Road(Direction.WEST);

        // config: east, west -> left; north, south -> right
        TrafficLightsConfiguration auxConfig = new TrafficLightsConfiguration(List.of(
                config.getParallelLights().get(1), // north, right
                config.getParallelLights().get(3), // south, right
                new TrafficLights(east, Lane.LEFT),
                new TrafficLights(west, Lane.LEFT)
        ));

        north.addVehicle(Lane.RIGHT, new Vehicle("1"));
        south.addVehicle(Lane.RIGHT, new Vehicle("1.5"));
        south.addVehicle(Lane.RIGHT, new Vehicle("2"));
        east.addVehicle(Lane.LEFT, new Vehicle("3"));
        east.addVehicle(Lane.LEFT, new Vehicle("4"));
        west.addVehicle(Lane.LEFT, new Vehicle("5"));
        north.addVehicle(Lane.MIDDLE, new Vehicle("6"));
        south.addVehicle(Lane.MIDDLE, new Vehicle("7"));

        assertEquals(6, auxConfig.getWaitingVehicles());
        assertEquals("Expected 3 vehicles with a red light",
                3, auxConfig.getRedLightVehicles());
        assertEquals("Expected 3 non right turn vehicles",
                3, auxConfig.getNonRightTurnVehicles());

        config.deactivateLights();
        auxConfig.activateLights();

        assertEquals("Expected 2 vehicles with a red light",
                2, config.getRedLightVehicles());
        assertEquals("Expected 2 non right turn vehicles",
                2, config.getNonRightTurnVehicles());
        assertEquals(5, config.getWaitingVehicles());
    }

    @Test
    public void testMovingVehiclesFromTwoOppositeRoads() {
        setup();
        for (int i = 0; i < 10; i++) {
            north.addVehicle(Lane.MIDDLE, new Vehicle(Integer.toString(2 * i + 1)));
            south.addVehicle(Lane.MIDDLE, new Vehicle(Integer.toString(2 * i)));
        }
        Set<String> movedVehicleIDs = config.moveVehicles().stream().map(Vehicle::getId).collect(Collectors.toSet());
        assertEquals("Expected 2 vehicles to leave",
                2, movedVehicleIDs.size());
        assertTrue("Expected vehicle 1 to leave",
                movedVehicleIDs.contains("1"));
        assertTrue("Expected vehicle 0 to leave",
                movedVehicleIDs.contains("0"));
        assertEquals("Expected 9 vehicles on the north lane after moving",
                9, north.getVehicles(Lane.MIDDLE).size());
        assertEquals("Expected to have 1 active step",
                1, config.getActiveSteps());

        movedVehicleIDs = config.moveVehicles().stream().map(Vehicle::getId).collect(Collectors.toSet());
        assertEquals("Expected 2 vehicles to leave (take 2)",
                2, movedVehicleIDs.size());
        assertTrue("Expected vehicle 3 to leave",
                movedVehicleIDs.contains("3"));
        assertTrue("Expected vehicle 2 to leave",
                movedVehicleIDs.contains("2"));
        assertEquals("Expected 8 vehicles on the south middle lane after moving",
                8, south.getVehicles(Lane.MIDDLE).size());
    }

    @Test
    public void testMovingVehiclesFromThreeAndFourRoads() {
        setup();
        north.addVehicle(Lane.RIGHT, new Vehicle("1"));
        north.addVehicle(Lane.MIDDLE,  new Vehicle("2"));
        south.addVehicle(Lane.RIGHT, new Vehicle("3"));

        assertEquals(3, config.moveVehicles().size());
        assertEquals(0, config.getWaitingVehicles());

        north.addVehicle(Lane.RIGHT, new Vehicle("1"));
        north.addVehicle(Lane.MIDDLE,  new Vehicle("2"));
        south.addVehicle(Lane.RIGHT, new Vehicle("3"));
        south.addVehicle(Lane.MIDDLE,  new Vehicle("4"));
        south.addVehicle(Lane.RIGHT, new Vehicle("5"));

        assertEquals(4, config.moveVehicles().size());
        assertEquals(1, config.getWaitingVehicles());
    }

    @Test
    public void testGettingStepsTillEmpty() {
        setup();
        north.addVehicle(Lane.RIGHT, new Vehicle("1"));
        north.addVehicle(Lane.MIDDLE,  new Vehicle("2"));
        south.addVehicle(Lane.RIGHT, new Vehicle("3"));
        south.addVehicle(Lane.MIDDLE,  new Vehicle("4"));

        assertEquals(1, config.getStepsTillEmpty());
        north.addVehicle(Lane.RIGHT, new Vehicle("5"));

        assertEquals("Expected 2 steps till empty",
                2, config.getStepsTillEmpty());

        south.addVehicle(Lane.RIGHT, new Vehicle("6"));
        assertEquals("Expected 2 steps till empty after adding vehicle 6",
                2, config.getStepsTillEmpty());

        north.addVehicle(Lane.RIGHT, new Vehicle("7"));
        assertEquals(3, config.getStepsTillEmpty());
    }

    @Test
    public void testFiveGuaranteedPrioritySteps() {
        setup();
        for (int i = 0; i <= TrafficLightsConfiguration.GUARANTEED_STEPS; i++) {
            north.addVehicle(Lane.MIDDLE, new Vehicle(Integer.toString(i)));
        }
        for (int i = 0; i < TrafficLightsConfiguration.GUARANTEED_STEPS; i++) {
            assertEquals(Double.POSITIVE_INFINITY, config.getPriority());
            config.moveVehicles();
        }
        assertEquals(1.0, config.getPriority(), 1e-4);
        config.moveVehicles();
        assertEquals(Double.NEGATIVE_INFINITY, config.getPriority());
    }

    @Test
    public void testNegativeInfinityAfterMaximumSteps() {
        setup();
        for (int i = 0; i < TrafficLightsConfiguration.MAXIMUM_ACTIVE_STEPS + 10; i++) {
            north.addVehicle(Lane.MIDDLE, new Vehicle(Integer.toString(i)));
            north.addVehicle(Lane.RIGHT, new Vehicle(Integer.toString(i)));
        }
        for (int i = 0; i < TrafficLightsConfiguration.MAXIMUM_ACTIVE_STEPS; i++) {
            config.moveVehicles();
        }
        assertEquals(Double.NEGATIVE_INFINITY, config.getPriority());
    }

    @Test
    public void testMetricGivenTwoLanesHavingTheSameAmountOfVehicles() {
        setup();
        int steps = TrafficLightsConfiguration.GUARANTEED_STEPS;
        for (int i = 0; i < steps * 2; i++) {
            north.addVehicle(Lane.MIDDLE, new Vehicle(Integer.toString(i)));
            north.addVehicle(Lane.RIGHT, new Vehicle(Integer.toString(i)));
        }
        for (int i = 0; i < steps; i++)
            config.moveVehicles();

        // 10 / (5 + 2 * 5 - 10)
        assertEquals(2.0, config.getPriority(), 1e-4);
        config.moveVehicles();

        // 8 / (4 + 2 * 6 - 10)
        assertEquals(8. / 6, config.getPriority(), 1e-4);
        config.moveVehicles();

        // 6 / (3 + 2 * 7 - 10)
        assertEquals(6. / 7, config.getPriority(), 1e-4);
    }

    @Test
    public void testMetricGivenOneLaneContainingVehicles() {
        setup();
        int steps = TrafficLightsConfiguration.GUARANTEED_STEPS;
        for (int i = 0; i < steps * 2; i++) {
            north.addVehicle(Lane.MIDDLE, new Vehicle(Integer.toString(i)));
        }
        for (int i = 0; i < steps; i++)
            config.moveVehicles();

        // 5 / (5 + 2 * 5 - 10)
        assertEquals(1.0, config.getPriority(), 1e-4);
        config.moveVehicles();

        // 4 / (4 + 2 * 6 - 10)
        assertEquals(4. / 6, config.getPriority(), 1e-4);
    }

    @Test
    public void testMetricGivenFourParallelLinesContainingVehicles() {
        setup();
        int steps = TrafficLightsConfiguration.GUARANTEED_STEPS;
        for (int i = 0; i < steps * 2; i++) {
            north.addVehicle(Lane.MIDDLE, new Vehicle(Integer.toString(i)));
            north.addVehicle(Lane.RIGHT, new Vehicle(Integer.toString(i)));
            south.addVehicle(Lane.MIDDLE, new Vehicle(Integer.toString(i)));
            south.addVehicle(Lane.RIGHT, new Vehicle(Integer.toString(i)));
        }
        for (int i = 0; i < steps; i++)
            config.moveVehicles();

        // 20 / (5 + 2 * 5 - 10)
        assertEquals(4.0, config.getPriority(), 1e-4);
        config.moveVehicles();

        // 16 / (4 + 2 * 6 - 10)
        assertEquals(16. / 6, config.getPriority(), 1e-4);
        config.moveVehicles();

        // 12 / (3 + 2 * 7 - 10)
        assertEquals(12. / 7, config.getPriority(), 1e-4);

    }

    @Test
    public void testMetricForInactiveConfiguration() {
        setup();
        config.deactivateLights();
        north.addVehicle(Lane.RIGHT, new Vehicle("1"));
        north.addVehicle(Lane.MIDDLE, new Vehicle("2"));
        // 2 / 1
        assertEquals(2.0, config.getPriority(), 1e-4);

        north.addVehicle(Lane.MIDDLE, new Vehicle("3"));
        north.addVehicle(Lane.RIGHT, new Vehicle("4"));
        south.addVehicle(Lane.MIDDLE, new Vehicle("5"));
        south.addVehicle(Lane.RIGHT, new Vehicle("6"));

        // 6 / 2
        assertEquals(3.0, config.getPriority(), 1e-4);
    }

    @Test
    public void testContainsVehicle() {

    }
}
