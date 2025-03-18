import org.example.location.*;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class TestLightsConfiguration {

    private TrafficLightsConfiguration config;
    private Road road;

    private void setup() {
        road = new Road(Direction.NORTH);
        List<TrafficLights> lights = List.of(
                new TrafficLights(road, Lane.LEFT),
                new TrafficLights(road, Lane.MIDDLE),
                new TrafficLights(road, Lane.RIGHT)
        );
        config = new TrafficLightsConfiguration(lights);
        config.activateLights();
    }

    @Test
    public void testGettingWaitingVehicles() {
        setup();
        road.addVehicle(Lane.LEFT, new Vehicle("1", "north", "east"));
        road.addVehicle(Lane.LEFT, new Vehicle("1.5", "north", "east"));
        road.addVehicle(Lane.RIGHT, new Vehicle("2", "north", "west"));
        config.activateLights();
        assertEquals(3, config.getWaitingVehicles());
        assertEquals(2, config.getNonRightTurnVehicles());
    }

    @Test
    public void testMovingVehicles() {
        setup();
        for (int i = 0; i < 10; i++) {
            road.addVehicle(Lane.LEFT, new Vehicle(Integer.toString(2 * i + 1)));
            road.addVehicle(Lane.LEFT, new Vehicle(Integer.toString(2 * i)));
        }
    }

    @Test
    public void testFiveGuaranteedPrioritySteps() {
        setup();
        for (int i = 0; i <= TrafficLightsConfiguration.GUARANTEED_STEPS; i++) {
            road.addVehicle(Lane.LEFT, new Vehicle(Integer.toString(i)));
        }
        for (int i = 0; i < TrafficLightsConfiguration.GUARANTEED_STEPS; i++) {
            assertEquals(Double.POSITIVE_INFINITY, config.getPriority());
            config.registerActiveStep();
            config.moveVehicles();
        }
        assertEquals(1.0, config.getPriority(), 1e-4);
    }
}
