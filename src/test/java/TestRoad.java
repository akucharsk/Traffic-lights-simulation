import org.example.location.Direction;
import org.example.location.Lane;
import org.example.location.Road;
import org.example.location.Vehicle;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;

public class TestRoad {

    private Road road;

    private void setup() {
        road = new Road(Direction.NORTH);
    }

    @Test
    public void testAddVehicleToAllLanes() {
        setup();
        road.addVehicle(Lane.LEFT, new Vehicle());
        road.addVehicle(Lane.RIGHT, new Vehicle());
        road.addVehicle(Lane.LEFT, new Vehicle());
        road.addVehicle(Lane.MIDDLE, new Vehicle());

        assertEquals(2, road.getVehicles(Lane.LEFT).size());
        assertEquals(1, road.getVehicles(Lane.RIGHT).size());
        assertEquals(1, road.getVehicles(Lane.MIDDLE).size());
    }

    @Test
    public void testRemoveVehicleFromAllLanes() {
        setup();
        road.addVehicle(Lane.LEFT, new Vehicle("1"));
        road.addVehicle(Lane.RIGHT, new Vehicle("2"));
        road.addVehicle(Lane.LEFT, new Vehicle("3"));
        road.addVehicle(Lane.RIGHT, new Vehicle("4"));
        road.addVehicle(Lane.LEFT, new Vehicle("5"));
        road.addVehicle(Lane.MIDDLE,  new Vehicle("6"));

        Vehicle vehicle = road.removeVehicle(Lane.LEFT);
        assertEquals("1", vehicle.getId());
        assertEquals(2, road.getVehicles(Lane.LEFT).size());

        vehicle = road.removeVehicle(Lane.RIGHT);
        assertEquals("2", vehicle.getId());
        assertEquals(1, road.getVehicles(Lane.RIGHT).size());

        vehicle = road.removeVehicle(Lane.MIDDLE);
        assertEquals("6", vehicle.getId());
        assertEquals(0, road.getVehicles(Lane.MIDDLE).size());

        assertNull(road.removeVehicle(Lane.MIDDLE));
    }
}
