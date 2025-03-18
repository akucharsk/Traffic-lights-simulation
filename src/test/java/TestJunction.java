import org.example.commands.Command;
import org.example.commands.StepCommand;
import org.example.commands.VehicleAddCommand;
import org.example.location.*;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class TestJunction {

    private Junction junction;

    private void setup() {
        junction = new Junction();
    }

    @Test
    public void testJunctionAfterAddVehicleCommand() {
        setup();
        VehicleAddCommand command = new VehicleAddCommand("BMW", "north", "south");
        command.setJunction(junction);
        command.execute();
        Road road = junction.getRoad(Direction.NORTH);
        assertEquals(1, road.getVehicles(Lane.MIDDLE).size());
        assertEquals(0, road.getVehicles(Lane.RIGHT).size());
        assertEquals(0, road.getVehicles(Lane.LEFT).size());

        command = new VehicleAddCommand("Audi", "east", "north");
        command.setJunction(junction);
        command.execute();
        road = junction.getRoad(Direction.EAST);
        assertEquals(1, road.getVehicles(Lane.RIGHT).size());
        assertEquals(0, road.getVehicles(Lane.LEFT).size());
        assertEquals(0, road.getVehicles(Lane.MIDDLE).size());
    }

    @Test
    public void testJunctionAfterStepCommand_GivenTwoVehiclesInOneLightsConfiguration() {
        setup();
        List<VehicleAddCommand> addCommands = List.of(
                new VehicleAddCommand("BMW", "north", "south"),
                new VehicleAddCommand("Audi", "south", "east"),
                new VehicleAddCommand("Mercedes", "south", "north")
        );
        addCommands.forEach(command -> {
            command.setJunction(junction);
            command.execute();
        });

        assertEquals(junction.getConfigurationIdx(), Junction.NORTH_SOUTH_STRAIGHT_LINE);

        StepCommand command = new StepCommand();
        command.setJunction(junction);
        command.execute();
        assertTrue(junction.lightsOnDemand());
        assertEquals(junction.getConfigurationIdx(), Junction.NORTH_SOUTH_STRAIGHT_LINE);

    }

    @Test
    public void testJunctionAfterStepCommand_GivenTwoVehiclesOnCollidingConfigurations() {
        setup();
        List<VehicleAddCommand> addCommands = List.of(
                new VehicleAddCommand("BMW", "north", "east"),
                new VehicleAddCommand("Audi", "south", "north")
        );
        addCommands.forEach(command -> {
            command.setJunction(junction);
            command.execute();
        });

        assertEquals(junction.getConfigurationIdx(), Junction.NORTH_SOUTH_LEFT_TURN);
        StepCommand command = new StepCommand();
        command.setJunction(junction);
        command.execute();

        assertEquals(junction.getConfigurationIdx(), Junction.NORTH_SOUTH_STRAIGHT_LINE);
        command = new StepCommand();
        command.setJunction(junction);
        command.execute();

        assertTrue(junction.lightsOnDemand());
        assertEquals(junction.getConfigurationIdx(), Junction.NORTH_SOUTH_STRAIGHT_LINE);
    }

    @Test
    public void testJunctionAfterStepCommands_GivenTenVehiclesOnOneConfigurationAndZeroOnOthers() {
        setup();
        for (int i = 0; i < 10; i++) {
            Command command = new VehicleAddCommand(Integer.toString(i), "east", "west");
            command.setJunction(junction);
            command.execute();
        }

        assertEquals(junction.getConfigurationIdx(), Junction.EAST_WEST_STRAIGHT_LINE);

        for (int i = 0; i < 10; i++) {
            Command command = new StepCommand();
            command.setJunction(junction);
            command.execute();

            assertEquals(junction.getConfigurationIdx(), Junction.EAST_WEST_STRAIGHT_LINE);
        }

        assertEquals(junction.getConfigurationIdx(), Junction.EAST_WEST_STRAIGHT_LINE);
        assertTrue(junction.lightsOnDemand());
    }

    @Test
    public void testLightChangeWhenOneVehicleIsWaitingAndOneConfigurationMadeMoreThan20Steps() {
        setup();
        for (int i = 0; i < 40; i++) {
            junction.addVehicle(new Vehicle("", Direction.NORTH, Direction.SOUTH));
            junction.addVehicle(new Vehicle("", Direction.NORTH, Direction.WEST));
            junction.addVehicle(new Vehicle("", Direction.SOUTH, Direction.NORTH));
            junction.addVehicle(new Vehicle("", Direction.SOUTH, Direction.EAST));
        }
        for (int i = 0; i < 20; i++) {
            assertEquals(i, junction.getActiveConfig().getActiveSteps());
            junction.makeStep();
        }
        assertEquals(Junction.NORTH_SOUTH_STRAIGHT_LINE, junction.getConfigurationIdx());
        assertEquals(20, junction.getActiveConfig().getActiveSteps());
        assertEquals(Double.NEGATIVE_INFINITY, junction.getActiveConfig().getPriority());

        junction.addVehicle(new Vehicle("", Direction.EAST, Direction.WEST));
        junction.makeStep();
        assertEquals(Junction.EAST_WEST_STRAIGHT_LINE, junction.getConfigurationIdx());
        assertEquals(0, junction.getActiveConfig().getActiveSteps());
        assertEquals(Double.POSITIVE_INFINITY, junction.getActiveConfig().getPriority());
    }
}
