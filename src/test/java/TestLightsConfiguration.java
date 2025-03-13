import org.example.junction.*;
import org.junit.Test;

import java.util.List;

public class TestLightsConfiguration {

    private TrafficLightsConfiguration config;
    private Road road;

    private void setup() {
        road = new Road();
        List<TrafficLights> lights = List.of(
                new TrafficLights(road, Lane.LEFT),
                new TrafficLights(road, Lane.MIDDLE),
                new TrafficLights(road, Lane.RIGHT)
        );
        config = new TrafficLightsConfiguration(lights);
    }

    @Test
    public void testGettingWaitingVehicles() {
        setup();
        road.addVehicle(Lane.LEFT, new Vehicle("1"));
    }
}
